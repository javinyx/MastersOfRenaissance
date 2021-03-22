package it.polimi.ingsw.model;

public class MarbleAbility extends LeaderDecorator{

    private Resource replacingResource;

    public MarbleAbility(int victoryPoints, Buyable cost, Resource replacingResource) {
        super(victoryPoints, cost);
        this.replacingResource = replacingResource;
    }

    public Resource getReplacement(){ return replacingResource; }

    public void applyEffect(){}

}
