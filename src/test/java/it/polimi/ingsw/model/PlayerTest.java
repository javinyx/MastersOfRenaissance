package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.stub.ControllerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player player;
    Controller controller = new ControllerStub();
    Game singleGame = new SinglePlayerGame(controller);
    Game multiGame = new MultiPlayerGame(controller);
    @BeforeEach
    void setUp() {
        singleGame.createPlayer("Gatto");
        player = singleGame.getPlayers().get(0);
       //player = new Player("Gatto", singleGame);
       player.registerObserver(singleGame);
    }

    @Test
    void getNickname() { assertEquals("Gatto", player.getNickname());
    }

    @Test
    void getCurrentPosition() {
        assertEquals(0, player.getCurrentPosition());
        player.moveOnBoard(2);
        assertEquals(2, player.getCurrentPosition());
    }

    @Test
    void moveOnBoard() {
        player.moveOnBoard(1);
        assertEquals(1, player.getCurrentPosition());
        assertThrows(IndexOutOfBoundsException.class, () -> player.moveOnBoard(-1));
        player.moveOnBoard(23);
        assertEquals(24, player.getCurrentPosition(), "Player didn't arrived at last position");
        assertThrows(IndexOutOfBoundsException.class, () -> player.moveOnBoard(1));
    }

    @Test
    void registerObserver() {
        player.registerObserver((ModelObserver)singleGame);
        assertSame(singleGame, player.getObserver(), "Got singlePlayer game wrong");
        player.registerObserver((ModelObserver)multiGame);
        assertSame(multiGame, player.getObserver(), "ModelObserver substitution: cannot go from single to multi");
    }

    @Test
    void isInVaticanReportRange(){
        player.moveOnBoard(4);
        assertFalse(player.isInVaticanReportRange(1));
        player.moveOnBoard(1);
        assertTrue(player.isInVaticanReportRange(1));
        assertFalse(player.isInVaticanReportRange(2));
        assertFalse(player.isInVaticanReportRange(3));
        player.moveOnBoard(4);
        assertTrue(player.isInVaticanReportRange(1));
    }

}