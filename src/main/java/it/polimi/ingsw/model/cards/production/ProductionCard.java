package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

public class ProductionCard implements Buyable, Card {

    private ColorEnum color;
    private List<Resource> requiredResources;
    private List<Resource> production;
    private List<Resource> cost;
    private int victoryPoints;
    private int level;

    public ProductionCard(ColorEnum color, List<Resource> requiredResources, List<Resource> production, List<Resource> cost, int victoryPoints, int level) {
        this.color = color;
        this.requiredResources = requiredResources;
        this.production = production;
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.level = level;
    }

    public List<Resource> getCost() {
        return new ArrayList<Resource>(cost);
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public List<Resource> getRequiredResources() { return new ArrayList<>(requiredResources); }

    public List<Resource> getProduction() { return new ArrayList<>(production); }

    public int getLevel() {
        return level;
    }


}
