package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarbleAbilityTest {

    MultiPlayerGame game;
    ProPlayer bubu;
    ProPlayer javin;
    Controller controller = new Controller();

    @BeforeEach
    public void testSetUp(){
        game = new MultiPlayerGame(controller);
        bubu = new ProPlayer("Bubu", 1, game);
        javin = new ProPlayer("Javin", 2, game);
    }

    @Test
    void getReplacement() {
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
        MarbleAbility l = null;
        while (!((LeaderCard)(game).getLeaderDeckNew().getFirst()).getNameNew().equals("MarbleAbility"))
            if (((LeaderCard)(game).getLeaderDeckNew().peekFirst()).getNameNew().equals("MarbleAbility")) {
                l = (MarbleAbility) game.getLeaderDeckNew().getFirst();
                break;
            }
        List<Resource> marble = new ArrayList<>();
        marble.add(Resource.SERVANT);
        marble.add(Resource.BLANK);
        marble.add(Resource.STONE);
        marble.add(Resource.BLANK);

        bubu.setResAcquired(marble);
        bubu.setTurnType('m');
        if(l!=null) {
            l.applyEffect(bubu);
            l.applyEffect(bubu);
            assertEquals(l.getReplacingResource(), marble.get(2));
            assertEquals(l.getReplacingResource(), marble.get(3));
        }

    }

    @Test
    void testToString() {
    }
}