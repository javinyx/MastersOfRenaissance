package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class UpdateMessage {
    private ProPlayer player;
    private List<Integer> availableProductionCards;
    private List<Integer> productionCardsId, leadersId;
    private List<Resource> resources;

    public UpdateMessage(ProPlayer player, List<Integer> availableProductionCards, List<Integer> productionCardsId,
                         List<Integer> leadersId, List<Resource> resources){
        this.player = player;
        this.availableProductionCards = availableProductionCards;
        this.productionCardsId = productionCardsId;
        this.leadersId = leadersId;
        this.resources = resources;
    }
}
