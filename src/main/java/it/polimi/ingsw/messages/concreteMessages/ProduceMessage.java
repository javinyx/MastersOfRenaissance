package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.util.List;

public class ProduceMessage extends SimpleMessage {
    private final List<ConcreteProductionCard> productionCards;
    private final ResourcesWallet resourcesWallet;
    private final List<LeaderCard> leaderCards;
    private final List<Resource> leaderOutputs;
    private final boolean basicProduction;
    private final Resource basicOutput;
    private final List<Resource> basicIn;

    public ProduceMessage(List<ConcreteProductionCard> productionCards, ResourcesWallet wallet, List<LeaderCard> leaderCards
            , List<Resource> leaderOutputs, boolean basicProduction, Resource basicOutput, List<Resource> basicIn){
        this.productionCards = productionCards;
        this.resourcesWallet = wallet;
        this.leaderCards = leaderCards;
        this.leaderOutputs = leaderOutputs;
        this.basicProduction = basicProduction;
        this.basicOutput = basicOutput;
        this.basicIn = basicIn;
    }

    public List<ConcreteProductionCard> getProductionCards(){
        return productionCards;
    }

    public ResourcesWallet getResourcesWallet() {
        return resourcesWallet;
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public List<Resource> getLeaderOutputs() {
        return leaderOutputs;
    }

    public boolean isBasicProduction() {
        return basicProduction;
    }

    public Resource getBasicOutput() {
        return basicOutput;
    }

    public List<Resource> getBasicIn() {
        return basicIn;
    }
}
