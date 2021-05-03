package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.*;
import it.polimi.ingsw.messages.concreteMessage.BuyMarketMessage;
import it.polimi.ingsw.messages.concreteMessage.BuyProductionMessage;
import it.polimi.ingsw.messages.concreteMessage.ProduceMessage;
import it.polimi.ingsw.messages.concreteMessage.StoreResourcesMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.BadStorageException;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.*;

public class Controller implements Observer<MessageID> {

    //private int turnTime = 300; // max turn time in seconds
    //private Timer timer;
    private Game game;
    private boolean gameOver;
    private int numPlayer;
    private boolean initializationPhase;
    private char turnType;
    private List<Observer<MessageEnvelope>> remoteViews;

    public Controller() {
        initializationPhase = true;
        remoteViews = new ArrayList<>();

    }

    public boolean registerObserver (Observer<MessageEnvelope> obs){
        return remoteViews.add(obs);
    }

    // GAME INITIALIZATION -------------------------------------------------------------------------------

    public void createSinglePlayerGame(String nickName){
        numPlayer = 1;
        game = new SinglePlayerGame();
        initializationPhase = false;

        addPlayer(nickName);

        update(MessageID.ACK);
    }

    public void createMultiplayerGame(int numPlayer){
        if (numPlayer > 1 && numPlayer < 5) {
            this.numPlayer = numPlayer;
            game = new MultiPlayerGame();
            createLobby();
        }
        else
            update(MessageID.TOO_MANY_PLAYERS);

        //timer = new Timer(true);
        //timer.schedule(new TurnTimerTask(this, game.getCurrPlayer().getTurnType()),turnTime*1000);

        initializationPhase = false;
        game.start(numPlayer);

        update(MessageID.ACK);
    }

    public synchronized void addPlayer(String nickname){
        game.createPlayer(nickname);
        update(MessageID.ACK);
        notifyAll();
    }

    private synchronized void createLobby(){
        while(game.getTotalPlayers() != numPlayer) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // END GAME INITIALIZATION -----------------------------------------------------------------------------------------

    // TURN STRUCTURE --------------------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    public synchronized void buyFromMarAction(BuyMarketMessage buyMark) {

        //TODO: MODIFICARE IL MODEL PER ADATTARE LA BIFUNCTION E TOGLIERE LA DIPENDENZA DAL CONTROLLER PER I LEADER
        try {
            game.getCurrPlayer().buyFromMarket(buyMark.getDimension(), buyMark.getIndex(), buyMark.getMarbleUsage());
        }
        catch (IllegalArgumentException e){
            update(MessageID.BAD_DIMENSION_REQUEST);
        }
        catch (IndexOutOfBoundsException e) {
            update(MessageID.WRONG_STACK_CHOICE);
        }
        catch(RuntimeException e){
            update(MessageID.CARD_NOT_AVAILABLE);
        }

        update(MessageID.OK_BUY_MARKET);

    }

    public void buyProdCardAction(BuyProductionMessage buyProd) {

        try {
            game.getCurrPlayer().buyProductionCard(buyProd.getProdCard(), buyProd.getStack(), buyProd.getLeader(), buyProd.getResourcesWallet());
        }
        catch (BadStorageException e){
            //player has no resources
            update(MessageID.BAD_PAYMENT_REQUEST);
        }
        catch (IllegalArgumentException e){
            //prodcard have no cards
            update(MessageID.CARD_NOT_AVAILABLE);
        }
        catch(IndexOutOfBoundsException e){
            //stack < 1 || stack > 3
            update(MessageID.WRONG_STACK_CHOICE);
        }
        catch (RuntimeException e){
            //prodcard has wrong level
            update(MessageID.WRONG_LEVEL_REQUEST);
        }

        update(MessageID.OK_BUY_PRODUCTION_CARD);

    }

