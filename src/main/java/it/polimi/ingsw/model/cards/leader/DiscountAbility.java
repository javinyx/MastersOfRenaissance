package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DiscountAbility implements LeaderCard {
    private int id;
    private final int victoryPoints;
    private final List<ConcreteProductionCard> cost;
    private boolean status;
    private final Resource discountType;

    public DiscountAbility(int id, int victoryPoints, List<ConcreteProductionCard> cost, Resource discountType) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>(cost);
        status = false;
        this.discountType = discountType;
    }

    public Resource getDiscountType(){ return discountType; }

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
    public List<Buyable> getCost() {
        return new ArrayList<>(cost);
    }

    public boolean applyEffect(ProPlayer player){

        if (player.getTurnType() == 'b') {

            for (int i = 0; i < player.getResAcquired().size(); i++) {
                if (player.getResAcquired().get(i).equals(discountType)) {
                    player.getResAcquired().remove(i);
                    return true;
                }
            }
        }
        return false;
    }
    @Override
    public String toString(){
        List<ProductionCard> generalCost = cost.stream().map(x -> (ProductionCard)x).collect(Collectors.toList());
        return "DiscountAbility(Victory Points: " + victoryPoints + ("\nActivation Cost: " + generalCost==null ? "null" : generalCost)
                + "\nDiscount Type: " + discountType + ")";
    }

}