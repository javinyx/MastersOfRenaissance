package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class UpdateMessage extends SimpleMessage {
    private final Resource[][] marketBoard;
    private final Resource extraMarble;
    private final List<Integer> availableProductionCards;
    private final List<Integer> productionCardsId, leadersId;
    private final List<Resource> resources;

    public UpdateMessage(Resource[][] marketBoard, Resource extraMarble, List<Integer> availableProductionCards,
                         List<Integer> productionCardsId, List<Integer> leadersId, List<Resource> resources){
        this.marketBoard = marketBoard;
        this.extraMarble = extraMarble;
        this.availableProductionCards = availableProductionCards;
        this.productionCardsId = productionCardsId;
        this.leadersId = leadersId;
        this.resources = resources;
    }

    public Resource[][] getMarketBoard(){
        return marketBoard;
    }

    public List<Integer> getAvailableProductionCards() {
        return availableProductionCards;
    }

    public List<Integer> getProductionCardsId() {
        return productionCardsId;
    }

    public List<Integer> getLeadersId() {
        return leadersId;
    }

    public List<Resource> getResources() {
        return resources;
    }
}
