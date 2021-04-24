package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.view.RemoteView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Controller implements ControllerObserver {

    private int turnTime = 300; // max turn time in seconds
    private Timer timer;
    private Game game;
    private boolean gameOver;
    private int numPlayer;
    private boolean initializationPhase;
    private int busy;
    private RemoteView remoteView;

    public Controller() {
        initializationPhase = true;
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
            game = new MultiplayerGame();
            createLobby();
        }
        //else
            //invia messaggio TOO MANY PLAYERS
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

        timer = new Timer(true);
        timer.schedule(new TurnTimerTask(this, game.getCurrPlayer().getTurnID()),turnTime*1000);

        initializationPhase = false;
        game.start(numPlayer);
    }

    // END GAME INITIALIZATION ---------------------------------------------------------------------------

    // TURN STRUCTURE ------------------------------------------------------------------------------------

    //m == buymarket, b == buyproduction, p == activateProduction

    public synchronized void buyFromMar(int turnId, char rowOrCol, int index, List<LeaderCard> leaders) {

        game.getCurrPlayer().buyFromMarket(rowOrCol, index, leaders);

        game.updateEndTurn(game.getCurrPlayer());

    }

    public void organizeResource(List<Resource> resAquired){

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

    private void buyProdCard(int turnId) {
    }

    private void activateProd(int turnId) {
    }

    // END TURN STRUCTURE --------------------------------------------------------------------------------

    public int getBusy() {
        return busy;
    }

    public void setBusy(int busy) {
        this.busy = busy;
    }


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
     * Called when timer expires, eliminates the player whose turn is over
     * @param turnId identifier for the turn the timer is referring to
     */
    synchronized void turnTimeOver(int turnId) {
        if (gameOver || turnId != this.game.getCurrPlayer().getTurnID()) return;
        if (initializationPhase == true){
            abortGame();
            return;
        }
        //game.setRequest(GameMessagesToClient.TIME_OVER.name());
        //eliminatePlayer(null);
    }

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

    /**
     * Resets the turn timer.
     */
    public void resetTimer() {
        timer.cancel();
        timer = new Timer(true);
        timer.schedule(new TurnTimerTask(this, ((MultiplayerGame)game).getNextPlayer().getTurnID()),turnTime*1000);
    }

}
