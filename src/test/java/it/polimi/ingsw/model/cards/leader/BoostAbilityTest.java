package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.stub.MultiPlayerGameStub;
import it.polimi.ingsw.model.stub.ProPlayerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoostAbilityTest {

    MultiPlayerGameStub game;
    ProPlayerStub bubu;

    @BeforeEach
    public void testSetUp(){
        game = new MultiPlayerGameStub();
        bubu = new ProPlayerStub("Bubu", 1, game);
    }

    @Test
    void getResource() {
    }

    @Test
    void isActive() {
    }

    @Test
    void setStatus() {
    }

    @Test
    void getVictoryPoints() {
    }

    @Test
    void getCost() {
    }

    @Test
    void applyEffect() {


    }

    @Test
    void testToString() {
    }
}