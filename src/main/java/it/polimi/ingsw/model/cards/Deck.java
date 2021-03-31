package it.polimi.ingsw.model.cards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Deck {

    private File properties;
    private List<Card> cardList;

    public Deck(File properties){
        this.properties = properties;
        this.cardList = new ArrayList<>(); //deck
    }

    public void createDeck(){
        //lettura del file, creazione della carta mediante uno dei 3 costruttori: ProductionCard/LeaderCard/ActionToken
        //poi inserimento nella cardList
        //N.B: prodCard decks have 4 cards each
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
