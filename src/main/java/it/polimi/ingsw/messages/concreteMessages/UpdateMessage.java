package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.market.Resource;

import java.util.List;
import java.util.Map;

public class UpdateMessage extends SimpleMessage {
    private final int playerId, playerPos, nextPlayerId;
    private final Resource[][] marketBoard;
    private final Resource extraMarble;
    private final List<Integer> availableProductionCards;

    private final BiElement<Integer,Integer> productionCardId;
    private final List<Integer> leadersId;
    private final Map<BiElement<Resource, Storage>, Integer> addedResources, removedResources;

    /**@param playerId is the player that has caused changes in the model
     * @param playerPos how far the player that caused this update message is gone on the faith track
     * @param nextPlayerId is the next player that has to play the turn
     * @param marketBoard the new market configuration
     * @param extraMarble the new extra marble in market
     * @param availableProductionCards the 12 production cards available for purchase, after the player ({@code playerId}
     * has made some changes
     * @param productionCardId production card's id bought by the {@code playerId} ({@code T}) player
     *        and the stack where it's been put ({@code V})
     * @param leadersId active leaders that {@code playerId} has
     * @param addedResources all the resources that {@code playerId} has (lootchest, warehouse, extraStorage) in that moment
     * @param removedResources all the resources removed from {@code playerId}'s storage (lootchest, warehouse, extraStorage) once the turn was over*/
    public UpdateMessage(int playerId, int playerPos, int nextPlayerId, Resource[][] marketBoard, Resource extraMarble,
                         List<Integer> availableProductionCards, BiElement<Integer, Integer> productionCardId,
                         List<Integer> leadersId, Map<BiElement<Resource, Storage> ,Integer> addedResources,
                         Map<BiElement<Resource, Storage> ,Integer> removedResources){
        this.playerId = playerId;
        this.playerPos = playerPos;
        this.nextPlayerId = nextPlayerId;
        this.marketBoard = marketBoard;
        this.extraMarble = extraMarble;
        this.availableProductionCards = availableProductionCards;
        this.productionCardId = productionCardId;
        this.leadersId = leadersId;
        this.addedResources = addedResources;
        this.removedResources = removedResources;
    }

    public Resource[][] getMarketBoard(){
        return marketBoard;
    }
    public Resource getExtraMarble(){return extraMarble;}
    public int getPlayerId(){return playerId;}
    public int getPlayerPos(){return playerPos;}
    public int getNextPlayerId(){return nextPlayerId;}
    public List<Integer> getAvailableProductionCards() {
        return availableProductionCards;
    }

    /**return the production card's id bought (param T in {@link it.polimi.ingsw.misc.BiElement}) and the
     * stack in where it's been put (param V in {@link it.polimi.ingsw.misc.BiElement})*/
    public BiElement<Integer, Integer> getProductionCardId() {
        return productionCardId;
    }

    public List<Integer> getLeadersId() {
        return leadersId;
    }

    /**@return all the resources that the player owns in that moment across all types of storage*/
    public Map<BiElement<Resource, Storage> ,Integer> getAddedResources() {
        return addedResources;
    }

    /**@return all the resources removed from the player's storages*/
    public Map<BiElement<Resource, Storage>,Integer> getRemovedResources() {
        return removedResources;
    }

}
