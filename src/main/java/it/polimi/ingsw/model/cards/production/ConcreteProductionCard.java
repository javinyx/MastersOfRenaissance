package it.polimi.ingsw.model.cards.production;

import com.google.gson.Gson;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.io.IOException;

public class ConcreteProductionCard extends ProductionCard implements Buyable, Card {
    private int id;
    private int victoryPoints;
    private ColorEnum color;
    private int level;
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

    public static void main(String[] args) {

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream("/json/ProductionCards.json"))) {

            // Convert JSON File to Java Object
            ConcreteProductionCard[] prodCards = gson.fromJson(reader, ConcreteProductionCard[].class);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Resource> getCost() {
        return new ArrayList<Resource>(cost);
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public ArrayList<Resource> getRequiredResources() { return new ArrayList<>(requiredResources); }

    public ArrayList<Resource> getProduction() { return new ArrayList<>(production); }

}
