package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StorageAbilityTest {

    MultiplayerGame game;
    ProPlayer bubu;
    ProPlayer javin;

    @BeforeEach
    public void testSetUp(){
        game = new MultiplayerGame();
        bubu = new ProPlayer("Bubu", 1, game);
        javin = new ProPlayer("Javin", 2, game);
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
        while (!((LeaderCard)(game).getLeaderDeckNew().getFirst()).getNameNew().equals("StorageAbility"))
            game.getLeaderDeckNew().getFirst();

    }

    @Test
    void getStorageType() {
    }

    @Test
    void remove() {
    }

    @Test
    void add() {
    }

    @Test
    void size() {
    }

    @Test
    void testToString() {
    }
}