    public void activateProdAction(ProduceMessage produce) {

        Optional<Resource> basicOut;
        if(produce.getBasicOutput() == null){
            basicOut = Optional.empty();
        }
        else{
            basicOut = Optional.of(produce.getBasicOutput());
        }
        try {
            game.getCurrPlayer().startProduction(produce.getProductionCards(), produce.getResourcesWallet(), produce.getLeaderCards(), produce.getLeaderOutputs(), produce.isBasicProduction(), basicOut);
        }catch(BadStorageException e1){
            //notify that there's something wrong with the player storage
            update(MessageID.BAD_PRODUCTION_REQUEST);
        }catch(RuntimeException e2){
            //notify that some cards in production cards chosen by the player for this task cannot produce
            update(MessageID.CARD_NOT_AVAILABLE);
        }

        update(MessageID.OK_PRODUCTION);

    }

    // END TURN STRUCTURE ----------------------------------------------------------------------------------------------

    // TURN UTILITIES --------------------------------------------------------------------------------------------------

    public void activateLeader(LeaderCard leaderCard){

        for (int i = 0; i < ProPlayer.getMaxNumExtraStorage(); i++) {
            if(game.getCurrPlayer().getLeaderCards().get(i).equals(leaderCard)) {
                game.getCurrPlayer().activateLeaderCard(leaderCard);
                break;
            }
        }

        update(MessageID.CARD_NOT_AVAILABLE);
    }

    public void organizeResourceAction(List<BiElement<Resource, Storage>> message){

        for (BiElement<Resource, Storage> element: message ) {

            switch (element.getV()){

                case WAREHOUSE_SMALL -> game.getCurrPlayer().storeInWarehouse(element.getT(), 1);
                case WAREHOUSE_MID -> game.getCurrPlayer().storeInWarehouse(element.getT(), 2);
                case WAREHOUSE_LARGE -> game.getCurrPlayer().storeInWarehouse(element.getT(), 3);

                case EXTRA1 -> game.getCurrPlayer().storeInExtraStorage(element.getT(), game.getCurrPlayer().getExtraStorage().get(0));
                case EXTRA2 -> game.getCurrPlayer().storeInExtraStorage(element.getT(), game.getCurrPlayer().getExtraStorage().get(1));

                default -> update(MessageID.BAD_STORAGE_REQUEST);
            }
        }
    }

    public String getNick(){
        return game.getCurrPlayer().getNickname();
    }

    // END TURN UTILITIES ----------------------------------------------------------------------------------------------

    // ENVELOPE CREATOR ------------------------------------------------------------------------------------------------

    @Override
    public void update(MessageID messageID){

        //CAPIRE QUALI MESSAGGI SONO INDIRIZZATI A TUTTI E QUALI NO
        /*
         MODIFICARE POI METODI MODEL:
         STORE_RESOURCE
         OK_PRODUCTION
         OK_BUY_PROD_CARD
         OK_BUY_MARKET
         */

        switch(messageID) {
            case CHOOSE_RESOURCE -> { }
            case CHOOSE_CARD -> { }

            case ACK -> remoteViews.get(0).update(new MessageEnvelope(messageID, "Operation confirmed"));

            //INITIALIZATION

            case TOO_MANY_PLAYERS -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "The number of player must be from 2 to 4"));

            //GAME PHASES

            case CARD_NOT_AVAILABLE -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "The chosen card is not available"));

            case BAD_PRODUCTION_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "There something wrong with the storage"));
            case BAD_PAYMENT_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "don't have enough resources"));
            case BAD_DIMENSION_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "The dimension is wrong"));
            case WRONG_STACK_CHOICE -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "Stacks are from 1 to 3/4"));
            case WRONG_LEVEL_REQUEST ->  remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "The level of the card is wrong"));

            case STORE_RESOURCES -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));

            case OK_BUY_MARKET -> {
                for (int i = 0; i < game.getTotalPlayers(); i++)
                    remoteViews.get(i).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));
            }
            case OK_PRODUCTION -> {
                //for (int i = 0; i < game.getTotalPlayers(); i++)
                  //  remoteViews.get(i).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));
                }
            case OK_BUY_PRODUCTION_CARD -> {
                //for (int i = 0; i < game.getTotalPlayers(); i++)
                  //  remoteViews.get(i).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));
                }



            default -> throw new IllegalStateException("Unexpected value: " + messageID);
        }
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
        gameOver=true;
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

}