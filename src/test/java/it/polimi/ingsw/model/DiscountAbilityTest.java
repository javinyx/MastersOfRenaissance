package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.cards.leader.DiscountAbility;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.cards.production.ColorEnum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class DiscountAbilityTest {

    DiscountAbility dis;
    ProPlayer player;
    Game game;
    ProductionCard prodC1;
    ProductionCard prodC2;

    @BeforeEach
    public void testSetup(){
        List<Resource> resNeed= new ArrayList<>();
        resNeed.add(Resource.COIN);

        List<Resource> cost= new ArrayList<>();
        cost.add(Resource.SHIELD);
        cost.add(Resource.SHIELD);

        List<Resource> prod = new ArrayList<>();
        prod.add(Resource.FAITH);

        prodC1 = new ProductionCard(ColorEnum.GREEN, resNeed, prod, cost, 1, 1);

        prodC2 = new ProductionCard(ColorEnum.YELLOW, resNeed, prod, cost, 1, 1);

        List<ProductionCard> prodC = new ArrayList<>();
        prodC.add(prodC1);
        prodC.add(prodC2);

        dis = new DiscountAbility(12, prodC, Resource.SERVANT);

        game = new MultiplayerGame();

        player = new ProPlayer("Luca", 1, game);



        player.activateLeaderCard(dis);
    }

    @Test
    void applyEffect() {
        //player.getResAcquired().add();
        dis.applyEffect(player);
        //player.buyProductionCard(prodC1, 1);

        //player.buyProductionCard(prodC2, 2);






    }


}