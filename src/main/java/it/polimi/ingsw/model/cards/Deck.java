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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Deck {

    private File properties;
    private List<Card> cardList;

    public Deck(File properties){
        this.properties = properties;
        this.cardList = new ArrayList<>(); //deck
    }

    public Deck createDeck(Class inputClass){
        //lettura del file, creazione della carta mediante uno dei 3 costruttori: ProductionCard/LeaderCard/ActionToken
        //poi inserimento nella cardList
        //N.B: prodCard decks have 4 cards each
        return null;
    }

    public Deck createDeck(Class inputClass, String fileName){
        //lettura del file, creazione della carta mediante uno dei 3 costruttori: ProductionCard/LeaderCard/ActionToken
        //poi inserimento nella cardList
        //N.B: prodCard decks have 4 cards each
        return null;
    }

    private void instanceProdCards(String path) {

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream("/json/ProductionCards.json"))) {

            // Convert JSON File to Java Object
            cardList = Arrays.asList(gson.fromJson(reader, ConcreteProductionCard[].class));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void instanceLeadCards(String[] args) {

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream("/json/LeaderCards.json"))) {

            // Convert JSON File to Java Object
            LeaderCard[] leadCards = gson.fromJson(reader, LeaderCard[].class);

            // Pass each set of leader cards to the corresponding ability class
            // TO-DO: Assign the json objects to corresponding abilities

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void instanceActionTokens(String[] args) {

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream("/json/ActionTokens.json"))) {

            // Convert JSON File to Java Object
            ActionToken[] actionTokens = gson.fromJson(reader, ActionToken[].class);

            // Pass each set of action tokens to the corresponding ability class
            // TO-DO: Assign the json objects to corresponding abilities

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(){}

    public List<Card> getCards(){
        return cardList;
    }

    public Card getCard(int index){
        return cardList.get(index);
    }

    public void removeCard(int index){
        cardList.remove(index);
    }
}
