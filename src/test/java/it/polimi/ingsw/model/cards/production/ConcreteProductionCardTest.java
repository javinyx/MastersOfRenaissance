package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteProductionCardTest {

    MultiPlayerGame game;
    ProPlayer player;
    Controller controller = new Controller();

    @BeforeEach
    public void testSetUp(){
        game = new MultiPlayerGame(controller);
        player = new ProPlayer("ale", 1, game);
    }

    @Test
    void getCost() {
        for (int i = 0; i < 48; i++) {
            assertEquals(Resource.class, game.getAllProductionDecks().get(i).getCost().get(0).getClass());
        }
    }

    @Test
    void getVictoryPoints() {
        for (int i = 0; i < 48; i++) {
            assertTrue(game.getAllProductionDecks().get(i).getVictoryPoints() >= 0);
        }
    }

    @Test
    void getRequiredResources() {
        for (int i = 0; i < 48; i++) {
            assertEquals(Resource.class, game.getAllProductionDecks().get(i).getRequiredResources().get(0).getClass());
        }
    }

    @Test
    void getProduction() {
        for (int i = 0; i < 48; i++) {
            assertEquals(Resource.class, game.getAllProductionDecks().get(i).getProduction().get(0).getClass());
        }
    }
}