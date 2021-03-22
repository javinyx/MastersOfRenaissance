package it.polimi.ingsw.model;

public class DiscountAbility extends LeaderDecorator{

    private Resource discountType;

    public DiscountAbility(int victoryPoints, Buyable cost, Resource discountType) {
        super(victoryPoints, cost);
        this.discountType = discountType;
    }

    public Resource getDiscountType(){ return discountType; }

    public void applyEffect(){}

}