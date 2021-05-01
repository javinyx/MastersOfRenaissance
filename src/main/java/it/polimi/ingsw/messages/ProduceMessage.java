package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class ProduceMessage extends SimpleMessage{
    private final List<Integer> productionCardsIDs;
    private final ResourcesWallet resourcesWallet;
    private final List<Integer> leaderCardsIDs;
    private final List<Resource> leaderOutputs;
    private final boolean basicProduction;
    private final Resource basicOutput;

    public ProduceMessage(ProPlayer player, List<Integer> productionCardsIDs, ResourcesWallet wallet, List<Integer> leaderCardsIDs
                        , List<Resource> leaderOutputs, boolean basicProduction, Resource basicOutput){
        super(player);
        this.productionCardsIDs = productionCardsIDs;
        this.resourcesWallet = wallet;
        this.leaderCardsIDs = leaderCardsIDs;
        this.leaderOutputs = leaderOutputs;
        this.basicProduction = basicProduction;
        this.basicOutput = basicOutput;
    }

    public List<Integer> getProductionCardsIDs(){
        return productionCardsIDs;
    }

    public ResourcesWallet getResourcesWallet() {
        return resourcesWallet;
    }

    public List<Integer> getLeaderCardsIDs() {
        return leaderCardsIDs;
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
