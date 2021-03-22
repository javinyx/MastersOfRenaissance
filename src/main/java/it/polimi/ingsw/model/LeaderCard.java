package it.polimi.ingsw.model;

public class LeaderCard implements Card{

    private int victoryPoints;
    private Buyable cost;

    public LeaderCard(int victoryPoints, Buyable cost) {
        this.victoryPoints = victoryPoints;
        this.cost = cost;
    }

    public int getVictoryPoints(){ return victoryPoints; }

    public Buyable getCost(){ return cost; }

    public void applyEffect(){}
}
