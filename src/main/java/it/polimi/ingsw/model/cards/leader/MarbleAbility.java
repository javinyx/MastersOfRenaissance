package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This LeaderCard makes it so, when you take Resources from the Market, each white Marble in the chosen column/row
 * gives you the indicated Resource.
 * <p>If you play two Leaders with this ability, when you take Resources from the Market, you must choose which Resource to take
 * (from those given by your Leaders) for each of the white Marbles (i.e. you can’t take both Resources from a single white Marble).</p>
 * <p>In order to activate the card, players must have all the production cards specified in {@code cost}, even if they're
 * hidden.
 * Use the {@code isActive()} method to discover its status.</p>
 */
public class MarbleAbility implements LeaderCard {
    private final int id;
    private final int victoryPoints;
    private final List<ConcreteProductionCard> cost;
    private boolean status;
    private Resource replacingResource;

    /**
     * Instantiate a new Marble Ability Leader Card.
     *
     * @param id             the id of the card
     * @param victoryPoints  the victory points
     * @param cost           the cost in ConcreteProductionCards
     * @param replacingResource the Resource gained every time a White Marble is selected in the Market
     */
    public MarbleAbility(int id, int victoryPoints, List<ConcreteProductionCard> cost, Resource replacingResource) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>(cost);
        status = false;
        this.replacingResource = replacingResource;
    }

    /**
     * Get the Resource gained from White Marbles in the Market.
     *
     * @return the resource gained
     */
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
    public List<Buyable> getCost() {
        return new ArrayList<>(cost);
    }

    public boolean applyEffect(ProPlayer player) {
        if(player.getTurnType()=='m') {
            List<Resource> res = player.getResAcquired();
            if (res.contains(Resource.BLANK)) {
                res.remove(Resource.BLANK);
                res.add(replacingResource);
                return true;
            }

        /*for (Resource r : player.getResAcquired()) {
            if (r == Resource.BLANK) {
                player.getResAcquired().remove(r);
                player.getResAcquired().add(replacingResource);
                return true;
            }
        }*/
        }
        return false;
    }
    @Override
    public String toString(){
        List<ProductionCard> generalCost = cost.stream().map(x -> (ProductionCard)x).collect(Collectors.toList());
        return "MarbleAbility(Victory Points: " + victoryPoints +("\nActivation Cost: " + generalCost==null ? "null" : generalCost) +
                "\nReplacing Resource: " + replacingResource + ")";
    }


    public String getNameNew(){
        return "MarbleAbility";
    }

}
