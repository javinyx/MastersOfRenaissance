package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

public class SingleGameStub extends SinglePlayerGame {

    public SingleGameStub(){
        super();
    }

    public List<ConcreteProductionCard> getBuyableProductionCards(){
        //hardcoded card id: 1
        List<ConcreteProductionCard> cards = new ArrayList<>();
        List<Resource> cost = new ArrayList<>();
        List<Resource> requiredRes = new ArrayList<>();
        List<Resource> prod = new ArrayList<>();
        cost.add(Resource.SHIELD);
        cost.add(Resource.SHIELD);
        requiredRes.add(Resource.COIN);
        prod.add(Resource.FAITH);
        cards.add(new ConcreteProductionCard(1, 1, ColorEnum.GREEN, 1, cost, requiredRes, prod));

        return cards;
    }
}
