package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConcreteProductionCardTest {

    MultiplayerGame game;
    ProPlayer player;

    @BeforeEach
    public void testSetUp(){
        game = new MultiplayerGame();
        player = new ProPlayer("ale", 1, game);
    }

    @Test
    void getCost() {
        for (int i = 0; i < 48; i++) {
            assertEquals(Resource.class, game.getProductionDecks().get(i).getCost().get(0).getClass());
        }
    }

    @Test
    void getVictoryPoints() {
        for (int i = 0; i < 48; i++) {
            assertTrue(game.getProductionDecks().get(i).getVictoryPoints() >= 0);
        }
    }

    @Test
    void getRequiredResources() {
        for (int i = 0; i < 48; i++) {
            assertEquals(Resource.class, game.getProductionDecks().get(i).getRequiredResources().get(0).getClass());
        }
    }

    @Test
    void getProduction() {
        for (int i = 0; i < 48; i++) {
            assertEquals(Resource.class, game.getProductionDecks().get(i).getProduction().get(0).getClass());
        }
    }
}