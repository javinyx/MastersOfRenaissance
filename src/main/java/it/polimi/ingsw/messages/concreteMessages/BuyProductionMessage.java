package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.model.ResourcesWallet;

import java.util.List;

public class BuyProductionMessage {

    private final int prodCardId;
    private final int stack;
    private final List<Integer> leaderId;
    private final ResourcesWallet resourcesWallet;

    /**
     * @param prodCardId id of the Development Card the player wishes to buy
     * @param stack where to put the card on the Player's Board once it has been bought (1,2 or 3)
     * @param leaderId list of leader cards ids that the player wants to use for buying the card
     * @param resourcesWallet the guide on how the server should remove the resources from the player's storage
     *                        in order to pay for the card
     */
    public BuyProductionMessage(int prodCardId, int stack, List<Integer> leaderId, ResourcesWallet resourcesWallet) {
        this.prodCardId = prodCardId;
        this.stack = stack;
        this.leaderId = leaderId;
        this.resourcesWallet = resourcesWallet;
    }

    public int getProdCardId() {
        return prodCardId;
    }

    public int getStack() {
        return stack;
    }

    public List<Integer> getLeader() {
        return leaderId;
    }

    public ResourcesWallet getResourcesWallet() {
        return resourcesWallet;
    }
}
