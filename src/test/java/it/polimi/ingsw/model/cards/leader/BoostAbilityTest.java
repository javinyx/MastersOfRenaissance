package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoostAbilityTest {

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
        while (!((LeaderCard)(game).getLeaderDeckNew().getFirst()).getNameNew().equals("BoostAbility"))
            game.getLeaderDeckNew().getFirst();



    }

    @Test
    void testToString() {
    }
}