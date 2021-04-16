package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.cards.leader.DiscountAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiscountAbilityTest {

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
    void applyEffect() {
        while (!((LeaderCard)(game).getLeaderDeckNew().getFirst()).getNameNew().equals("DiscountAbility"))
            game.getLeaderDeckNew().getFirst();





    }


}