package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.cards.leader.DiscountAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


class DiscountAbilityTest {

    DiscountAbility dis;
    ProPlayer player;
    Game game;
    ConcreteProductionCard prodC1;
    ConcreteProductionCard prodC2;

    @BeforeEach
    public void testSetup(){
        List<Resource> resNeed= new ArrayList<>();
        resNeed.add(Resource.COIN);

        List<Resource> cost= new ArrayList<>();
        cost.add(Resource.SHIELD);
        cost.add(Resource.SHIELD);

        List<Resource> prod = new ArrayList<>();
        prod.add(Resource.FAITH);
        /*
        prodC1 = new ConcreteProductionCard(ColorEnum.GREEN, resNeed, prod, cost, 1, 1);

        prodC2 = new ConcreteProductionCard(ColorEnum.YELLOW, resNeed, prod, cost, 1, 1);
*/
        /*List<ConcreteProductionCard> prodC = new ArrayList<>();
        prodC.add(prodC1);
        prodC.add(prodC2);
        List<ProductionCard> costProd = prodC.stream()
                .map(x->(ProductionCard)x)
                .collect(Collectors.toList());*/

        //dis = new DiscountAbility(12, costProd, Resource.SERVANT);

        game = new MultiplayerGame();

        player = new ProPlayer("Luca", 1, game);



        //player.activateLeaderCard(dis);
    }

    @Test
    void applyEffect() {
        //player.getResAcquired().add();
        //dis.applyEffect(player);
        //player.buyProductionCard(prodC1, 1);

        //player.buyProductionCard(prodC2, 2);






    }


}