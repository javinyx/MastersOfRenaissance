package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.*;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.BadStorageException;
import it.polimi.ingsw.model.player.ProPlayer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.messages.MessageID.LORENZO_POSITION;

public class Controller implements Observer<MessageID> {

    //private int turnTime = 300; // max turn time in seconds
    //private Timer timer;
    private Game game;
    private boolean gameOver;
    private int numPlayer;
    private boolean initializationPhase;
    private List<LeaderCard> allLeaders = new ArrayList<>(); /*NIUBBI*/
    private List<ConcreteProductionCard> allProductionCards = new ArrayList<>();
    private List<Observer<MessageEnvelope>> remoteViews;
    Gson gson = new Gson();

    //player choices to create updateMessage
    Map<BiElement<Resource, Storage>, Integer> addedResources = new HashMap<>();
    Map<BiElement<Resource, Storage>, Integer> removedResources = new HashMap<>();
    boolean mustChoosePlacements = false;
    boolean basicActionDone = false;
    Optional<List<BiElement<Integer, Integer>>> boughtCard = Optional.empty();

    ProPlayer previousPlayer;

    public Controller() {
        initializationPhase = true;
        remoteViews = new ArrayList<>();
        initAllCards();

    }

    public boolean registerObserver(Observer<MessageEnvelope> obs) {
        return remoteViews.add(obs);
    }

    // GAME INITIALIZATION -------------------------------------------------------------------------------

    public void createSinglePlayerGame(String nickName) {
        MessageEnvelope envelope;

        numPlayer = 1;
        game = new SinglePlayerGame(this);
        List<BiElement<String, Integer>> turnNumber = new ArrayList<>();

        game.createPlayer(nickName);

        turnNumber.add(new BiElement<>(nickName, game.getCurrPlayer().getTurnID()));
        TurnNumberMessage msg = new TurnNumberMessage(turnNumber);
        envelope = new MessageEnvelope(MessageID.TURN_NUMBER, gson.toJson(msg, TurnNumberMessage.class));
        remoteViews.get(0).update(envelope);

        //Leader choice
        envelope = new MessageEnvelope(MessageID.CHOOSE_LEADER_CARDS, String.valueOf(game.leaderDistribution()));
        remoteViews.get(0).update(envelope);

        game.start(game.getCurrPlayer());
        initializationPhase = false;
    }

    public void createMultiplayerGame(List<String> players) {

        this.numPlayer = players.size();
        game = new MultiPlayerGame(this);
        MessageEnvelope envelope;
        List<BiElement<String, Integer>> turnNumber = new ArrayList<>();

        //Player Creation
        for (int i = 0; i < numPlayer; i++)
            game.createPlayer(players.get(i));

        for (ProPlayer p : ((MultiPlayerGame) game).getActivePlayers())
            turnNumber.add(new BiElement<>(p.getNickname(), p.getTurnID()));


        for (ProPlayer p : ((MultiPlayerGame) game).getActivePlayers()) {
            TurnNumberMessage msg = new TurnNumberMessage(turnNumber);
            envelope = new MessageEnvelope(MessageID.TURN_NUMBER, gson.toJson(msg, TurnNumberMessage.class));
            int curr = p.getTurnID() - 1;
            remoteViews.get(curr).update(envelope);

            //Leader choice
            envelope = new MessageEnvelope(MessageID.CHOOSE_LEADER_CARDS, String.valueOf(game.leaderDistribution()));
            remoteViews.get(curr).update(envelope);

            //Resource Choice
            if (curr == 1) {
                envelope = new MessageEnvelope(MessageID.CHOOSE_RESOURCE, String.valueOf(curr));
                remoteViews.get(curr).update(envelope);
            } else if (curr == 2 || curr == 3) {
                envelope = new MessageEnvelope(MessageID.CHOOSE_RESOURCE, String.valueOf(curr));
                remoteViews.get(curr).update(envelope);
                game.getCurrPlayer().moveOnBoard(1);
            }

        }
        for (ProPlayer p : ((MultiPlayerGame) game).getActivePlayers()){
            game.start(p);
            envelope = new MessageEnvelope(MessageID.UPDATE, p.getUpdate());
            remoteViews.get(p.getTurnID()-1).update(envelope);
        }

        initializationPhase = false;
        // TODO: ogni giocatore va informato della situazione degli altri 3

        //timer = new Timer(true);
        //timer.schedule(new TurnTimerTask(this, game.getCurrPlayer().getTurnType()),turnTime*1000);
    }

