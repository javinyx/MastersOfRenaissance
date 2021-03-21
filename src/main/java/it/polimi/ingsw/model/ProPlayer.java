package it.polimi.ingsw.model;

import java.util.List;

public class ProPlayer extends Player{
    private List<ProductionCard> prodCards;
    private int turnID;

    public ProPlayer(String nickname, int turnID){
        super(nickname);
        this.turnID = turnID;
    }

    public int getTurnID(){
        return turnID;
    }
    public void buyProductionCard(ProductionCard card){

    }
}
