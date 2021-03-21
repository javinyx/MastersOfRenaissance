package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Player implements Observable{

    private String nickname;
    private int currPos;
    private List<ProductionCard> prodCards;
    private Observer observer;

    public Player(String nickname){
        this.nickname = nickname;
        this.prodCards = new ArrayList<>();
    }


    public String getNickname(){
        return nickname;
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
