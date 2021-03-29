package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.market.Resource;

public class BoostAbility implements LeaderCard {

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
    public void setStatus(boolean activate) { status = activate; }

    @Override
    public int getVictoryPoints() {
        return 0;
    }

    @Override
    public Buyable getCost() {
        return null;
    }

    public void applyEffect(ProPlayer player){

        if (player.getTurnType() == 'p'){

            if(player.getWarehouse().getSmallInventory() != null && player.getWarehouse().getSmallInventory().equals(resourceNeeded))
                player.getWarehouse().removeSmall();
            else if (player.getWarehouse().getMidInventory() != null && player.getWarehouse().getMidInventory().get(0).equals(resourceNeeded))
                player.getWarehouse().removeMid();
            else if (player.getWarehouse().getLargeInventory() != null && player.getWarehouse().getLargeInventory().get(0).equals(resourceNeeded))
                player.getWarehouse().removeLarge();
            else
                return;

            player.addFaithPoints(1);
            player.getLootChest().addResources(player.chooseResource());
        }
    }
}