package it.polimi.ingsw.model;

public class BoostAbility extends LeaderDecorator{

    private Resource resourceNeeded;

    public BoostAbility(int victoryPoints, Buyable cost, Resource resourceNeeded) {
        super(victoryPoints, cost);
        this.resourceNeeded = resourceNeeded;
    }

    public Resource getResource(){ return resourceNeeded; }

    private void addFaithPoints(){}

    public void applyEffect(){}

}