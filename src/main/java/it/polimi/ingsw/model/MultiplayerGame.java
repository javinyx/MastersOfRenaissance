package it.polimi.ingsw.model;


import java.util.ArrayList;
import java.util.List;

public class MultiplayerGame implements Game, Observer {
    private List<ProPlayer> players;
    private List<ProPlayer> activePlayers;
    //private Market market;
    private int getTurnID;
    private int currPos;
    private String nickname;
    private Observer observer;
    private int totalPlayers;
    private ProPlayer currPlayer;
    private ProPlayer winner;

    void MultiplayerGame(){
        players = new ArrayList<>();
        totalPlayers = 0;
        currPlayer = null;
        winner = null;
        activePlayers = new ArrayList<>();
    }

    public void createPlayer(String nickname){
        totalPlayers++;
        ProPlayer p = new ProPlayer(nickname, totalPlayers);
        players.add(p);
        p.registerObserver(this);
        activePlayers.add(p);
    }

    public List<Player> getPlayers(){
        List<Player> users = new ArrayList<>();
        users.addAll(players);
        return users;
    }

    public void updateEnd(ProPlayer player){
        if(player.getTurnID()<4){
            //let all the players on the caller's left side to play their last turn
        }

        /*create a list with all the possibile winners by victoryPoints and then choose which one
        * by counting their resources if needed. In the end, choose the final winner and end the match*/
    }

    public void updateEnd(Player player){}
    public void updatePosition(Player player){}
}

