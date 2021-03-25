package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    Player player;
    Game singleGame = new SinglePlayerGame();
    Game multiGame = new MultiplayerGame();
    @BeforeEach
    void setUp() {
       player = new Player("Gatto", singleGame);
       player.registerObserver((Observer)singleGame);
    }

    @Test
    void getNickname() {
        assertEquals("Gatto", player.getNickname());
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
        player.moveOnBoard(-1);
        assertEquals(1, player.getCurrentPosition(), "Player moved backward");
        player.moveOnBoard(23);
        assertEquals(24, player.getCurrentPosition(), "Player didn't arrived at last position");
        player.moveOnBoard(1);
        assertEquals(24, player.getCurrentPosition(), "Player went over 24th position");
    }

    @Test
    void registerObserver() {
        player.registerObserver((Observer)singleGame);
        assertSame(singleGame, player.getObserver(), "Got singlePlayer game wrong");
        player.registerObserver((Observer)multiGame);
        assertSame(multiGame, player.getObserver(), "Observer substitution: cannot go from single to multi");
    }
}