package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscountAbilityTest {

    DiscountAbility dis;
    ProPlayer player;
    Game game;

    @BeforeEach
    public void testSetup(){
        List<Resource> resNeed= new ArrayList<>();
        resNeed.add(Resource.COINS);

        List<Resource> cost= new ArrayList<>();
        cost.add(Resource.SHIELD);
        cost.add(Resource.SHIELD);

        List<Resource> prod = new ArrayList<>();
        prod.add(Resource.FAITH);

        ProductionCard prodC1 = new ProductionCard(ColorEnum.GREEN, resNeed, prod, cost, 1, 1);

        ProductionCard prodC2 = new ProductionCard(ColorEnum.YELLOW, resNeed, prod, cost, 1, 1);

        List<ProductionCard> prodC = new ArrayList<>();
        prodC.add(prodC1);
        prodC.add(prodC2);

        dis = new DiscountAbility(12, prodC, Resource.SERVANT);

        game = new MultiplayerGame();

        player = new ProPlayer("Luca", 1, game);

        /*player.buyProductionCard(prodC1);
        player.buyProductionCard(prodC2);*/

        player.activateLeaderCard(dis);
    }

    @Test
    void applyEffect() {




    }


}