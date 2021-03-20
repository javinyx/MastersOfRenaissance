package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private Player currPlayer;
    private List<Player> players;
    private int totalPlayers;


    public Game(){
        players = new ArrayList<>();
        totalPlayers = 0;
        currPlayer = null;
    }
    public void createPlayer(String nickname){
        /*if(totalPlayers == 4){
            //lanciare eccezione tooManyPlayers
            //se arriva a 4 disabilita metodo da controller
        }*/
        totalPlayers++;
        players.add(new Player(nickname, totalPlayers));
    }
}
