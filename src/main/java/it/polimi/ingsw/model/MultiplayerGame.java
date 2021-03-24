package it.polimi.ingsw.model;


import java.util.ArrayList;
import java.util.List;

public class MultiplayerGame implements Game, Observer {
    private List<ProPlayer> players;
    private List<ProPlayer> activePlayers;
    private int totalPlayers;
    private ProPlayer currPlayer;
    private ProPlayer winner;
    private Market market;


    void MultiplayerGame(){
        players = new ArrayList<>();
        market = new Market();
        totalPlayers = 0;
        currPlayer = null;
        winner = null;
        activePlayers = new ArrayList<>();
    }

    public void start(){}

    public Player getWinner(){
        return winner;
    }

    public void createPlayer(String nickname){
        totalPlayers++;
        ProPlayer p = new ProPlayer(nickname, totalPlayers, this);
        players.add(p);
        p.registerObserver(this);
        activePlayers.add(p);
    }

    public List<ProPlayer> getPlayers() {
        return players;
    }

    /*public List<Player> getPlayers(){
        List<Player> users = new ArrayList<>();
        users.addAll(players);
        return users;
    }*/

    public Market getMarket(){
        this.updateEnd(currPlayer);
        return market;
    }

    /** The Observer finalize the game by managing the last turn of every player after the one who called the method.
     * Decide the winner based on {@code searchForWinner()} policy.
     * @param player the observed one notifying the observer that the game might end since the faith track's end or
     * max num of prodCards has been reached.*/
    public void updateEnd(ProPlayer player){
        if(player.getTurnID()<4){
            //let all the players on the caller's left side to play their last turn
        }

        /*create a list with all the possibile winners by victoryPoints and then choose which one
        * by counting their resources if needed. In the end, choose the final winner and end the match*/
    }

    public void updateEndTurn(ProPlayer player){
        int currID = player.getTurnID();
        currID = (currID == totalPlayers) ? 1 : currID+1;
        if(activePlayers.isEmpty()){
            //kill game: nobody is in the lobby right now, we could send the thread to sleep (like for 2 mins)
            //if after that nobody joined back, end the game
            return;
        }
        for(ProPlayer p : activePlayers){
            //next active player found: give him the currPlayer title
            if(p.getTurnID() == currID){
                currPlayer = p;
                return;
            }
        }
        //search for the one who should have been playing the next turn, then call the method (recursion)
        for(ProPlayer p : players){
            if(p.getTurnID() == currID){
                updateEndTurn(p);
            }
        }

    }

    public void updateEnd(Player player){}
    public void updatePosition(Player player){}
    public void alertVaticanReport(Player player, int vaticanReport){}

    /**Add 1 faith point to each active player (except the discarding one).
     * @param player player discarding a resource */
    public void alertDiscardResource(Player player){
        for(ProPlayer p : activePlayers){
            if(!p.equals(player)){
                p.addFaithPoints(1);
            }
        }
    }
}

