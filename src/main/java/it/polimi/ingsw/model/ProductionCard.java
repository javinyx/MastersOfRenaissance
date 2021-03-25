package it.polimi.ingsw.model;

import java.util.List;

public class ProductionCard implements Buyable, Card {

    private ColorEnum color;
    private Resource requiredResource;
    private List<Resource> requiredResources;
    private List<Resource> production;
    private List<Resource> cost;
    private int faithPoints;
    private int victoryPoints;
    private int level;

    public ProductionCard(ColorEnum color, Resource requiredResource, List<Resource> requiredResources, List<Resource> production, List<Resource> cost, int faithPoints, int victoryPoints, int level) {
        this.color = color;
        this.requiredResource = requiredResource;
        this.requiredResources = requiredResources;
        this.production = production;
        this.cost = cost;
        this.faithPoints = faithPoints;
        this.victoryPoints = victoryPoints;
        this.level = level;
    }

    public List<Resource> getCost() {
        return cost;
    }

    public int getFaithPoints() {
        return faithPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public Resource getRequiredResource() { return requiredResource; }

    public List<Resource> getRequiredResources() { return requiredResources; }

    public List<Resource> getProduction() { return production; }

    public int getLevel() {
        return level;
    }


}
