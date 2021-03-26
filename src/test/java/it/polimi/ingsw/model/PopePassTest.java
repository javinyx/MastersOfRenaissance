package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PopePassTest {
    PopePass popePass1, popePass2, popePass3;
    @BeforeEach
    void setUp() {
        popePass1 = new PopePass(1);
        popePass2 = new PopePass(2);
        popePass3 = new PopePass(3);
    }

    @Test
    void isActive() {
        assertFalse(popePass1.isActive());
        assertFalse(popePass2.isActive());
        assertFalse(popePass3.isActive());
        popePass1.activate();
        assertTrue(popePass1.isActive());
    }

    @Test
    void getLevel() {
        assertEquals(1, popePass1.getLevel());
        assertEquals(2, popePass2.getLevel());
        assertEquals(3, popePass3.getLevel());
    }

    @Test
    void activate() {
        assertFalse(popePass1.isActive());
        popePass1.activate();
        assertTrue(popePass1.isActive());
    }

    @Test
    void getVictoryPoints() {
        assertEquals(0, popePass1.getVictoryPoints());
        popePass2.activate();
        popePass3.activate();
        assertEquals(3, popePass2.getVictoryPoints());
        assertEquals(4, popePass3.getVictoryPoints());
    }
}