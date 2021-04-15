package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class DeckTest {

    @BeforeEach
    public void testSetup(){
        List<Deck> productionDecks = Deck.createProdDeckList();
        Deck d = new Deck("LeaderCard");
        /*for (int i = 0; i < 16; i++) {
            System.out.println(d.getFirst().toString());
        }*/

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.println(productionDecks.get(i).getFirst().toString()+"\n");
            }
        }

        /*Deck a = new Deck("ActionToken");
        for (int i = 0; i < 7; i++) {
            System.out.println(a.getFirst().toString());
        }*/

    }

    @Test
    void getCards() {
    }

    /*@Test
    void createProdDeckList() {
    }*/
}