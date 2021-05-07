package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.model.ResourcesWallet;

public class BuyProductionMessage {

    private int prodCardId;
    private int stack;
    private int leaderId;
    private ResourcesWallet resourcesWallet;

    public BuyProductionMessage(int prodCardId, int stack, int leaderId, ResourcesWallet resourcesWallet) {
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

    public int getLeader() {
        return leaderId;
    }

    public ResourcesWallet getResourcesWallet() {
        return resourcesWallet;
    }
}
