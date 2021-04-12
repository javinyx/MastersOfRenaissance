package it.polimi.ingsw.model.cards;

import com.google.gson.Gson;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class Deck {

    private Deque<Card> cardList;
    private List<Deck> deckList;
    private static String   prodPath = "/json/ProductionCards.json",
                            leadPath = "/json/LeaderCards.json",
                            tokenPath = "/json/ActionTokens.json";

    public Deck(){
        this.cardList = new ArrayDeque<Card>();
    }

    public Deck(Class classe,String filePath){

        //switch + istanzia deck + deck mischiato; arrangiati

    }

    // If given only the deck class name to initiate, then it will find the json path and call the main deck constructor
    private void readJson(){
        readJson();
    }

    // Main deck constructor
    private void readJson(String filePath){
        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream(filePath))) {

            // Convert JSON File to Java Object
            List<Card> list = Arrays.asList(gson.fromJson(reader, Card.class));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Deck> createProdDeck(String filePath) {

        //crea lista totale

        return deckList;
    }

    public void draw(){}

    public Deque<Card> getCards(){
        return cardList;
    }

    public Card peekFirst() {
        return cardList.peekFirst();
    }

    public Card getFirst(){
        return cardList.getFirst();
    }

}
