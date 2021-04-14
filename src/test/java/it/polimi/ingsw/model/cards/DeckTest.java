package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @BeforeEach
    public void testSetup(){
        List<Deck> productionDecks = Deck.createProdDeckList();
        Deck d = new Deck("LeaderCard");
        Deck a = new Deck("ActionToken");
    }

    @Test
    void getCards() {
    }

    @Test
    void createProdDeckList() {
    }
}