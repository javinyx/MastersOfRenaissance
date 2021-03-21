package it.polimi.ingsw;

import java.util.List;

public abstract class Game implements Observer{
    protected Player currPlayer;
    protected List<Player> players;
    protected int totalPlayers;
    protected Player winner;

    public void createPlayer(String nickname){
        /*if(totalPlayers == 4){
            //lanciare eccezione tooManyPlayers
            //se arriva a 4 disabilita metodo da controller
        }*/
        totalPlayers++;
        Player p = new Player(nickname, totalPlayers);
        players.add(p);
        p.registerObserver(this);
    }

    /* le politiche di gestione della fine della partita sono un po' diverse tra Multiplayer e Singleplayer
    * quindi deleghiamo l'implementazione specifica del metodo updateEnd() alle 2 sottoclassi
    public void updateEnd(Player player){
        if(player.getTurnID()<4){
            //let all the players on the caller's left side to play their last turn
            }

        }*/

    public void updatePosition(Player player){

    }
}
