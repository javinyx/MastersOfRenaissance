package it.polimi.ingsw.model;

public abstract class LeaderDecorator extends LeaderCard{

    public LeaderDecorator(int victoryPoints, Buyable cost) {
        super(victoryPoints, cost);
    }

    public void applyEffect(){}

}
