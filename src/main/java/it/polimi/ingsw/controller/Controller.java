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
import it.polimi.ingsw.exception.BadStorageException;
import it.polimi.ingsw.model.player.ProPlayer;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static it.polimi.ingsw.messages.MessageID.*;

public class Controller implements Observer<MessageID> {

    private Game game;
    private List<LeaderCard> allLeaders = new ArrayList<>();
    private List<ConcreteProductionCard> allProductionCards = new ArrayList<>();
    private List<Observer<MessageEnvelope>> remoteViews;

    private ProPlayer playerToAck;
    private int numPlayer;

    Gson gson = new Gson();

    private boolean gameOver;
    private boolean mustChoosePlacements = false;
    private boolean basicActionDone = false;
    private boolean initializationPhase;

    /**
     * The following attributes are needed for generating the UpdateMessage. They're filled during the player turn and cleared everytime
     * the turn is over to collect next player data.
     */
    Map<BiElement<Resource, Storage>, Integer> addedResources = new HashMap<>();
    Map<BiElement<Resource, Storage>, Integer> removedResources = new HashMap<>();
    Optional<List<BiElement<Integer, Integer>>> boughtCard = Optional.empty();
    ProPlayer previousPlayer;

    public Controller() {
        initializationPhase = true;
        remoteViews = new ArrayList<>();
        initAllCards();
    }

    // GETTER & SETTER --------------------------------------------------------------------------------------

    public String getCurrPlayerNick() {return game.getCurrPlayer().getNickname();}

    public int getCurrPlayerTurnID() {return game.getCurrPlayer().getTurnID();}

    public boolean registerObserver(Observer<MessageEnvelope> obs) {
        return remoteViews.add(obs);
    }

    /**
     * @return {@code true}, if the game already ended
     */
    synchronized boolean isGameOver() {return gameOver;}

    /**
     * sets the game as ended
     */
    synchronized void gameOver() {gameOver = true;}

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
        envelope = new MessageEnvelope(MessageID.UPDATE, game.getCurrPlayer().getUpdate());
        remoteViews.get(game.getCurrPlayer().getTurnID()-1).update(envelope);

        game.getCurrPlayer().setInitializationPhase(false);
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

