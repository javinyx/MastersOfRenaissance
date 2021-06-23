package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.stub.ControllerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoveShuffleTokenTest {

    SinglePlayerGame game;
    ProPlayer player;
    ControllerStub controller = new ControllerStub();
    @BeforeEach
    public void testSetUp(){
        game = new SinglePlayerGame(controller);
        controller.registerGame(game);
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