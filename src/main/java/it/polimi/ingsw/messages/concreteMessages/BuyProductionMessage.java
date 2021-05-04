package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;

public class BuyProductionMessage {

    private ConcreteProductionCard prodCard;
    private int stack;
    private LeaderCard leader;
    private ResourcesWallet resourcesWallet;

    public BuyProductionMessage(ConcreteProductionCard prodCard, int stack, LeaderCard leader, ResourcesWallet resourcesWallet) {
        this.prodCard = prodCard;
        this.stack = stack;
        this.leader = leader;
        this.resourcesWallet = resourcesWallet;
    }

    public ConcreteProductionCard getProdCard() {
        return prodCard;
    }

    public int getStack() {
        return stack;
    }

    public LeaderCard getLeader() {
        return leader;
    }

    public ResourcesWallet getResourcesWallet() {
        return resourcesWallet;
    }
}
