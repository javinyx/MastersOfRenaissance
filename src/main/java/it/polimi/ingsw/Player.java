package it.polimi.ingsw;

import java.util.ArrayList;
import java.util.List;

public class Player implements Observable{

    private String nickname;
    private final int turnID;
    private int currPos;
    private List<ProductionCard> prodCards;
    private Observer observer;

    public Player(String nickname, int turnID){
        this.turnID = turnID;
        this.nickname = nickname;
        this.prodCards = new ArrayList<>();
    }


    public String getNickname(){
        return nickname;
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
