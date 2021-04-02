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

public class ReadFromJson implements Buyable, Card {
    private int id;
    private int victoryPoints;
    private ColorEnum color;
    private int level;
    private ArrayList<Integer> cost;
    private ArrayList<Integer> requiredResources;
    private ArrayList<Integer> production;

    public ReadFromJson(int id, int victoryPoints, ColorEnum color, int level, ArrayList<Integer> cost, ArrayList<Integer> requiredResources, ArrayList<Integer> production) {
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
            ReadFromJson[] prodCards = new Gson().fromJson(reader, ReadFromJson[].class);

            // print prodCards
            for(int i = 0; i < prodCards.length; i++) {
                System.out.println(prodCards[i].color);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}