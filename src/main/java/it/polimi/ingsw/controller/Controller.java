package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.BuyMarketMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.ProduceMessage;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.BadStorageException;

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

    public void createSinglePlayerGame(){
        numPlayer = 1;
        game = new SinglePlayerGame();
        initializationPhase = false;
    }

    public void createMultiplayerGame(int numPlayer){
        if (numPlayer > 1 && numPlayer < 5) {
            this.numPlayer = numPlayer;
            game = new MultiPlayerGame();
            createLobby();
        }
        //else
        //Message: TOO_MANY_PLAYERS
    }

    public synchronized void addPlayer(String nickname){
        game.createPlayer(nickname);
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

        //timer = new Timer(true);
        //timer.schedule(new TurnTimerTask(this, game.getCurrPlayer().getTurnType()),turnTime*1000);

        initializationPhase = false;
        game.start(numPlayer);
    }

    // END GAME INITIALIZATION -----------------------------------------------------------------------------------------

    // TURN STRUCTURE --------------------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    public synchronized void buyFromMarAction(BuyMarketMessage classe) {

        //fare cose per avere i parametri

        //game.getCurrPlayer().buyFromMarket(rowOrCol, index, leaders);

        game.updateEndTurn(game.getCurrPlayer());

    }

    public void organizeResourceAction(List<Resource> resAquired){

        //in qualche modo manda un messaggio alla view vera chiamando la remote view che a quel punto ritorna la lista con le risorse ordinate

        List<Resource> organizedRes = new ArrayList<>();

        //in qualche modo devo avere questa lista e metterla in organizedRes

        //organizedRes = cose organizzate dalla view
        for (int i = 0; i < 6; i++) {
            if (organizedRes.get(0) != null)
                game.getCurrPlayer().storeInWarehouse(organizedRes.get(0), 1);

            if (organizedRes.get(1) != null) {
                game.getCurrPlayer().storeInWarehouse(organizedRes.get(1), 2);
                game.getCurrPlayer().storeInWarehouse(organizedRes.get(2), 2);
            }
            if (organizedRes.get(3) != null) {
                game.getCurrPlayer().storeInWarehouse(organizedRes.get(3), 3);
                game.getCurrPlayer().storeInWarehouse(organizedRes.get(4), 3);
                game.getCurrPlayer().storeInWarehouse(organizedRes.get(5), 3);
            }
        }

    }

    public void buyProdCardAction(ConcreteProductionCard prodCard, int stack, LeaderCard leader, ResourcesWallet resourcesWallet) {

            try {
                game.getCurrPlayer().buyProductionCard(prodCard, stack, leader, resourcesWallet);
            }
            catch (BadStorageException e){
                //player has no resources
                //Message: BAD_PAYMENT_REQUEST
            }
            catch (IllegalArgumentException e){
                //prodcard have no cards
                //Message: BAD_BUY_REQUEST
            }
            catch(IndexOutOfBoundsException e){
                //stack < 1 || stack > 3
                //Message: WRONG_STACK_REQUEST;
            }
            catch (RuntimeException e){
                //prodcard has wrong level
                //Message: WRONG_LEVEL_REQUEST
            }


        //invia messaggio esito corretto alla view;

        game.updateEndTurn(game.getCurrPlayer());

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

            List<ConcreteProductionCard> productionCards = new ArrayList<>();
            List<BoostAbility> leaderCards = new ArrayList<>();

            //Production cards
            for (int i = 0; i < produce.getProductionCardsIDs().size(); i++) {

                if(produce.getProductionCardsIDs().get(i) == game.getCurrPlayer().getProdCards1().peekFirst().getId())
                    productionCards.add(game.getCurrPlayer().getProdCards1().peekFirst());

                else if(produce.getProductionCardsIDs().get(i) == game.getCurrPlayer().getProdCards2().peekFirst().getId())
                    productionCards.add(game.getCurrPlayer().getProdCards2().peekFirst());

                else if(produce.getProductionCardsIDs().get(i) == game.getCurrPlayer().getProdCards3().peekFirst().getId())
                    productionCards.add(game.getCurrPlayer().getProdCards2().peekFirst());
            }

            //Leader cards
            for (int i = 0; i < produce.getLeaderCardsIDs().size(); i++) {

                if(produce.getLeaderCardsIDs().get(i) == game.getCurrPlayer().getLeaderCards().get(0).getId())
                    leaderCards.add((BoostAbility) game.getCurrPlayer().getLeaderCards().get(0));

                else if(produce.getLeaderCardsIDs().get(i) == game.getCurrPlayer().getLeaderCards().get(1).getId())
                    leaderCards.add((BoostAbility) game.getCurrPlayer().getLeaderCards().get(1));

            }

            game.getCurrPlayer().startProduction(productionCards, produce.getResourcesWallet(), leaderCards, produce.getLeaderOutputs(), produce.isBasicProduction(), basicOut);

        }catch(BadStorageException e1){
            //notify that there's something wrong with the player storage
            update(MessageID.BAD_PRODUCTION_REQUEST);
        }catch(RuntimeException e2){
            //notify that some cards in production cards chosen by the player for this task cannot produce
            update(MessageID.CARD_NOT_AVAILABLE);
        }

        update(MessageID.OK_PRODUCTION);

        game.updateEndTurn(game.getCurrPlayer());
    }

    // END TURN STRUCTURE ----------------------------------------------------------------------------------------------

    // TURN UTILITIES --------------------------------------------------------------------------------------------------

    public void activateLeader(LeaderCard leaderCard){

    }

    // END TURN UTILITIES ----------------------------------------------------------------------------------------------

    // ENVELOPE CREATOR ------------------------------------------------------------------------------------------------

    @Override
    public void update(MessageID messageID){

        //CAPIRE QUALI MESSAGGI SONO INDIRIZZATI A TUTTI E QUALI NO
        /*
         MODIFICARE POI METODI MODEL:
         ORGANIZE_RESOURCES
         OK_PRODUCTION
         */


        switch(messageID) {
            case CHOOSE_RESOURCE -> { }
            case CHOOSE_CARD -> { }

            case ORGANIZE_RESOURCES -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));
            case BAD_PRODUCTION_REQUEST -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "There something wrong with the storage"));
            case CARD_NOT_AVAILABLE -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "The chosen card is not available"));

            case OK_PRODUCTION -> {
                for (int i = 0; i < game.getTotalPlayers(); i++)
                    remoteViews.get(i).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));
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