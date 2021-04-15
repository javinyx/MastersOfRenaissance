package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DeckTest {

    @BeforeEach
    public void testSetup(){
        List<Deck> productionDecks = Deck.createProdDeckList();
        Deck d = new Deck("LeaderCard");
        Deck a = new Deck("ActionToken");
        System.out.println(productionDecks.get(0).getFirst().toString());
        //System.out.println(d.getFirst().toString());
    }

    @Test
    void getCards() {
    }

    @Test
    void createProdDeckList() {
    }
}