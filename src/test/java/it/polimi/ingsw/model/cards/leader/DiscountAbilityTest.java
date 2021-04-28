package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.stub.MultiPlayerGameStub;
import it.polimi.ingsw.model.stub.ProPlayerStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class DiscountAbilityTest {

    MultiPlayerGameStub game;
    ProPlayerStub bubu;
    Deck deckClone;

    @BeforeEach
    public void testSetUp(){
        game = new MultiPlayerGameStub();
        bubu = new ProPlayerStub("Bubu", 1, game);
        deckClone = game.getLeaderDeckNew();

        bubu.setTurnType('b');
        game.setCurrPlayer(bubu);
    }

    @Test
    void applyEffect() {
        Resource testRes;
        List<Resource> tempLootChest = new ArrayList<>();

        LeaderCard currCard;

        int j = 0;

        for (int i = 0; i < deckClone.getCards().size(); i++, deckClone.getFirst()) {
            if ((currCard = (LeaderCard) deckClone.peekFirst()) instanceof DiscountAbility) {
                j++;
                System.out.println(currCard);

                tempLootChest.add(Resource.STONE);
                tempLootChest.add(Resource.STONE);
                tempLootChest.add(Resource.COIN);
                tempLootChest.add(Resource.COIN);
                tempLootChest.add(Resource.SHIELD);
                tempLootChest.add(Resource.SHIELD);
                tempLootChest.add(Resource.SERVANT);
                tempLootChest.add(Resource.SERVANT);

                testRes = ((DiscountAbility) currCard).getDiscountType();

                bubu.setLootChest(tempLootChest);
                bubu.setResAsCash();
                bubu.getResAsCash().setLootchestTray(tempLootChest);




                tempLootChest.clear();
                bubu.setResAsCash();
                bubu.resetLootChest();
            }
        }

    }
}