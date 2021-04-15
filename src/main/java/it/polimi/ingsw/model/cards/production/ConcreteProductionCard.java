package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.function.BiFunction;

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


    /*public String toStringProd(){
        return "Color: " + color + ", level: " + level + ", victoryPoints: " + victoryPoints +
                ("\nCost: " + cost==null ? "null" : cost.toString()) + ("\nRequired Resources: " + requiredResources==null ? "null" : requiredResources.toString()) +
                ("\nProduction: " + production==null ? "null" : production.toString());
    }*/
    @Override
    public String toString(){
        BiFunction<ArrayList<Resource>,String,  String> isNull = (x, name) -> {if(x==null)
                                                                return "";
                                                            return "\n"+name+" : " + x.toString();};
        return "Color: " + color + ", level: " + level + ", victoryPoints: " + victoryPoints +
                (isNull.apply(cost, "Cost")) + (isNull.apply(requiredResources, "RequiredResource")) +
                (isNull.apply(production, "Production"));
    }
}