    public synchronized void chooseLeaderCards(String ids, String nick) {
        /*List<Integer> leadersIds = convertStringToListInteger(ids);
        List<LeaderCard> leaders = convertIdToLeaderCard(leadersIds);
        List<ProPlayer> allP = new ArrayList<>();
        ProPlayer pp;
        if(game instanceof MultiPlayerGame){
            allP = ((MultiPlayerGame)game).getPlayers();
        }
        for(ProPlayer p : allP){
            if(p.getNickname().equals(nick)){
                p.chooseLeaders(leaders);
                return;
            }
        }*/
    }

    // END GAME INITIALIZATION -----------------------------------------------------------------------------------------

    /**
     * If the player still has to choose where to put some resources, send back in info message beacuse the turn cannot be
     * ended right now. Otherwise send an end confirmation message (that causes the next player to become current) and
     * send all the updates of the things that has been done during the turn.
     */
    public synchronized void endTurn() {
        basicActionDone = false;
        if (mustChoosePlacements) {
            update(MessageID.INFO);
            return;
        }
        update(MessageID.CONFIRM_END_TURN);
        update(MessageID.UPDATE);
    }

    // TURN STRUCTURE --------------------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    /**
     * Invoke {@code buyFromMarket()} method in {@link ProPlayer} checking the player choice's correctness.
     * <p>If everything is fine, calls {@code update()} in order to generate a {@code MessageID.CHOOSE_PLACEMENTS_IN_STORAGE}
     * and ask the player where to store the resources bought, otherwise generate various error messages.</p>
     */
    public synchronized void buyFromMarAction(BuyMarketMessage buyMark) {
        if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }
        try {
            game.getCurrPlayer().buyFromMarket(buyMark.getDimension(), buyMark.getIndex(), buyMark.getMarbleUsage());
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            update(MessageID.BAD_DIMENSION_REQUEST);
        } catch (RuntimeException e) {
            update(MessageID.CARD_NOT_AVAILABLE);
        }

        mustChoosePlacements = true;
        basicActionDone = true;

