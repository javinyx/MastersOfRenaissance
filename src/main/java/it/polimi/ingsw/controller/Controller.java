package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.exception.BadStorageException;
import it.polimi.ingsw.exception.WrongLevelException;
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
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.player.Warehouse;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static it.polimi.ingsw.messages.MessageID.*;

public class Controller implements Observer<MessageID> {

    protected Game game;
    private List<LeaderCard> allLeaders = new ArrayList<>();
    private List<ConcreteProductionCard> allProductionCards = new ArrayList<>();
    private List<Observer<MessageEnvelope>> remoteViews;
    private List<Integer> infoTokenLorenzo = new ArrayList<>();

    private ProPlayer playerToAck;
    private Player winner;
    private boolean endPhase = false;
    private int playersLeftToPlay = 0;
    private int numPlayer;
    private int startCounter = 0;
    private int playerLeaderDoneCtr = 0;

    Gson gson = new Gson();

    private boolean gameOver;
    private boolean mustChoosePlacements = false;
    private boolean basicActionDone = false;
    private boolean initializationPhase;
    private ActionToken act;

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

    public List<Observer<MessageEnvelope>> getRemoteViews() {return remoteViews;}

    public String getCurrPlayerNick() {return game.getCurrPlayer().getNickname();}

    public boolean isEndPhase(){return endPhase;}

    public int getCurrPlayerTurnID() {return game.getCurrPlayer().getTurnID();}

    public boolean registerObserver(Observer<MessageEnvelope> obs) {
        return remoteViews.add(obs);
    }

    /**
     * @return {@code true}, if the game already ended
     */
    synchronized boolean isGameOver() {return gameOver;}

    /**
     * Sets the game as ended
     */
    synchronized void gameOver() {gameOver = true;}

    // GAME INITIALIZATION -------------------------------------------------------------------------------