        int curr;
        for (ProPlayer p : ((MultiPlayerGame) game).getActivePlayers()) {
            if(p.getTurnID() == 1)
                p.setInitializationPhase(false);
            else
                p.setInitializationPhase(true);
            TurnNumberMessage msg = new TurnNumberMessage(turnNumber);
            envelope = new MessageEnvelope(MessageID.TURN_NUMBER, gson.toJson(msg, TurnNumberMessage.class));
            curr = p.getTurnID() - 1;
            remoteViews.get(curr).update(envelope);

            //Leader choice
            envelope = new MessageEnvelope(MessageID.CHOOSE_LEADER_CARDS, String.valueOf(game.leaderDistribution()));
            remoteViews.get(curr).update(envelope);

            //Resource Choice
            if (curr == 1 || curr == 2) {
                envelope = new MessageEnvelope(MessageID.CHOOSE_RESOURCE, String.valueOf(1));
                remoteViews.get(curr).update(envelope);
            } else if (curr == 3) {
                envelope = new MessageEnvelope(MessageID.CHOOSE_RESOURCE, String.valueOf(2));
                remoteViews.get(curr).update(envelope);
            }
            if(curr==2 || curr==3){
                p.moveOnBoard(1);
            }
            if(curr==0){
                game.start(p);
                envelope = new MessageEnvelope(MessageID.UPDATE, p.getUpdate());
                remoteViews.get(curr).update(envelope);
            }

        }
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
        //update(MessageID.CONFIRM_END_TURN);
        //parte nuova
        previousPlayer = game.getCurrPlayer();
        game.updateEndTurn(previousPlayer);
        //fine
        update(MessageID.UPDATE);
    }

    // TURN STRUCTURE --------------------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    public synchronized void buyProdCardAction(BuyProductionMessage buyProd) {
        /*if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }*/
        addedResources.clear();
        removedResources.clear();

        try {
            ConcreteProductionCard card = null;
            List<LeaderCard> leaderCards = new ArrayList<>();
            boolean found = false;

            for (int i = 0; i < 12; i++) {
                if (game.getBuyableProductionCards().get(i).getId() == buyProd.getProdCardId()) {
                    found = true;
                    card = game.getBuyableProductionCards().get(i);
                }
            }

            if(!found){
                update(CARD_NOT_AVAILABLE);
                return;
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
                        removeResources(elem, 1);
                    }
                }
                if (wallet.anyFromWarehouseTray()) {
                    for (Resource r : wallet.getWarehouseTray()) {
                        elem = new BiElement<>(r, Storage.WAREHOUSE);
                        removeResources(elem, 1);
                    }
                }
                if (wallet.anyFromExtraStorage()) {
                    for (int index = wallet.extraStorageSize() - 1; index >= 0; index--) {
                        Storage extra = index == 0 ? Storage.EXTRA1 : Storage.EXTRA2;
                        for (Resource r : wallet.getExtraStorage(index)) {
                            elem = new BiElement<>(r, extra);
                            removeResources(elem, 1);
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
        playerToAck = game.getCurrPlayer();
        update(MessageID.ACK);
    }

    /**
     * Let the player play the produce phase accordingly to the {@code produce} message's content (see {@link ProduceMessage}).
     */
    public synchronized void activateProdAction(ProduceMessage produce) {
        /*if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }*/
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
                            addResources(elem, 1);
                        }
                    }
                }
                if (extraPowerProd != null && !extraPowerProd.isEmpty() && extraOutput != null && !extraOutput.isEmpty()) {
                    if (extraPowerProd.size() == extraOutput.size()) {
                        for (Resource r : extraOutput) {
                            elem = new BiElement<>(r, Storage.LOOTCHEST);
                            addResources(elem, 1);
                        }
                    } else {
                        throw new BadStorageException();
                    }
                }

                if (produce.isBasicProduction() && basicOut.isPresent()) {
                    elem = new BiElement<>(basicOut.get(), Storage.LOOTCHEST);
                    addResources(elem, 1);
                }
                if (wallet.anyFromLootchestTray()) {
                    for (Resource r : wallet.getLootchestTray()) {
                        elem = new BiElement<>(r, Storage.LOOTCHEST);
                        removeResources(elem, 1);
                    }
                }
                if (wallet.anyFromWarehouseTray()) {
                    for (Resource r : wallet.getWarehouseTray()) {
                        elem = new BiElement<>(r, Storage.WAREHOUSE);
                        removeResources(elem,1 );
                    }
                }
                if (wallet.anyFromExtraStorage()) {
                    for (int index = wallet.extraStorageSize() - 1; index >= 0; index--) {
                        Storage extra = index == 0 ? Storage.EXTRA1 : Storage.EXTRA2;
                        for (Resource r : wallet.getExtraStorage(index)) {
                            elem = new BiElement<>(r, extra);
                            removeResources(elem, 1);
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

    /**
     * Invoke {@code buyFromMarket()} method in {@link ProPlayer} checking the player choice's correctness.
     * <p>If everything is fine, calls {@code update()} in order to generate a {@code MessageID.CHOOSE_PLACEMENTS_IN_STORAGE}
     * and ask the player where to store the resources bought, otherwise generate various error messages.</p>
     */
    public synchronized void buyFromMarAction(BuyMarketMessage buyMark) {
        /*if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }*/
        playerToAck = game.getCurrPlayer();
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

    // END TURN STRUCTURE ----------------------------------------------------------------------------------------------

    // TURN UTILITIES --------------------------------------------------------------------------------------------------

    /**
     * Let the player choose where to store the resources or even discard them accordingly to the {@code message}'s
     * content (see {@link StoreResourcesMessage}).
     * @param message
     */
    public synchronized void organizeResourceAction(StoreResourcesMessage message) {
        /*if (!mustChoosePlacements) {
            //it doesn't have to store anything
            update(MessageID.INFO);
            return;
        }*/
        int playerID = message.getTurnID();
        for(ProPlayer p : game.getPlayers()){
            if(p.getTurnID() == playerID){
                playerToAck = p;
            }
        }
        //playerForOrganizeRes = game.getCurrPlayer();

        if (game instanceof MultiPlayerGame)
            for(ProPlayer p : ((MultiPlayerGame) game).getActivePlayers())
                if(p.getTurnID() == message.getTurnID())
                    playerToAck = p;

        addedResources.clear();
        removedResources.clear();

        int discarding = 0;

        for (BiElement<Resource, Storage> element : message.getPlacements()) {
            switch (element.getSecondValue()) {
                case WAREHOUSE_SMALL -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 1)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case WAREHOUSE_MID -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 2)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case WAREHOUSE_LARGE -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 3)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case EXTRA1 -> {
                    if (playerToAck.storeInExtraStorage(element.getFirstValue(), playerToAck.getExtraStorage().get(0))) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        update(MessageID.BAD_STORAGE_REQUEST);
                        return;
                    }
                }
                case EXTRA2 -> {
                    if (playerToAck.storeInExtraStorage(element.getFirstValue(), playerToAck.getExtraStorage().get(1))) {
                        addResources(element, 1);
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
            playerToAck.discardResources(discarding);
        }

        mustChoosePlacements = false;
        if(!playerToAck.isInitializationPhase())
            update(MessageID.ACK);
        else{
            game.start(playerToAck);
            MessageEnvelope envelope = new MessageEnvelope(MessageID.UPDATE, playerToAck.getUpdate());
            remoteViews.get(playerToAck.getTurnID()-1).update(envelope);
            playerToAck.setInitializationPhase(false);
        }
    }

    public synchronized void chooseLeaderCards(String ids, String nick) {
        List<Integer> leadersIds = convertStringToListInteger(ids);
        List<LeaderCard> leaders = convertIdToLeaderCard(leadersIds);
        List<ProPlayer> allP;
        allP = game.getPlayers();

        for(ProPlayer p : allP){
            if(p.getNickname().equals(nick)){
                p.chooseLeaders(leaders);
                return;
            }
        }
    }

    /**
     * @param leaderCard the card to be activated.
     */
    public synchronized void activateLeader(LeaderCard leaderCard) {
        /*if (mustChoosePlacements) {
            update(MessageID.INFO);
            return;
        }*/

        for (int i = 0; i < ProPlayer.getMaxNumExtraStorage(); i++) {
            if (game.getCurrPlayer().getLeaderCards().get(i).equals(leaderCard)) {
                if (game.getCurrPlayer().activateLeaderCard(leaderCard)) {
                    update(MessageID.ACK);
                    return;
                } else {
                    update(LEADER_NOT_ACTIVABLE);
                    return;
                }
            }
        }
        update(MessageID.CARD_NOT_AVAILABLE);
    }

    /**
     * Let the player discard a leader.
     */
    public synchronized void discardLeader(String s) {
        System.out.println("discardin'n'pimpin \n" + s);
        playerToAck = game.getCurrPlayer();
        List<LeaderCard> leaders = playerToAck.getLeaderCards();
        for (LeaderCard card : leaders) {
            if (card.getId() == Integer.parseInt(s)) {
                if (playerToAck.discardLeaderCard(card)) {
                    update(MessageID.PLAYERS_POSITION);
                    System.out.println(basicActionDone);
                    update(MessageID.ACK);
                }else{
                    update(MessageID.CARD_NOT_AVAILABLE);
                }
                break;
            }
        }
    }

    /**
     * If {@code element} is already contained in {@code addedResources}, then it increments its presence by 1.
     * Otherwise, it add the element to the map as a new occurrence with is value set at 1.
     */
    private synchronized void addResources(BiElement<Resource, Storage> element, Integer qty) {
        if (qty == 0) {
            return;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        addedResources.forEach((x,y) -> {
            if (x.equals(element)) {
                found.set(true);
                addedResources.compute(x, (k,v) -> v + qty);
            }
        });

        if(!found.get()){
            addedResources.put(element, qty);
        }
    }

    /**
     * If {@code element} is already contained in {@code addedResources}, then it decrements its presence by {@code qty}.
     * In order to do this, it increments the presence in {@code removedResources} map which is specific for this
     * purpose. Otherwise, it add the element to the map as a new occurrence with is value set at {@code qty}.
     */
    private synchronized void removeResources(BiElement<Resource, Storage> element, Integer qty) {
        if(qty == 0){
            return;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        removedResources.forEach((x,y) -> {
            if (x.equals(element)) {
                found.set(true);
                removedResources.compute(x, (k,v) -> v + qty);
            }
        });

        if(!found.get()){
            removedResources.put(element, qty);
        }
    }

    // END TURN UTILITIES ----------------------------------------------------------------------------------------------

    /**
     * Generate an {@link EndTurnMessage} by setting the player that has just terminated the turn as {@code previousPlayer}
     * and cause the change of currentPlayer by calling {@code updateEndTurn()} from {@link it.polimi.ingsw.model.ModelObserver}.
     * <p>In the message, it stores the new player that has to play and the buyable production cards.</p>
     */
    private synchronized EndTurnMessage generateEndTurnMessage() {
        basicActionDone = false;
        previousPlayer = game.getCurrPlayer();
        game.updateEndTurn(previousPlayer);

        return new EndTurnMessage(game.getCurrPlayer().getTurnID(), game.getBuyableProductionID());
    }

    /**
     * Generate an {@link UpdateMessage} by filling its attributes using {@code previousPlayer} set by {@code generateEndTurn()},
     * the (new) currentPlayer, some information retrieved from model, {@code addedResources} and {@code removedResources}.
     */
    private synchronized UpdateMessage generateUpdateMessage() {
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

        msg.setSerializedResources();

        return msg;
    }

    // ENVELOPE CREATOR ------------------------------------------------------------------------------------------------


    @Override
    public synchronized void update(MessageID messageID) {
        switch (messageID) {

            case ACK -> remoteViews.get(playerToAck.getTurnID() - 1).update(new MessageEnvelope(messageID, String.valueOf(basicActionDone)));
            case CONFIRM_END_TURN -> {
                System.err.println("CONFIRM_END_TURN: non dovremmo più essere qui");
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
                    LEADER_NOT_ACTIVABLE,
                    WRONG_LEVEL_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, ""));

            case STORE_RESOURCES -> remoteViews.get(playerToAck.getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));

            case UPDATE -> {
                /*if (initializationPhase) {
                    remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getUpdate()));
                } else {*/
                /*WARNING : in order to generate correctly an UpdateMessage with generateUpdateMessage(), a CONFIRM_END_TURN must be
                 *  sent because it causes a generateEndTurnMessage() that updates the previousPlayer and the currentPlayer */
                UpdateMessage msg = generateUpdateMessage();
                MessageEnvelope envelope = new MessageEnvelope(MessageID.UPDATE, gson.toJson(msg, UpdateMessage.class));
                for (Observer<MessageEnvelope> obs : remoteViews) {
                    //modifica: sostituzione updateFrom -> update normale
                    obs.update(envelope);
                    //obs.updateFrom(envelope, previousPlayer.getNickname());
                }
                // }
            }

            case LORENZO_POSITION -> {
                MessageEnvelope env = new MessageEnvelope(LORENZO_POSITION, String.valueOf(((SinglePlayerGame) game).getLorenzoPosition()));
                remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(env);
            }
            case PLAYERS_POSITION -> {
                List<ProPlayer> players = game.getPlayers();
                List<BiElement<Integer, Integer>> positions = new ArrayList<>();
                for (ProPlayer pp : players) {
                    BiElement<Integer, Integer> pos = new BiElement<>(pp.getTurnID(), pp.getCurrentPosition());
                    positions.add(pos);
                }
                if(game instanceof SinglePlayerGame){
                    positions.add(new BiElement<>(0, ((SinglePlayerGame) game).getLorenzoPosition()));
                }
                PlayersPositionMessage msg = new PlayersPositionMessage(positions);
                String payload = gson.toJson(msg, PlayersPositionMessage.class);
                MessageEnvelope env = new MessageEnvelope(MessageID.PLAYERS_POSITION, payload);

                for (Observer<MessageEnvelope> obs : remoteViews) {
                    obs.update(env);
                }
            }

            case VATICAN_REPORT -> {
                List<BiElement<Integer,Boolean>> vaticanReportStatus = new ArrayList<>();
                int numReport = game.getReportTriggered();
                for(ProPlayer p : game.getPlayers()){
                    vaticanReportStatus.add(new BiElement<>(p.getTurnID(), p.getPopePassStatus(numReport)));
                }
                VaticanReportMessage msg = new VaticanReportMessage(game.getTriggerPlayerForReport().getNickname(),
                        numReport, vaticanReportStatus);
                String paylaod = gson.toJson(msg, VaticanReportMessage.class);
                MessageEnvelope envelope = new MessageEnvelope(VATICAN_REPORT, paylaod);
                for(Observer<MessageEnvelope> obs : remoteViews){
                    obs.update(envelope);
                }
            }
            case INFO -> remoteViews.get(getCurrPlayerTurnID() - 1).update(new MessageEnvelope(messageID, "Invalid operation now"));


            default -> System.out.println("No no no");
        }
    }

    protected List<Integer> convertStringToListInteger(String s) {
        return (new ArrayList<>(Arrays.asList(s.substring(1, s.length() - 1).split(", ")))).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public List<LeaderCard> convertIdToLeaderCard(List<Integer> ids) {

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
     * Aborts game due to an early disconnection or error.
     */
    public synchronized void abortGame() {
        gameOver = true;
        //game.setRequest(GameMessagesToClient.ABORT_GAME.name());
    }

    // REJOIN PART ---------------------------------------------------------

    public void setInactivePlayer(String playerName) {
        if (game instanceof MultiPlayerGame) {
            ((MultiPlayerGame) game).removeFromActivePlayers(playerName);
        } else {
            //do nothing, unless we want reset the game instead of leaving it pending
            //disconnect?
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
            addResources(new BiElement<>(resource, Storage.WAREHOUSE_SMALL), 1);
        }
        for (Resource res : player.getWarehouse().getMidInventory()) {
            addResources(new BiElement<>(res, Storage.WAREHOUSE_MID), 1);
        }
        for (Resource res : player.getWarehouse().getLargeInventory()) {
            addResources(new BiElement<>(res, Storage.WAREHOUSE_LARGE), 1);
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

        //TODO: ha creato il messaggio di update ma come glielo mandiamo? Con il metodo update
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