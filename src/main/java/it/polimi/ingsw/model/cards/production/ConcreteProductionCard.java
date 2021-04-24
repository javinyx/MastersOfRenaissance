package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * The ConcreteProductionCard is a Card that can be puchased from the grid, when it's the player's turn.
 * It costs a determined amount of Resourced, indicated by {@code cost}, and it's objective is to help the player produce more
 * Resourced and earn more victory points.
 * <p>Every Production card has a color and a level, which is the criteria used to divide the cards into the smaller 4-card decks, for each color and level.
 * If a player wishes to purchase a ProductionCard of a level higher than 1, they must already own the Card of the previous level, while the new one needs to be placed on top.</p>
 * <p>Once a player owns at least one ProductionCard, they can use them to convert {@code requiredResources} to {@code production}, which includes Faith Points, of course
 * the conversion gets better the higher the level of the card is.</p>
 * <p>It's also important to know that if a player reaches 7 owned ProductionCards, the game ends.</p>
 */
public class ConcreteProductionCard extends ProductionCard implements Card {
    private final int id;
    private int victoryPoints;
    private List<Resource> cost;
    private List<Resource> requiredResources;
    private List<Resource> production;

    /**
     * Instantiate a new Concrete Production Card.
     *
     * @param id                the id of the card
     * @param victoryPoints     the victory points
     * @param color             the color of the card
     * @param level             the level of the card
     * @param cost              the cost in Resources
     * @param requiredResources the required Resources to start Production
     * @param production        the output of Resources when Production is finished
     */
    public ConcreteProductionCard(int id, int victoryPoints, ColorEnum color, int level, List<Resource> cost, List<Resource> requiredResources, List<Resource> production) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.color = color;
        this.level = level;
        this.cost = new ArrayList<>();
        this.cost.addAll(cost);
        this.requiredResources = new ArrayList<>();
        this.requiredResources.addAll(requiredResources);
        this.production = new ArrayList<>();
        this.production.addAll(production);
    }

    public int getId(){return id;}

    /**
     * Get the cost of the card in Resources.
     *
     * @return the cost in Resources
     */
    public List<Resource> getCost() {
        return new ArrayList<Resource>(cost);
    }

    /**
     * Get the amount of victory points assigned to the card.
     *
     * @return the victory points
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Get the required Resources to start the Production phase.
     *
     * @return the required resources
     */
    public List<Resource> getRequiredResources() { return new ArrayList<>(requiredResources); }

    /**
     * Get the Resources produced once the Production phase is finished.
     *
     * @return the production output
     */
    public List<Resource> getProduction() { return new ArrayList<>(production); }


    /* public String toStringProd(){
        return "Color: " + color + ", level: " + level + ", victoryPoints: " + victoryPoints +
                ("\nCost: " + cost==null ? "null" : cost.toString()) + ("\nRequired Resources: " + requiredResources==null ? "null" : requiredResources.toString()) +
                ("\nProduction: " + production==null ? "null" : production.toString());
    }*/
    @Override
    public String toString(){
        BiFunction<List<Resource>,String,  String> isNull = (x, name) -> {if(x==null)
                                                                return "";
                                                            return "\n"+name+" : " + x.toString();};
        return "Color: " + color + ", level: " + level + ", victoryPoints: " + victoryPoints +
                (isNull.apply(cost, "Cost")) + (isNull.apply(requiredResources, "RequiredResource")) +
                (isNull.apply(production, "Production"));
    }
}
