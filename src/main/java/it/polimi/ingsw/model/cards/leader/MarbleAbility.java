package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.market.Resource;
import java.util.ArrayList;
import java.util.List;

public class MarbleAbility implements LeaderCard {
    private final int victoryPoints;
    private final List<ProductionCard> cost;
    private boolean status;
    private Resource replacingResource;

    public MarbleAbility(int victoryPoints, List<ProductionCard> cost, Resource replacingResource) {
        this.victoryPoints = victoryPoints;
        this.cost = cost;
        status = false;
        this.replacingResource = replacingResource;
    }

    public Resource getReplacement(){
        return replacingResource;
    }

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
        return (Buyable)cost;
    }

    public void applyEffect(ProPlayer player) {

        for (Resource r : player.getResAcquired()) {
            if (r == Resource.BLANK) {
                player.getResAcquired().remove(r);
                player.getResAcquired().add(replacingResource);
            }
        }
    }

}
