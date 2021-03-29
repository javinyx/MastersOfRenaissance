package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.model.player.Player;

import org.junit.jupiter.api.Assertions;

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
        Assertions.assertEquals("Gatto", player.getNickname());
    }

    @Test
    void getCurrentPosition() {
        Assertions.assertEquals(0, player.getCurrentPosition());
        player.moveOnBoard(2);
        Assertions.assertEquals(2, player.getCurrentPosition());
    }

    @Test
    void moveOnBoard() {
        player.moveOnBoard(1);
        Assertions.assertEquals(1, player.getCurrentPosition());
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> player.moveOnBoard(-1));
        player.moveOnBoard(23);
        Assertions.assertEquals(24, player.getCurrentPosition(), "Player didn't arrived at last position");
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> player.moveOnBoard(1));
    }

    @Test
    void registerObserver() {
        player.registerObserver((Observer)singleGame);
        Assertions.assertSame(singleGame, player.getObserver(), "Got singlePlayer game wrong");
        player.registerObserver((Observer)multiGame);
        Assertions.assertSame(multiGame, player.getObserver(), "Observer substitution: cannot go from single to multi");
    }
}