    /**
     * Create a single player game and the player, assign the player his turn number and let him choose the leaders
     * @param nickName the nick of the player
     */
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
    }

    /**
     * After {@link #createSinglePlayerGame(String)} create the update message and send it to the player.
     * Then terminate the initialization phase.
     */
    public void continueStart(){
        MessageEnvelope envelope;
        game.start(game.getCurrPlayer());
        game.getCurrPlayer().setInitializationPhase(false);

        envelope = new MessageEnvelope(MessageID.UPDATE, game.getCurrPlayer().getUpdate());
        remoteViews.get(game.getCurrPlayer().getTurnID()-1).update(envelope);

        initializationPhase = false;
    }

    /**
     * Create a multiplayer game and the players, assign all the player the respective turn number and let him choose the leaders,
     * then he receive the update message of all the other player to update the first leader and resources choice
     * @param players the list of the nicks' players
     */
    public synchronized void createMultiplayerGame(List<String> players) {

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
        /*if(!basicActionDone){
            addedResources.clear();
            removedResources.clear();
            boughtCard = Optional.empty();
        }*/
        basicActionDone = false;
        if (mustChoosePlacements) {
            update(MessageID.INFO);
            return;
        }

        if(endPhase && playersLeftToPlay>0){
            playersLeftToPlay--;
            System.out.println("END TURN 1: " + playersLeftToPlay);
        }else if(endPhase){
            winner = game.countFinalPointsAndWinner();
            System.out.println("END TURN 2");
            update(PLAYER_WIN);
            return;
        }
        if (game.getTotalPlayers() == 1) {
            infoTokenLorenzo.clear();
            act = ((SinglePlayerGame) game).drawActionToken();

            update(LORENZO_POSITION);
        }

        update(CONFIRM_END_TURN);
        //fine
        //update(MessageID.UPDATE);
        addedResources.clear();
        removedResources.clear();
        boughtCard = Optional.empty();
        previousPlayer = game.getCurrPlayer();
    }

    // TURN STRUCTURE --------------------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    /**
     * Let the currentPlayer buy the single production card specified by the message.
     * <li>It checks if the selected card is available for purchase, if not it throws a
     * {@code CARD_NOT_AVAILABLE};</li>
     * <li>If the stack chosen is not suited for that card accordingly to the rules implemented by the model,
     * send {@code WRONG_STACK_CHOICE} or {@code WRONG_LEVEL_REQUEST};</li>
     * <li>If the payment foreseen by the player is not adequate for the card, send a
     * {@code BAD_PAYMENT_REQUEST};</li>
     * <li>Otherwise: {@code ACK}.</li>
     * Related: {@link MessageID}
     * @param buyProdMsg the message that carries all the information about: card, payment, production stack.
     */
    public synchronized void buyProdCardAction(BuyProductionMessage buyProdMsg) {
        /*if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }*/
        boolean t = false;
        addedResources.clear();
        removedResources.clear();

        try {
            ConcreteProductionCard card = null;
            List<LeaderCard> leaderCards = new ArrayList<>();
            List<ConcreteProductionCard> buyableProdCard = new ArrayList<>(game.getBuyableProductionCards());

            for (int i = 0; i < buyableProdCard.size(); i++) {
                if (buyableProdCard.get(i).getId() == buyProdMsg.getProdCardId()) {
                    card = game.getBuyableProductionCards().get(i);
                }
            }

            if(card==null){
                update(CARD_NOT_AVAILABLE);
                return;
            }


            for (int i = 0; i < buyProdMsg.getLeader().size(); i++) {

                if (game.getCurrPlayer().getLeaderCards().get(0).getId() == buyProdMsg.getLeader().get(i))
                    leaderCards.add(game.getCurrPlayer().getLeaderCards().get(0));
                if (game.getCurrPlayer().getLeaderCards().get(1).getId() == buyProdMsg.getLeader().get(i))
                    leaderCards.add(game.getCurrPlayer().getLeaderCards().get(1));

            }

            List<BiElement<Integer, Integer>> cards = new ArrayList<>();
            cards.add(new BiElement<>(card.getId(), buyProdMsg.getStack()));
            boughtCard = Optional.of(cards);
            game.getCurrPlayer().buyProductionCard(card, buyProdMsg.getStack(), leaderCards, buyProdMsg.getResourcesWallet());

        } catch (BadStorageException e) {
            //player has no resources
            removedResources.clear();
            boughtCard = Optional.empty();
            update(MessageID.BAD_PAYMENT_REQUEST);
            t = true;
        } catch (IllegalArgumentException e) {
            //prodcard have no cards
            removedResources.clear();
            boughtCard = Optional.empty();
            update(MessageID.CARD_NOT_AVAILABLE);
            t = true;
        } catch (IndexOutOfBoundsException e) {
            //stack < 1 || stack > 3
            removedResources.clear();
            boughtCard = Optional.empty();
            update(MessageID.WRONG_STACK_CHOICE);
            t = true;
        } catch (WrongLevelException e) {
            //prodcard has wrong level
            System.out.println("card id: " + buyProdMsg.getProdCardId());
            removedResources.clear();
            boughtCard = Optional.empty();
            update(MessageID.WRONG_LEVEL_REQUEST);
            t = true;
        }

        if (!t){
            basicActionDone = true;
            playerToAck = game.getCurrPlayer();
            update(UPDATE);
            update(MessageID.ACK);
        }
    }

    /**
     * Let the player play the produce phase accordingly to the {@code produce} message's content (see {@link ProduceMessage}).
     */
    public synchronized void activateProdAction(ProduceMessage produce) {
        /*if (mustChoosePlacements || basicActionDone) {
            update(MessageID.INFO);
            return;
        }*/
        //boolean t = false;
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
            if ((cards != null && !cards.isEmpty() || produce.isBasicProduction()) && wallet != null) {
                game.getCurrPlayer().startProduction(cards, wallet, extraPowerProd, extraOutput, produce.isBasicProduction(), basicOut);
            }
        } catch (BadStorageException e1) {
            //notify that there's something wrong with the player storage
            addedResources.clear();
            removedResources.clear();
            update(MessageID.BAD_PRODUCTION_REQUEST);
            return;
        } catch (RuntimeException e2) {
            //notify that some cards in production cards chosen by the player for this task cannot produce
            addedResources.clear();
            removedResources.clear();
            e2.printStackTrace();
            update(MessageID.CARD_NOT_AVAILABLE);
            return;
        }


            basicActionDone = true;
            update(UPDATE);
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
        boolean t = false;
        playerToAck = game.getCurrPlayer();
        try {
            game.getCurrPlayer().buyFromMarket(buyMark.getDimension(), buyMark.getIndex(), buyMark.getMarbleUsage());
        } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
            update(MessageID.BAD_DIMENSION_REQUEST);
            t = true;
        } catch (RuntimeException e) {
            update(MessageID.CARD_NOT_AVAILABLE);
            t = true;
        }

        if (!t){
            mustChoosePlacements = true;
            basicActionDone = true;

            update(MessageID.STORE_RESOURCES);
        }
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
        Warehouse warBackUp = new Warehouse(playerToAck.getWarehouse());
        List<StorageAbility> storeBackUp = new ArrayList<>(playerToAck.getExtraStorage());



        int discarding = 0;

        for (BiElement<Resource, Storage> element : message.getPlacements()) {
            switch (element.getSecondValue()) {
                case WAREHOUSE_SMALL -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 1)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        playerToAck.setWarehouse(warBackUp);
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
                        playerToAck.setWarehouse(warBackUp);
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
                        playerToAck.setWarehouse(warBackUp);
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
                        playerToAck.resetExtraStorage(storeBackUp);
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
                        playerToAck.resetExtraStorage(storeBackUp);
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

        if(!playerToAck.isInitializationPhase()) {
            update(UPDATE);
            update(MessageID.ACK);
        }else{
            game.start(playerToAck);
            MessageEnvelope envelope = new MessageEnvelope(MessageID.UPDATE, playerToAck.getUpdate());
            for(ProPlayer pp : ((MultiPlayerGame) game).getActivePlayers()){
                    remoteViews.get(pp.getTurnID()-1).update(envelope);
            }
            addedResources.clear();
            removedResources.clear();
            playerToAck.setInitializationPhase(false);
            startCounter++;
            if(startCounter == game.getTotalPlayers()-1 && game.getTotalPlayers() != 1 && playerLeaderDoneCtr==game.getTotalPlayers()){
                MessageEnvelope envelope1 = new MessageEnvelope(MessageID.START_INITIAL_GAME, "");
                for(ProPlayer pp : ((MultiPlayerGame) game).getActivePlayers()){
                    remoteViews.get(pp.getTurnID()-1).update(envelope1);
                }
            }
        }
    }

    /**
     * Rearrange all the resources in the player's warehouse removing everything currently present and replacing them
     * in the correct shelves as indicated by the {@code msg}. It doesn't check if the message is well formed or is exhaustive
     * on how to refurnish the warehouse.
     * @param msg has to contain all the resources previously held by the player with the new arrangement
     */
    public synchronized void rearrangeWarehouse(StoreResourcesMessage msg){

        Warehouse warBackup;

        int playerID = msg.getTurnID();
        for(ProPlayer p : game.getPlayers()){
            if(p.getTurnID() == playerID){
                playerToAck = p;
            }
        }
        //playerForOrganizeRes = game.getCurrPlayer();
        if (game instanceof MultiPlayerGame)
            for(ProPlayer p : ((MultiPlayerGame) game).getActivePlayers())
                if(p.getTurnID() == msg.getTurnID())
                    playerToAck = p;

        addedResources.clear();
        removedResources.clear();

        if(playerToAck.getWarehouse().getLargeInventory() != null)
            for (int i = 0; i < playerToAck.getWarehouse().getLargeInventory().size(); i++) {
                removeResources(new BiElement<>(playerToAck.getWarehouse().getLargeInventory().get(i), Storage.WAREHOUSE_LARGE), 1);
            }
        if(playerToAck.getWarehouse().getMidInventory() != null)
            for (int i = 0; i < playerToAck.getWarehouse().getMidInventory().size(); i++) {
                removeResources(new BiElement<>(playerToAck.getWarehouse().getMidInventory().get(i), Storage.WAREHOUSE_MID), 1);
            }
        if(playerToAck.getWarehouse().getSmallInventory() != null)
            removeResources(new BiElement<>(playerToAck.getWarehouse().getSmallInventory(), Storage.WAREHOUSE_SMALL), 1);


        warBackup = playerToAck.getWarehouse();
        playerToAck.clearWarehouse();

        for (BiElement<Resource, Storage> element : msg.getPlacements()) {
            switch (element.getSecondValue()) {
                case WAREHOUSE_SMALL -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 1)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        playerToAck.setWarehouse(warBackup);
                        update(MessageID.BAD_REARRANGE_REQUEST);
                        return;
                    }
                }
                case WAREHOUSE_MID -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 2)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        playerToAck.setWarehouse(warBackup);
                        update(MessageID.BAD_REARRANGE_REQUEST);
                        return;
                    }
                }
                case WAREHOUSE_LARGE -> {
                    if (playerToAck.storeInWarehouse(element.getFirstValue(), 3)) {
                        addResources(element, 1);
                    } else {
                        addedResources.clear();
                        mustChoosePlacements = true;
                        playerToAck.setWarehouse(warBackup);
                        update(MessageID.BAD_REARRANGE_REQUEST);
                        return;
                    }
                }
                default -> {
                    addedResources.clear();
                    mustChoosePlacements = true;
                    playerToAck.setWarehouse(warBackup);
                    update(MessageID.BAD_REARRANGE_REQUEST);
                    return;
                }
            }
        }
        update(UPDATE);
        update(MessageID.ACK);
    }

    /**
     * Register the leader that a player choose in model
     * @param ids IDs of the leaders
     * @param nick the player that has made the choice
     */
    public synchronized void chooseLeaderCards(String ids, String nick) {

        List<Integer> leadersIds = convertStringToListInteger(ids);
        List<LeaderCard> leaders = convertIdToLeaderCard(leadersIds);
        List<ProPlayer> allP = game.getPlayers();
        ProPlayer choosingPlayer = null;

        for(ProPlayer p : allP){
            if(p.getNickname().equals(nick)){
                choosingPlayer = p;
                p.chooseLeaders(leaders);
                break;
            }
        }

        if(choosingPlayer!=null && choosingPlayer.getTurnID()==1 &&  game.getTotalPlayers()>1){
            System.out.println("Player 1 leaders: " + choosingPlayer.getLeaderCards().size());
            System.out.println("Sending leader choice for player 1 to everyone");
            game.start(choosingPlayer);
            MessageEnvelope env = new MessageEnvelope(UPDATE, choosingPlayer.getUpdate());
            updateBroadCast(env);
        }
        if(playerLeaderDoneCtr == game.getTotalPlayers()-1 && game.getTotalPlayers()!=1 && choosingPlayer!=null && choosingPlayer.getTurnID()==1 && startCounter==game.getTotalPlayers()-1){
            update(START_INITIAL_GAME);
        }
        playerLeaderDoneCtr++;
        continueStart();
    }

    private int leaderIDtoSend;

    /**
     * Active a leader
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
                    leaderIDtoSend = leaderCard.getId();
                    update(ACTIVATE_LEADER);
                    update(ACK);
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
        playerToAck = game.getCurrPlayer();
        List<LeaderCard> leaders = playerToAck.getLeaderCards();
        for (LeaderCard card : leaders) {
            if (card.getId() == Integer.parseInt(s)) {
                if (playerToAck.discardLeaderCard(card)) {
                    update(MessageID.PLAYERS_POSITION);
                    System.out.println("Basic action done: " + basicActionDone);
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
    public synchronized void addResources(BiElement<Resource, Storage> element, Integer qty) {
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

    private void setTempRes(BiElement<Resource,Storage> element, Integer qty, Map<BiElement<Resource,Storage>, Integer> deposit){
        if (qty == 0) {
            return;
        }
        AtomicBoolean found = new AtomicBoolean(false);
        deposit.forEach((x,y) -> {
            if (x.equals(element)) {
                found.set(true);
                deposit.compute(x, (k,v) -> v + qty);
            }
        });

        if(!found.get()){
            deposit.put(element, qty);
        }
    }

    /**
     * If {@code element} is already contained in {@code addedResources}, then it decrements its presence by {@code qty}.
     * In order to do this, it increments the presence in {@code removedResources} map which is specific for this
     * purpose. Otherwise, it add the element to the map as a new occurrence with is value set at {@code qty}.
     */
    public synchronized void removeResources(BiElement<Resource, Storage> element, Integer qty) {
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
     * Start the last phase of the game by letting the others players make their last turn
     * @param playerTriggerID player that has trigger the win action
     */
    public void initEndPhase(int playerTriggerID){
        if(!endPhase) {
            System.out.println("INIT END PHASE");
            endPhase = true;
            playersLeftToPlay = game.getTotalPlayers() - playerTriggerID;
            if(playersLeftToPlay==0){
                winner = game.getWinner();
                update(PLAYER_WIN);
            }
        }
    }

    /**
     * Generate an {@link EndTurnMessage} by setting the player that has just terminated the turn as {@code previousPlayer}
     * and cause the change of currentPlayer by calling {@code updateEndTurn()} from {@link it.polimi.ingsw.model.ModelObserver}.
     * <p>In the message, it stores the new player that has to play and the buyable production cards.</p>
     */
    private synchronized EndTurnMessage generateEndTurnMessage() {
        basicActionDone = false;
        previousPlayer = game.getCurrPlayer();
        game.updateEndTurn(previousPlayer);

        return new EndTurnMessage(game.getCurrPlayer().getTurnID());
    }

    /**
     * Generate an {@link UpdateMessage} by filling its attributes using {@code previousPlayer} set by {@code generateEndTurn()},
     * the (new) currentPlayer, some information retrieved from model, {@code addedResources} and {@code removedResources}.
     */
    private synchronized UpdateMessage generateUpdateMessage() {
        //for first update in mid game
        if(previousPlayer==null){
            previousPlayer = game.getCurrPlayer();
        }
        List<BiElement<Integer, Boolean>> thisPlayerActiveLeaders = new ArrayList<>();

        //production card bought set by: buyProductionCardAction
        //addedResources set by: organizeResourceAction,
        //removedResources set by:
        for (LeaderCard ld : previousPlayer.getLeaderCards()) {
            thisPlayerActiveLeaders.add(new BiElement<>(ld.getId(), ld.isActive()));
            /*if (ld.isActive()) {
                thisPlayerActiveLeaders.add(new BiElement<>(ld.getId(), ld.isActive()));
            }*/
        }

        UpdateMessage msg = new UpdateMessage(previousPlayer.getTurnID(), previousPlayer.getCurrentPosition(), game.getCurrPlayer().getTurnID(),
                game.getMarket().getMarketBoard(), game.getMarket().getExtraMarble(), game.getBuyableProductionID(),
                boughtCard.orElse(null), thisPlayerActiveLeaders, addedResources, removedResources);

        //msg.setSerializedResources();
        boughtCard = Optional.empty();

        return msg;
    }

    // ENVELOPE CREATOR ------------------------------------------------------------------------------------------------


    /**
     * Send the message to the respective remoteview using the right parameter for every message
     * @param messageID the message ID of the message to send
     */
    @Override
    public synchronized void update(MessageID messageID) {
        System.out.println(messageID);
        switch (messageID) {

            case ACK -> remoteViews.get(playerToAck.getTurnID() - 1).update(new MessageEnvelope(messageID, String.valueOf(basicActionDone)));

            case CONFIRM_END_TURN -> {
                EndTurnMessage msg = generateEndTurnMessage();
                String payload = gson.toJson(msg, EndTurnMessage.class);
                MessageEnvelope envelope = new MessageEnvelope(messageID, payload);
                updateBroadCast(envelope);
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
                    BAD_REARRANGE_REQUEST,
                    BAD_STORAGE_REQUEST,
                    WRONG_LEVEL_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, ""));

            case STORE_RESOURCES -> remoteViews.get(playerToAck.getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));

            case UPDATE -> {
                UpdateMessage msg = generateUpdateMessage();
                MessageEnvelope envelope = new MessageEnvelope(MessageID.UPDATE, gson.toJson(msg, UpdateMessage.class));
                updateBroadCast(envelope);
            }

            case LORENZO_POSITION -> {
                LorenzoInformationsMessage msg = new LorenzoInformationsMessage(act.getId(), ((SinglePlayerGame) game).getLorenzoPosition(), game.getBuyableProductionCards());
                MessageEnvelope env = new MessageEnvelope(LORENZO_POSITION, gson.toJson(msg, LorenzoInformationsMessage.class));
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
                MessageEnvelope envelope = new MessageEnvelope(MessageID.PLAYERS_POSITION, payload);

                updateBroadCast(envelope);
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
                updateBroadCast(envelope);
            }

            case INFO -> remoteViews.get(getCurrPlayerTurnID() - 1).update(new MessageEnvelope(messageID, "Invalid operation now"));

            case START_INITIAL_GAME -> {
                MessageEnvelope envelope = new MessageEnvelope(MessageID.START_INITIAL_GAME, "");
                updateBroadCast(envelope);
            }

            case ACTIVATE_LEADER -> remoteViews.get(getCurrPlayerTurnID() - 1).update(new MessageEnvelope(messageID, String.valueOf(leaderIDtoSend)));

            case PLAYER_WIN -> {
                MessageEnvelope envelope = new MessageEnvelope(PLAYER_WIN, winner.getNickname());
                updateBroadCast(envelope);
            }

            case PLAYER_CRASHED -> {
                EndTurnMessage msg = new EndTurnMessage(game.getCurrPlayer().getTurnID());
                MessageEnvelope envelope = new MessageEnvelope(PLAYER_CRASHED, gson.toJson(msg, EndTurnMessage.class));
                updateBroadCast(envelope);
            }

            default -> System.out.println("No no no");
        }
    }

    private void updateBroadCast(MessageEnvelope envelope){
        for(Observer<MessageEnvelope> obs : remoteViews){
            obs.update(envelope);
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
        ProPlayer crashedP;

        if (game instanceof MultiPlayerGame) {
            crashedP = game.getPlayerFromNickname(playerName);
            if(crashedP!=null){
                if(crashedP.getTurnID() == game.getCurrPlayer().getTurnID()){
                    game.updateEndTurn(crashedP);
                    basicActionDone = false;
                    addedResources.clear();
                    removedResources.clear();
                    boughtCard = Optional.empty();
                }
                ((MultiPlayerGame)game).removeFromActivePlayers(crashedP);
                update(PLAYER_CRASHED);
            }
        } else {
            //do nothing, unless we want reset the game instead of leaving it pending
            //disconnect?
        }
    }

    /**
     * Let the player with this {@code nickname} rejoin the multiplayer game he/she was in before a disconnection.
     */
    public void rejoin(MultiPlayerGame game, String nickname) {
        System.out.println("Rejoining");
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
            throw new RuntimeException("Rejoin multiplayer error: player not found");
        }

        List<BiElement<Integer, Integer>> prodCards = new ArrayList<>();
        prodCards.addAll(player.getProdCards1().stream().map(x -> new BiElement<>(x.getId(), 1)).collect(Collectors.toList()));
        prodCards.addAll(player.getProdCards2().stream().map(x -> new BiElement<>(x.getId(), 2)).collect(Collectors.toList()));
        prodCards.addAll(player.getProdCards3().stream().map(x -> new BiElement<>(x.getId(), 3)).collect(Collectors.toList()));

        Map<BiElement<Resource, Storage>, Integer> tempAddRes = new HashMap<>();
        Resource resource;
        if ((resource = player.getWarehouse().getSmallInventory()) != null) {
            setTempRes(new BiElement<>(resource, Storage.WAREHOUSE_SMALL), 1, tempAddRes);
        }
        for (Resource res : player.getWarehouse().getMidInventory()) {
            setTempRes(new BiElement<>(res, Storage.WAREHOUSE_MID), 1, tempAddRes);
        }
        for (Resource res : player.getWarehouse().getLargeInventory()) {
            setTempRes(new BiElement<>(res, Storage.WAREHOUSE_LARGE), 1, tempAddRes);
        }
        Map<Resource, Integer> inventory = player.getLootChest().getInventory();
        setTempRes(new BiElement<>(Resource.SERVANT, Storage.LOOTCHEST), inventory.get(Resource.SERVANT), tempAddRes);
        setTempRes(new BiElement<>(Resource.COIN, Storage.LOOTCHEST), inventory.get(Resource.COIN), tempAddRes);
        setTempRes(new BiElement<>(Resource.SHIELD, Storage.LOOTCHEST), inventory.get(Resource.SHIELD), tempAddRes);
        setTempRes(new BiElement<>(Resource.STONE, Storage.LOOTCHEST), inventory.get(Resource.STONE), tempAddRes);


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
                prodCards, leadersIds, tempAddRes, null);
        //MessageEnvelope env = new MessageEnvelope(REJOIN_UPDATE, gson.toJson(msg, UpdateMessage.class));

        //updateBroadCast(env);
    }

    public void rejoin(SinglePlayerGame game, String nickname) {
    }

    public Game getGame() {
        return game;
    }

    /**
     * Initialize all the card from the JSON file
     */

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