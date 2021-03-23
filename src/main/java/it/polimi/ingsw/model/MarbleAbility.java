package it.polimi.ingsw.model;

public class MarbleAbility implements LeaderCard {
    private final int victoryPoints;
    private final Buyable cost;
    private boolean status;
    private Resource replacingResource;

    public MarbleAbility(int victoryPoints, Buyable cost, Resource replacingResource) {
        this.victoryPoints = victoryPoints;
        this.cost = cost;
        status = false;
        this.replacingResource = replacingResource;
    }

    public Resource getReplacement(){ return replacingResource; }

    @Override
    public boolean isActive() {
        return status;
    }

    @Override
    public void setStatus(boolean activate) {
        status = activate;
    }

    @Override
    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public Buyable getCost() {
        return cost;
    }

    public void applyEffect(){}

}
