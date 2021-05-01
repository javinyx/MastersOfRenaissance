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
 * This LeaderCard makes it so, when you buy a ProductionCard, you can pay its cost with a discount of
 * the indicated Resource - {@code discountType} (if the card you are buying has that Resource as a cost).
 * <p>In order to activate the card, players must have all the production cards specified in {@code cost}, even if they're
 * hidden.
 * Use the {@code isActive()} method to discover its status.</p>
 */
public class DiscountAbility implements LeaderCard {
    private final int id;
    private final int victoryPoints;
    private final List<ConcreteProductionCard> cost;
    private boolean status;
    private final Resource discountType;

    /**
     * Instantiate a new Discount Ability Leader Card.
     *
     * @param id             the id of the card
     * @param victoryPoints  the victory points
     * @param cost           the cost in ConcreteProductionCards
     * @param discountType  the Resource saved upon every purchase
     */
    public DiscountAbility(int id, int victoryPoints, List<ConcreteProductionCard> cost, Resource discountType) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>(cost);
        status = false;
        this.discountType = discountType;
    }

    /**
     * Get the Resource that is discounted upon every purchase at the market.
     *
     * @return the resource
     */
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
        return "DiscountAbility(Victory Points: " + victoryPoints + "\nActivation Cost: " + (/*generalCost==null ? "null" :*/ generalCost)
                + "\nDiscount Type: " + discountType + ")";
    }

    public String getNameNew(){
        return "DiscountAbility";
    }

    public int getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

}