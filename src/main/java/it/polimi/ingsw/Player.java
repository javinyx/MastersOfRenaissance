package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Player implements Observable{

    String nickname;
    int turnID;
    List<ProductionCard> prodCards;
    Observer observer;

    public Player(String nickname, int turnID){
        this.turnID = turnID;
        this.nickname = nickname;
        this.prodCards = new ArrayList<>();
    }

    public int getTurnID(){
        return turnID;
    }

    public int getVictoryPoints(){
        int victoryPoints = 0;
        //it will sum all victorPoints cumulated through cards etc...
        return victoryPoints;
    }

    public void registerObserver(Observer observer){
        this.observer = observer;
    }


}
