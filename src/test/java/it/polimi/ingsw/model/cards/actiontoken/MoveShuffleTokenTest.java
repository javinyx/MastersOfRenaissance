package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveShuffleTokenTest {

    SinglePlayerGame game;
    ProPlayer player;
    Controller controller = new Controller();
    @BeforeEach
    public void testSetUp(){
        game = new SinglePlayerGame(controller);
        player = new ProPlayer("ale", 1, game);
    }

    @Test
    void draw() {

        while (!game.getTokenDeck().peekFirst().toString().equals("Move&Shuffle Token"))
            game.getTokenDeck().getFirst();

        player.drawActionToken();
        assertEquals(1, player.getCurrentPosition());

        player.moveOnBoard(2);

        assertEquals(3, player.getCurrentPosition());

        while (!game.getTokenDeck().peekFirst().toString().equals("Move&Shuffle Token"))
            game.getTokenDeck().getFirst();

        player.drawActionToken();
        assertEquals(4, player.getCurrentPosition());

    }

    @Test
    void testToString() {
    }
}