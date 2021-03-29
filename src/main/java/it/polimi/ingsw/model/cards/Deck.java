package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.Card;

import java.io.File;
import java.util.ArrayList;

public class Deck {

    private File properties;
    private ArrayList<Card> cardList;

    public Deck(File properties, ArrayList<Card> cardList){
        this.properties = properties;
        this.cardList = cardList;
    }

    public void createDeck(){}

    public void draw(){}

}
