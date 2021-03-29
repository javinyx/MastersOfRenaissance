package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.market.Resource;

public class DiscountAbility implements LeaderCard {
    private final int victoryPoints;
    private final Buyable cost;
    private boolean status;
    private Resource discountType;

    public DiscountAbility(int victoryPoints, Buyable cost, Resource discountType) {
        this.victoryPoints = victoryPoints;
        this.cost = cost;
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
    public Buyable getCost() {
        return cost;
    }

    public void applyEffect(ProPlayer player){

        if (player.getTurnType() == 'b')

            for (int i = 0; i < player.getResAcquired().size(); i++)
                if (player.getResAcquired().get(i).equals(discountType)){
                    player.getResAcquired().remove(i);
                    return;
                }
    }

}