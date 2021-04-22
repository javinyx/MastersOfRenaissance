package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.stub.MultiGameStub;
import it.polimi.ingsw.model.stub.SingleGameStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest{
    Game gs1, gm1;

    @BeforeEach
    void setUp(){
       gs1 = new SingleGameStub();
       gm1 = new MultiGameStub();
    }

    @Test
    void getProductionDecks() {
        assertEquals(48, gs1.getAllProductionDecks().size());
        //all cards returned are in prodDecks
        for(int i=0; i<12; i++){
            for(int j=0; j<4; j++){
                assertTrue(gs1.getProductionDeck(i).getCards().contains(gs1.getAllProductionDecks().get((i*4)+j)));
            }
        }

        assertEquals(48, gm1.getAllProductionDecks().size());
        ConcreteProductionCard bought = gm1.getBuyableProductionCards().get(0);
        gm1.removeFromProdDeck(bought);
        assertEquals(47, gm1.getAllProductionDecks().size());
        assertEquals(3, gm1.getProductionDeck(0).size());
        for(Card c : gm1.getProductionDeck(0).getCards()){
            assertNotEquals(bought, c);
        }
        for(int i=1; i<12; i++){
            assertEquals(4, gm1.getProductionDeck(i).size());
        }
    }

    @Test
    void getProductionDeck() {
    }

    @Test
    void getBuyableProductionCards() {
        List<ConcreteProductionCard> cards = gs1.getBuyableProductionCards();
        assertEquals(12, cards.size());
        for(ConcreteProductionCard card : cards){
            assertTrue(gs1.getAllProductionDecks().contains(card));
        }
        assertTrue(gs1.removeFromProdDeck(cards.get(0)));
        assertEquals(12, gs1.getBuyableProductionCards().size());
        assertEquals(ColorEnum.GREEN, gs1.getBuyableProductionCards().get(0).getColor());
        for(int i=0; i<3; i++){
            cards = gs1.getBuyableProductionCards();
            System.out.println(cards.get(0));
            assertTrue(gs1.removeFromProdDeck(cards.get(0)));
            assertEquals(ColorEnum.GREEN, cards.get(0).getColor());
        }
        //1st deck is over
        assertEquals(11, gs1.getBuyableProductionCards().size());

        assertEquals(48, gm1.getAllProductionDecks().size());
        assertEquals(12, gm1.getBuyableProductionCards().size());
        for(int i=0; i<12; i++){
            assertEquals(4, gm1.getProductionDeck(i).size());
            for(int j=0; j<4; j++){
                assertEquals(12-i, gm1.getBuyableProductionCards().size());
                gm1.getProductionDeck(i).getFirst();
            }
        }
        assertEquals(0, gm1.getAllProductionDecks().size());
        assertEquals(0, gm1.getBuyableProductionCards().size());
        assertTrue(gm1.getBuyableProductionCards().isEmpty());
    }


    @Test
    void removeFromProdDeck() {
        ConcreteProductionCard p = (ConcreteProductionCard) gs1.getProductionDeck(1).peekFirst();
        assertFalse(gs1.getProductionDeck(0).remove(p));
        assertTrue(gs1.getProductionDeck(1).remove(p));
    }

    @Test
    void getTotalPlayers() {
        gm1.createPlayer("Coco");
        assertEquals(1, gm1.getTotalPlayers());
        gm1.createPlayer("Bobo");
        assertEquals(2, gm1.getTotalPlayers());
        gm1.createPlayer("Bananaaa");
        assertEquals(3, gm1.getTotalPlayers());
        gs1.createPlayer("Oli");
        assertEquals(1, gs1.getTotalPlayers());
    }

    @Test
    void getCurrPlayer() {
        ProPlayer p = new ProPlayer("Bb", 1, gm1);
        ((MultiGameStub)gm1).setCurrPlayer(p);
        assertEquals(p, gm1.getCurrPlayer());
        gs1.createPlayer("boh");
        assertEquals("boh", gs1.getCurrPlayer().getNickname());
    }
}