        update(MessageID.STORE_RESOURCES);
    }

    public void buyProdCardAction(BuyProductionMessage buyProd) {
        if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }
        addedResources.clear();
        removedResources.clear();

        try {
            ConcreteProductionCard card = null;
            List<LeaderCard> leaderCards = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                if (game.getBuyableProductionCards().get(i).getId() == buyProd.getProdCardId())
                    card = game.getBuyableProductionCards().get(i);
                else {
                    update(MessageID.CARD_NOT_AVAILABLE);
                    return;
                }
            }

            for (int i = 0; i < buyProd.getLeader().size(); i++) {

                if (game.getCurrPlayer().getLeaderCards().get(0).getId() == buyProd.getLeader().get(i))
                    leaderCards.add(game.getCurrPlayer().getLeaderCards().get(0));
                if (game.getCurrPlayer().getLeaderCards().get(1).getId() == buyProd.getLeader().get(i))
                    leaderCards.add(game.getCurrPlayer().getLeaderCards().get(1));

            }

            if (card != null) {
                List<BiElement<Integer, Integer>> cards = new ArrayList<>();
                cards.add(new BiElement<>(card.getId(), buyProd.getStack()));
                boughtCard = Optional.of(cards);
                ResourcesWallet wallet = buyProd.getResourcesWallet();
                BiElement<Resource, Storage> elem;
                if (wallet.anyFromLootchestTray()) {
                    for (Resource r : wallet.getLootchestTray()) {
                        elem = new BiElement<>(r, Storage.LOOTCHEST);
                        removeResources(elem);
                    }
                }
                if (wallet.anyFromWarehouseTray()) {
                    for (Resource r : wallet.getWarehouseTray()) {
                        elem = new BiElement<>(r, Storage.WAREHOUSE);
                        removeResources(elem);
                    }
                }
                if (wallet.anyFromExtraStorage()) {
                    for (int index = wallet.extraStorageSize() - 1; index >= 0; index--) {
                        Storage extra = index == 0 ? Storage.EXTRA1 : Storage.EXTRA2;
                        for (Resource r : wallet.getExtraStorage(index)) {
                            elem = new BiElement<>(r, extra);
                            removeResources(elem);
                        }
                    }
                }
                game.getCurrPlayer().buyProductionCard(card, buyProd.getStack(), leaderCards, buyProd.getResourcesWallet());
            }

        } catch (BadStorageException e) {
            //player has no resources
            removedResources.clear();
            update(MessageID.BAD_PAYMENT_REQUEST);
        } catch (IllegalArgumentException e) {
            //prodcard have no cards
            removedResources.clear();
            update(MessageID.CARD_NOT_AVAILABLE);
        } catch (IndexOutOfBoundsException e) {
            //stack < 1 || stack > 3
            removedResources.clear();
            update(MessageID.WRONG_STACK_CHOICE);
        } catch (RuntimeException e) {
            //prodcard has wrong level
            removedResources.clear();
            update(MessageID.WRONG_LEVEL_REQUEST);
        }

        basicActionDone = true;
        update(MessageID.ACK);

    }

    public void activateProdAction(ProduceMessage produce) {
        if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }
        removedResources.clear();
        addedResources.clear();

        Optional<Resource> basicOut;
        if (produce.getBasicOutput() == null) {
            basicOut = Optional.empty();
        } else {
            basicOut = Optional.of(produce.getBasicOutput());
        }

        List<ConcreteProductionCard> cards = produce.getProductionCards();
        List<BoostAbility> extraPowerProd = produce.getLeaderCards();
        List<Resource> extraOutput = produce.getLeaderOutputs();
        ResourcesWallet wallet = produce.getResourcesWallet();
        BiElement<Resource, Storage> elem;
        try {
            if (cards != null && !cards.isEmpty() && wallet != null) {
                for (ConcreteProductionCard c : cards) {
                    for (Resource r : c.getProduction()) {
                        if (r.isValidForTrading()) {
                            elem = new BiElement<>(r, Storage.LOOTCHEST);
                            addResources(elem);
                        }
                    }
                }
                if (extraPowerProd != null && !extraPowerProd.isEmpty() && extraOutput != null && !extraOutput.isEmpty()) {
                    if (extraPowerProd.size() == extraOutput.size()) {
                        for (Resource r : extraOutput) {
                            elem = new BiElement<>(r, Storage.LOOTCHEST);
                            addResources(elem);
                        }
                    } else {
                        throw new BadStorageException();
                    }
                }

                if (produce.isBasicProduction() && basicOut.isPresent()) {
                    elem = new BiElement<>(basicOut.get(), Storage.LOOTCHEST);
                    addResources(elem);
                }
                if (wallet.anyFromLootchestTray()) {
                    for (Resource r : wallet.getLootchestTray()) {
                        elem = new BiElement<>(r, Storage.LOOTCHEST);
                        removeResources(elem);
                    }
                }
                if (wallet.anyFromWarehouseTray()) {
                    for (Resource r : wallet.getWarehouseTray()) {
                        elem = new BiElement<>(r, Storage.WAREHOUSE);
                        removeResources(elem);
                    }
                }
                if (wallet.anyFromExtraStorage()) {
                    for (int index = wallet.extraStorageSize() - 1; index >= 0; index--) {
                        Storage extra = index == 0 ? Storage.EXTRA1 : Storage.EXTRA2;
                        for (Resource r : wallet.getExtraStorage(index)) {
                            elem = new BiElement<>(r, extra);
                            removeResources(elem);
                        }
                    }
                }

                game.getCurrPlayer().startProduction(cards, wallet, extraPowerProd, extraOutput, produce.isBasicProduction(), basicOut);
            }
        } catch (BadStorageException e1) {
            //notify that there's something wrong with the player storage
            addedResources.clear();
            removedResources.clear();
            update(MessageID.BAD_PRODUCTION_REQUEST);
        } catch (RuntimeException e2) {
            //notify that some cards in production cards chosen by the player for this task cannot produce
            addedResources.clear();
            removedResources.clear();
            update(MessageID.CARD_NOT_AVAILABLE);
        }

        basicActionDone = true;
        update(MessageID.ACK);
    }

    // END TURN STRUCTURE ----------------------------------------------------------------------------------------------

    // TURN UTILITIES --------------------------------------------------------------------------------------------------

    public void activateLeader(LeaderCard leaderCard) {
        if (mustChoosePlacements) {
            update(MessageID.INFO);
            return;
        }

        for (int i = 0; i < ProPlayer.getMaxNumExtraStorage(); i++) {
            if (game.getCurrPlayer().getLeaderCards().get(i).equals(leaderCard)) {
                if (game.getCurrPlayer().activateLeaderCard(leaderCard)) {
                    update(MessageID.ACK);
                    return;
                } else {
                    update(MessageID.CARD_NOT_AVAILABLE);
                    return;
                }
            }
        }
        update(MessageID.CARD_NOT_AVAILABLE);
    }

    /**
     * Let the player discard a leader.
     */
    public void discardLeader(String s) {
        boolean found = false;
        List<LeaderCard> leaders = game.getCurrPlayer().getLeaderCards();
        for (LeaderCard card : leaders) {
            if (card.getId() == Integer.parseInt(s)) {
                if (game.getCurrPlayer().discardLeaderCard(card)) {
                    update(MessageID.PLAYERS_POSITION);
                    update(MessageID.ACK);
                    break;
                }

            }
        }
        update(MessageID.CARD_NOT_AVAILABLE);
    }

    public void organizeResourceAction(StoreResourcesMessage message) {
        if (!mustChoosePlacements) {
            //it doesn't have to store anything
            update(MessageID.INFO);
            return;
        }
        addedResources.clear();
        removedResources.clear();

        int discarding = 0;

        for (BiElement<Resource, Storage> element : message.getPlacements()) {
            switch (element.getSecondValue()) {
                case WAREHOUSE_SMALL -> {
                    if (game.getCurrPlayer().storeInWarehouse(element.getFirstValue(), 1)) {
                        addResources(element);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case WAREHOUSE_MID -> {
                    if (game.getCurrPlayer().storeInWarehouse(element.getFirstValue(), 2)) {
                        addResources(element);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case WAREHOUSE_LARGE -> {
                    if (game.getCurrPlayer().storeInWarehouse(element.getFirstValue(), 3)) {
                        addResources(element);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case EXTRA1 -> {
                    if (game.getCurrPlayer().storeInExtraStorage(element.getFirstValue(), game.getCurrPlayer().getExtraStorage().get(0))) {
                        addResources(element);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case EXTRA2 -> {
                    if (game.getCurrPlayer().storeInExtraStorage(element.getFirstValue(), game.getCurrPlayer().getExtraStorage().get(1))) {
                        addResources(element);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case DISCARD -> {
                    discarding++;
                }
                default -> {
                    addedResources.clear();
                    mustChoosePlacements = true;
                    update(MessageID.BAD_STORAGE_REQUEST);
                    return;
                }
            }
        }

        if (discarding > 0) {
            game.getCurrPlayer().discardResources(discarding);
            update(MessageID.PLAYERS_POSITION);
        }

        mustChoosePlacements = false;
        update(MessageID.ACK);

    }

    /**
     * If {@code element} is already contained in {@code addedResources}, then it increments its presence by 1.
     * Otherwise, it add the element to the map as a new occurrence with is value set at 1.
     */
    private void addResources(BiElement<Resource, Storage> element) {
        if (addedResources.containsKey(element)) {
            addedResources.compute(element, (k, v) -> v++);
        } else {
            addedResources.put(element, 1);
        }
    }

    private void addResources(BiElement<Resource, Storage> element, Integer qty) {
        if (qty == 0) {
            return;
        }
        if (addedResources.containsKey(element)) {
            addedResources.compute(element, (k, v) -> v + qty);
        } else {
            addedResources.put(element, qty);
        }
    }

    /**
     * If {@code element} is already contained in {@code addedResources}, then it decrements its presence by 1.
     * In order to do this, it increments the presence in {@code removedResources} map which is specific for this
     * purpose. Otherwise, it add the element to the map as a new occurrence with is value set at 1.
     */
    private void removeResources(BiElement<Resource, Storage> element) {
        if (removedResources.containsKey(element)) {
            removedResources.compute(element, (k, v) -> v++);
        } else {
            removedResources.put(element, 1);
        }
    }

    public String getCurrPlayerNick() {
        return game.getCurrPlayer().getNickname();
    }

    public int getCurrPlayerTurnID() {
        return game.getCurrPlayer().getTurnID();
    }

    // END TURN UTILITIES ----------------------------------------------------------------------------------------------

    /**
     * Generate an {@link EndTurnMessage} by setting the player that has just terminated the turn as {@code previousPlayer}
     * and cause the change of currentPlayer by calling {@code updateEndTurn()} from {@link it.polimi.ingsw.model.ModelObserver}.
     * <p>In the message, it stores the new player that has to play and the buyable production cards.</p>
     */
    private EndTurnMessage generateEndTurnMessage() {
        previousPlayer = game.getCurrPlayer();
        game.updateEndTurn(previousPlayer);

        return new EndTurnMessage(game.getCurrPlayer().getTurnID(), game.getBuyableProductionID());
    }

    /**
     * Generate an {@link UpdateMessage} by filling its attributes using {@code previousPlayer} set by {@code generateEndTurn()},
     * the (new) currentPlayer, some information retrieved from model, {@code addedResources} and {@code removedResources}.
     */
    private UpdateMessage generateUpdateMessage() {
        List<BiElement<Integer, Boolean>> thisPlayerActiveLeaders = new ArrayList<>();

        //production card bought set by: buyProductionCardAction
        //addedResources set by: organizeResourceAction,
        //removedResources set by:
        for (LeaderCard ld : previousPlayer.getLeaderCards()) {
            if (ld.isActive()) {
                thisPlayerActiveLeaders.add(new BiElement<>(ld.getId(), ld.isActive()));
            }
        }

        UpdateMessage msg = new UpdateMessage(previousPlayer.getTurnID(), previousPlayer.getCurrentPosition(), game.getCurrPlayer().getTurnID(),
                game.getMarket().getMarketBoard(), game.getMarket().getExtraMarble(), game.getBuyableProductionID(),
                boughtCard.orElse(null), thisPlayerActiveLeaders, addedResources, removedResources);

        return msg;
    }

    // ENVELOPE CREATOR ------------------------------------------------------------------------------------------------


    @Override
    public void update(MessageID messageID) {
        switch (messageID) {

            case ACK -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, String.valueOf(basicActionDone)));
            case CONFIRM_END_TURN -> {
                EndTurnMessage msg = generateEndTurnMessage();
                String payload = gson.toJson(msg, EndTurnMessage.class);
                remoteViews.get(previousPlayer.getTurnID() - 1).update(new MessageEnvelope(messageID, payload));
            }

            //INITIALIZATION

            case TOO_MANY_PLAYERS -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, ""));
            //case CHOOSE_LEADER_CARDS -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "You have to choose a leader card"));
            case CHOOSE_RESOURCE -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, String.valueOf(game.getCurrPlayer().getTurnID())));

            //GAME PHASES

            case CARD_NOT_AVAILABLE,
                    BAD_PRODUCTION_REQUEST,
                    BAD_PAYMENT_REQUEST,
                    BAD_DIMENSION_REQUEST,
                    WRONG_STACK_CHOICE,
                    WRONG_LEVEL_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, ""));

            case STORE_RESOURCES -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));

            case UPDATE -> {
                /*if (initializationPhase) {
                    remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getUpdate()));
                } else {*/
                    /*WARNING : in order to generate correctly an UpdateMessage with generateUpdateMessage(), a CONFIRM_END_TURN must be
                     *  sent because it causes a generateEndTurnMessage() that updates the previousPlayer and the currentPlayer */
                    UpdateMessage msg = generateUpdateMessage();
                    String payload = gson.toJson(msg, UpdateMessage.class);
                    MessageEnvelope envelope = new MessageEnvelope(MessageID.UPDATE, payload);
                    for (Observer<MessageEnvelope> obs : remoteViews) {
                        obs.updateFrom(envelope, previousPlayer.getNickname());
                    }
               // }
            }

            case LORENZO_POSITION -> {
                MessageEnvelope env = new MessageEnvelope(LORENZO_POSITION, String.valueOf(((SinglePlayerGame) game).getLorenzoPosition()));
                remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(env);
            }
            case PLAYERS_POSITION -> {
                List<ProPlayer> players = ((MultiPlayerGame) game).getActivePlayers();
                List<BiElement<Integer, Integer>> positions = new ArrayList<>();
                for (ProPlayer pp : players) {
                    BiElement<Integer, Integer> pos = new BiElement<>(pp.getTurnID(), pp.getCurrentPosition());
                    positions.add(pos);
                }
                PlayersPositionMessage msg = new PlayersPositionMessage(positions);
                String payload = gson.toJson(msg, PlayersPositionMessage.class);
                MessageEnvelope env = new MessageEnvelope(MessageID.PLAYERS_POSITION, payload);

                for (Observer<MessageEnvelope> obs : remoteViews) {
                    obs.update(env);
                }
            }
            case INFO -> remoteViews.get(getCurrPlayerTurnID() - 1).update(new MessageEnvelope(messageID, "Invalid operation now"));


            default -> System.out.println("No no no");
        }
    }

    @Override
    public void updateFrom(MessageID messageID, String nickname) {
        //nothing
    }

    protected List<Integer> convertStringToListInteger(String s) {
        return (new ArrayList<>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")))).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    protected List<LeaderCard> convertIdToLeaderCard(List<Integer> ids) {

        List<LeaderCard> leaders = (allLeaders.stream().filter(x -> ids.contains(x.getId()))
                .collect(Collectors.toList()));

        return leaders;

    }

    protected List<ConcreteProductionCard> convertIdToProductionCard(List<Integer> ids) {
        return allProductionCards.stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
    }

    protected ConcreteProductionCard convertIdToProductionCard(int id) {
        for (ConcreteProductionCard pc : allProductionCards) {
            if (pc.getId() == id)
                return pc;
        }
        return null;
    }


    // END ENVELOPE CREATOR --------------------------------------------------------------------------------------------

    /**
     * Eliminates a player
     * @param playerToEliminate nickname of the player to eliminate
     */
    /*synchronized void eliminatePlayer(String playerToEliminate){
        int indexPlayerToEliminate = (playerToEliminate==null) ? game.getCurrPlayer() : game.getPl(playerToEliminate);
        if (indexPlayerToEliminate==-1) return;
        if (indexPlayerToEliminategame.getCurrPlayer()) timer.cancel();
        boolean newTurnNeeded = game.getCurrentPlayer() == indexPlayerToEliminate; //true if the current player has lost or has decided to surrender
        game.eliminatePlayer(playerToEliminate);
        if (newTurnNeeded && !gameOver) initializeNewTurn();
    }*/

    /**
     * Checks if the given player is the current turn player
     * @param evt event received from the view
     * @return {@code true}, if the player source of the event is current turn's player
     */
    /*synchronized boolean checkCorrectPlayer(PropertyChangeEvent evt) {
        String fromPlayer=((View)evt.getSource()).getNickname();
        return game.getPlayerIndex(fromPlayer) == game.getCurrentPlayer();
    }*/

    /**
     * @return {@code true}, if the game already ended
     */
    synchronized boolean isGameOver() {
        return gameOver;
    }

    /**
     * sets the game as ended
     */
    synchronized void gameOver() {
        gameOver = true;
    }

    /**
     * Aborts game due to an early disconnection or error.
     */
    public synchronized void abortGame() {
        gameOver = true;
        //game.setRequest(GameMessagesToClient.ABORT_GAME.name());
    }

    /*/**
     * Called when timer expires, eliminates the player whose turn is over
     * @param turnType identifier for the turn the timer is referring to
     */
    /*synchronized void turnTimeOver(char turnType) {
        if (gameOver || turnType != this.game.getCurrPlayer().getTurnType()) return;
        if (initializationPhase == true){
            abortGame();
            return;
        }
        //game.setRequest(GameMessagesToClient.TIME_OVER.name());
        //eliminatePlayer(null);
    }

    /**
     * Resets the turn timer.
     */
    /*public void resetTimer() {
        timer.cancel();
        timer = new Timer(true);
        timer.schedule(new TurnTimerTask(this, game.getCurrPlayer().getTurnType()),turnTime*1000);
    }*/

    public void setInactivePlayer(String playerName) {
        if (game instanceof MultiPlayerGame) {
            ((MultiPlayerGame) game).removeFromActivePlayers(playerName);
        } else {
            //do nothing, unless we want reset the game instead of leaving it pending
            //timer and then disconnect?
        }
    }

    /**
     * Let the player with this {@code nickname} rejoin the multiplayer game he/she was in before a disconnection.
     */
    public void rejoin(MultiPlayerGame game, String nickname) {
        List<ProPlayer> allPlayers = game.getPlayers();
        int turnId = 0;
        ProPlayer player = null;
        for (ProPlayer p : allPlayers) {
            if (p.getNickname().equals(nickname)) {
                player = p;
                turnId = p.getTurnID();
                game.getActivePlayers().add(turnId - 1, p);
                break;
            }
        }
        if (player == null) {
            throw new RuntimeException("Why???");
        }

        List<BiElement<Integer, Integer>> prodCards = new ArrayList<>();
        prodCards.addAll(player.getProdCards1().stream().map(x -> new BiElement<>(x.getId(), 1)).collect(Collectors.toList()));
        prodCards.addAll(player.getProdCards2().stream().map(x -> new BiElement<>(x.getId(), 2)).collect(Collectors.toList()));
        prodCards.addAll(player.getProdCards3().stream().map(x -> new BiElement<>(x.getId(), 3)).collect(Collectors.toList()));

        if (!mustChoosePlacements) {
            addedResources.clear();
        }
        Resource resource;
        if ((resource = player.getWarehouse().getSmallInventory()) != null) {
            addResources(new BiElement<>(resource, Storage.WAREHOUSE_SMALL));
        }
        for (Resource res : player.getWarehouse().getMidInventory()) {
            addResources(new BiElement<>(res, Storage.WAREHOUSE_MID));
        }
        for (Resource res : player.getWarehouse().getLargeInventory()) {
            addResources(new BiElement<>(res, Storage.WAREHOUSE_LARGE));
        }
        Map<Resource, Integer> inventory = player.getLootChest().getInventory();
        addResources(new BiElement<>(Resource.SERVANT, Storage.LOOTCHEST), inventory.get(Resource.SERVANT));
        addResources(new BiElement<>(Resource.COIN, Storage.LOOTCHEST), inventory.get(Resource.COIN));
        addResources(new BiElement<>(Resource.SHIELD, Storage.LOOTCHEST), inventory.get(Resource.SHIELD));
        addResources(new BiElement<>(Resource.STONE, Storage.LOOTCHEST), inventory.get(Resource.STONE));


        List<LeaderCard> leaders = player.getLeaderCards();
        List<BiElement<Integer, Boolean>> leadersIds = new ArrayList<>();
        int qty = 0, size;

        for (LeaderCard l : leaders) {
            leadersIds.add(new BiElement<>(l.getId(), l.isActive()));
            qty++;
            if (l.isActive() && (l instanceof StorageAbility) && (size = ((StorageAbility) l).size()) > 0) {
                addResources(new BiElement<>(((StorageAbility) l).getStorageType(), qty == 1 ? Storage.EXTRA1 : Storage.EXTRA2), size);
            }
        }

        UpdateMessage msg = new UpdateMessage(turnId, player.getCurrentPosition(), getCurrPlayerTurnID(),
                game.getMarket().getMarketBoard(), game.getMarket().getExtraMarble(), game.getBuyableProductionID(),
                prodCards, leadersIds, addedResources, removedResources);

        //TODO: ha creato il messaggio di update ma come glielo mandiamo?
    }

    public void rejoin(SinglePlayerGame game, String nickname) {
        //TODO: che politica usiamo per il rejoin del single player?
    }

    public Game getGame() {
        return game;
    }


    private void initAllCards() {

        String prodPath = "/json/ProductionCards.json",
                leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
                leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
                leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
                leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
                tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
                tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
                tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";

        Gson gson = new Gson();
        Reader reader;

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(prodPath)));
        allProductionCards = gson.fromJson(reader, new TypeToken<List<ConcreteProductionCard>>() {
        }.getType());

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadDiscountPath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<DiscountAbility>>() {
        }.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadStoragePath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<StorageAbility>>() {
        }.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadMarblePath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<MarbleAbility>>() {
        }.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadBoostPath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<BoostAbility>>() {
        }.getType()));
    }
}