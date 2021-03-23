package it.polimi.ingsw.model;

public class BoostAbility implements LeaderCard{

    private final int victoryPoints;
    private final Buyable cost;
    private boolean status;
    private Resource resourceNeeded;

    public BoostAbility(int victoryPoints, Buyable cost, Resource resourceNeeded) {
        this.victoryPoints = victoryPoints;
        this.cost = cost;
        status = false;
        this.resourceNeeded = resourceNeeded;
    }

    public Resource getResource(){ return resourceNeeded; }

    @Override
    public boolean isActive() {
        return status;
    }

    @Override
    public void setStatus(boolean activate) {

    }

    @Override
    public int getVictoryPoints() {
        return 0;
    }

    @Override
    public Buyable getCost() {
        return null;
    }

    public void applyEffect(ProPlayer player){}

}