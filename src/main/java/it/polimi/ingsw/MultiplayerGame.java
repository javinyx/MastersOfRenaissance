package it.polimi.ingsw;


import java.util.ArrayList;
import java.util.List;

public class MultiplayerGame extends Game{
    private List<Player> activePlayers;

    void MultiplayerGame(){
        players = new ArrayList<>();
        totalPlayers = 0;
        currPlayer = null;
        winner = null;
        activePlayers = new ArrayList<>();
    }

    public void createPlayer(String nickname){
        totalPlayers++;
        Player p = new Player(nickname, totalPlayers);
        players.add(p);
        p.registerObserver(this);
        activePlayers.add(p);
    }

    public void updateEnd(Player player){
        if(player.getTurnID()<4){
            //let all the players on the caller's left side to play their last turn
        }

        /*create a list with all the possibile winners by victoryPoints and then choose which one
        * by counting their resources if needed. In the end, choose the final winner and end the match*/
    }
}

