package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class ProduceMessage extends SimpleMessage {
    private final List<ConcreteProductionCard> productionCards;
    private final ResourcesWallet resourcesWallet;
    private final List<BoostAbility> leaderCards;
    private final List<Resource> leaderOutputs;
    private final boolean basicProduction;
    private final Resource basicOutput;

    public ProduceMessage(ProPlayer player, List<ConcreteProductionCard> productionCards, ResourcesWallet wallet, List<BoostAbility> leaderCards
                        , List<Resource> leaderOutputs, boolean basicProduction, Resource basicOutput){
        super(player);
        this.productionCards = productionCards;
        this.resourcesWallet = wallet;
        this.leaderCards = leaderCards;
        this.leaderOutputs = leaderOutputs;
        this.basicProduction = basicProduction;
        this.basicOutput = basicOutput;
    }

    public List<ConcreteProductionCard> getProductionCards(){
        return productionCards;
    }

    public ResourcesWallet getResourcesWallet() {
        return resourcesWallet;
    }

    public List<BoostAbility> getLeaderCards() {
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
}
