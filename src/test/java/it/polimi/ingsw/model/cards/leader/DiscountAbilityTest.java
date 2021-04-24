package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiscountAbilityTest {

    MultiPlayerGame game;
    ProPlayer bubu;
    ProPlayer javin;

    @BeforeEach
    public void testSetUp(){
        game = new MultiPlayerGame();
        bubu = new ProPlayer("Bubu", 1, game);
        javin = new ProPlayer("Javin", 2, game);
    }

    @Test
    void applyEffect() {
        while (!((LeaderCard)(game).getLeaderDeckNew().getFirst()).getNameNew().equals("DiscountAbility"))
            game.getLeaderDeckNew().getFirst();





    }


}