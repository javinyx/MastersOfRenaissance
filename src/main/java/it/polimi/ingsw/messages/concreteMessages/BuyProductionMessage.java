package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.model.ResourcesWallet;

import java.util.List;

public class BuyProductionMessage {

    private int prodCardId;
    private int stack;
    private List<Integer> leaderId;
    private ResourcesWallet resourcesWallet;

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
