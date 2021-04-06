package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;

/**This LeaderCard works similarly to a ProductionCard: it has a production power that players can decide to activate
 * if they have the card and the latter is active.
 * <p>In order to activate the card, players must have all the production cards specified in {@code cost}, even if they're
 * hidden.
 * Once it's active (use {@code isActive()} method to discover its status)</p>*/
public class BoostAbility implements LeaderCard {

    private final int victoryPoints;
    private final List<ProductionCard> cost;
    private boolean status;
    private Resource resourceNeeded;

    public BoostAbility(int victoryPoints, List<ProductionCard> cost, Resource resourceNeeded) {
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>();
        this.cost.addAll(cost);
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
        return victoryPoints;
    }

    @Override
    public List<Buyable> getCost() {
        return new ArrayList<>(cost);
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