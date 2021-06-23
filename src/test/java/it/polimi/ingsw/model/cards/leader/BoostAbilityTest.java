package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.stub.ControllerStub;
import it.polimi.ingsw.model.stub.MultiPlayerGameStub;
import it.polimi.ingsw.model.stub.ProPlayerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoostAbilityTest {

    MultiPlayerGameStub game;
    ProPlayerStub bubu;
    Deck deckClone;

    @BeforeEach
    public void testSetUp(){
        ControllerStub c = new ControllerStub();
        game = new MultiPlayerGameStub(c);
        c.registerGame(game);
        bubu = new ProPlayerStub("Bubu", 1, game);
        deckClone = game.getLeaderDeckNew();

        bubu.setTurnType('p');
        game.setCurrPlayer(bubu);

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

        Resource testRes;
        List<Resource> tempLootChest = new ArrayList<>();

        LeaderCard currCard;

        int j=0;

        for(int i = 0; i < deckClone.getCards().size(); i++, deckClone.getFirst()){
            if((currCard = (LeaderCard) deckClone.peekFirst()) instanceof BoostAbility){
                j++;
                System.out.println(currCard);

                tempLootChest.add(Resource.STONE);
                tempLootChest.add(Resource.COIN);

                testRes = ((BoostAbility) currCard).getResource();
                tempLootChest.add(testRes);

                bubu.setLootChest(tempLootChest);
                bubu.setResAsCash();
                bubu.getResAsCash().setLootchestTray(tempLootChest);

                assertTrue(currCard.applyEffect(bubu));
                assertEquals(4, bubu.getLootChest().getInventory().size());
                assertEquals(1, bubu.getLootChest().getInventory().get(Resource.STONE));
                assertEquals(1, bubu.getLootChest().getInventory().get(Resource.COIN));
                assertEquals(0, bubu.getLootChest().getInventory().get(Resource.SERVANT));
                assertEquals(0, bubu.getLootChest().getInventory().get(Resource.SHIELD));

                if(testRes != Resource.COIN && testRes != Resource.STONE)
                    assertFalse(bubu.getResAsCash().isInLootChestTray(testRes));

                assertEquals(2, bubu.getResAsCash().getLootchestTray().size());

                assertEquals(j, bubu.getCurrentPosition());

                tempLootChest.clear();
                bubu.setResAsCash();
                bubu.resetLootChest();
            }
        }

    }

    @Test
    void testToString() {
    }
}