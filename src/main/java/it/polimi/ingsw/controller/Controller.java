package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.*;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.BadStorageException;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        MessageEnvelope envelope;

        numPlayer = 1;
        game = new SinglePlayerGame(this);
        initializationPhase = false;

        game.createPlayer(nickName);

        int curr = game.getCurrPlayer().getTurnID()-1;

        //Leader choice
        envelope = new MessageEnvelope(MessageID.CHOOSE_LEADER_CARDS, String.valueOf(game.leaderDistribution()));
        remoteViews.get(curr).update(envelope);

        game.start(game.getCurrPlayer());
    }

    public void createMultiplayerGame(List<String> players){

        this.numPlayer = players.size();
        game = new MultiPlayerGame(this);

        //Player Creation
        for (int i = 0; i < numPlayer; i++)
            game.createPlayer(players.get(i));

        for (ProPlayer p : ((MultiPlayerGame)game).getActivePlayers()) {

            MessageEnvelope envelope;
            int curr = p.getTurnID()-1;

            envelope = new MessageEnvelope(MessageID.TURN_NUMBER, String.valueOf(curr+1));
            remoteViews.get(curr).update(envelope);

            //Leader choice
            envelope = new MessageEnvelope(MessageID.CHOOSE_LEADER_CARDS, String.valueOf(game.leaderDistribution()));
            remoteViews.get(curr).update(envelope);

            //Resource Choice
            if (curr == 1)
                update(MessageID.CHOOSE_RESOURCE);
            else if (curr == 2 || curr == 3){
                update(MessageID.CHOOSE_RESOURCE);
                game.getCurrPlayer().moveOnBoard(1);
            }
            game.start(p);
        }

        // TODO: ogni giocatore va informato della situazione degli altri 3

        //timer = new Timer(true);
        //timer.schedule(new TurnTimerTask(this, game.getCurrPlayer().getTurnType()),turnTime*1000);


        System.out.println("Hey, sei qui, nel controller");
    }

    // END GAME INITIALIZATION -----------------------------------------------------------------------------------------

    // TURN STRUCTURE --------------------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    public synchronized void buyFromMarAction(BuyMarketMessage buyMark) {

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

        UpdateMessage msg = new UpdateMessage(game.getCurrPlayer().getTurnID(), game.getCurrPlayer().getCurrentPosition(), game.getMarket().getMarketBoard(), game.getMarket().getExtraMarble(), null, null, null, null, null);

        game.getCurrPlayer().setUpdate(msg);

        update(MessageID.UPDATE);

        game.updateEndTurn(game.getCurrPlayer());

    }

    public void buyProdCardAction(BuyProductionMessage buyProd) {

        UpdateMessage msg;

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

            if (card != null)
                game.getCurrPlayer().buyProductionCard(card, buyProd.getStack(), leaderCards, buyProd.getResourcesWallet());

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

        //msg = new UpdateMessage(game.getCurrPlayer(), game.getBuyableProductionCards().get(index));

        update(MessageID.UPDATE);

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
            game.getCurrPlayer().startProduction(produce.getProductionCards(), produce.getResourcesWallet(), produce.getLeaderCards(), produce.getLeaderOutputs(), produce.isBasicProduction(), basicOut);
        }catch(BadStorageException e1){
            //notify that there's something wrong with the player storage
            update(MessageID.BAD_PRODUCTION_REQUEST);
        }catch(RuntimeException e2){
            //notify that some cards in production cards chosen by the player for this task cannot produce
            update(MessageID.CARD_NOT_AVAILABLE);
        }

        update(MessageID.UPDATE);

        game.updateEndTurn(game.getCurrPlayer());

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

    public void organizeResourceAction(StoreResourcesMessage message){


        for (BiElement<Resource, Storage> element: message.getPlacements()) {

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

        switch(messageID) {

            case ACK -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "True"));

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
                 WRONG_LEVEL_REQUEST ->  remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, ""));

            case STORE_RESOURCES -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getResAcquired().toString()));

            case UPDATE -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, game.getCurrPlayer().getUpdate()));


            default -> System.out.println("No no no");
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

    public void setInactivePlayer(String playerName){
        if(game instanceof MultiPlayerGame){
            ((MultiPlayerGame) game).removeFromActivePlayers(playerName);
        }else{
            //do nothing, unless we want reset the game instead of leaving it pending
            //timer and then disconnect?
        }
    }

    /**Let the player with this {@code nickname} rejoin the multiplayer game he/she was in before a disconnection.*/
    public void rejoin(MultiPlayerGame game, String nickname){
        List<ProPlayer> allPlayers = game.getPlayers();
        int turnId;
        for(ProPlayer p : allPlayers){
            if(p.getNickname().equals(nickname)){
                turnId = p.getTurnID();
                game.getActivePlayers().add(turnId-1, p);
                break;
            }
        }
    }

    public void rejoin(SinglePlayerGame game, String nickname){
        //TODO: che politica usiamo per il rejoin del single player?
    }

    public Game getGame(){
        return game;
    }
}