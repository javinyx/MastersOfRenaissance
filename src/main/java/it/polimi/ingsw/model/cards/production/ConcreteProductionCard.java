package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;

public class ConcreteProductionCard extends ProductionCard implements Buyable, Card {
    private int id;
    private int victoryPoints;
    private ArrayList<Resource> cost;
    private ArrayList<Resource> requiredResources;
    private ArrayList<Resource> production;

    public ConcreteProductionCard(int id, int victoryPoints, ColorEnum color, int level, ArrayList<Resource> cost, ArrayList<Resource> requiredResources, ArrayList<Resource> production) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.color = color;
        this.level = level;
        this.cost = cost;
        this.requiredResources = requiredResources;
        this.production = production;
    }

    public ArrayList<Resource> getCost() {
        return new ArrayList<Resource>(cost);
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public ArrayList<Resource> getRequiredResources() { return new ArrayList<>(requiredResources); }

    public ArrayList<Resource> getProduction() { return new ArrayList<>(production); }

    @Override
    public String toString(){
        return "Color: " + color + ", level: " + level + ", victoryPoints: " + victoryPoints +
                "\nCost: " + cost.toString() + "\nRequired Resources: " + requiredResources.toString() +
                "\nProduction: " + production.toString();
    }
}
