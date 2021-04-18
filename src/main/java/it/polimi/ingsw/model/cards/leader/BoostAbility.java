package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.player.Warehouse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This LeaderCard works similarly to a ProductionCard: it has a production power that players can decide to activate
 * if they have the card and the latter is active.
 * <p>In order to activate the card, players must have all the production cards specified in {@code cost}, even if they're
 * hidden.
 * Use the {@code isActive()} method to discover its status.</p>
 */
public class BoostAbility implements LeaderCard {
    private final int id;
    private final int victoryPoints;
    private final List<ConcreteProductionCard> cost;
    private boolean status;
    private Resource resourceNeeded;

    /**
     * Instantiate a new Boost Ability Leader Card.
     *
     * @param id             the id of the card
     * @param victoryPoints  the victory points
     * @param cost           the cost in ConcreteProductionCards
     * @param resourceNeeded the Resource needed
     */
    public BoostAbility(int id, int victoryPoints, List<ConcreteProductionCard> cost, Resource resourceNeeded) {
        this.id = id;
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>(cost);
        status = false;
        this.resourceNeeded = resourceNeeded;
    }

    /**
     * Get the Resource needed to use the card.
     *
     * @return the resource
     */
    public Resource getResource(){ return resourceNeeded; }

    @Override
    public boolean isActive() {
        return status;
    }

    @Override
    public void setStatus(boolean activate) { status = activate; }

    @Override
    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public List<Buyable> getCost() {
        return new ArrayList<>(cost);
    }

    public boolean applyEffect(ProPlayer player){
        if (player.getTurnType() == 'p'){
            ResourcesWallet wallet = player.getResAsCash();

            if(wallet.isInLootChestTray(resourceNeeded) && player.getLootChest().getNumberResInInventory(resourceNeeded)>0){
                List<Resource> removeFromLoot = wallet.getLootchestTray();
                removeFromLoot.remove(resourceNeeded);
                player.getLootChest().removeResources(resourceNeeded);

            }else if(wallet.isInWarehouseTray(resourceNeeded)){
                List<Resource> removeFromWar = wallet.getWarehouseTray();
                Warehouse warehouse = player.getWarehouse();
                if(warehouse.getSmallInventory().equals(resourceNeeded)){
                    warehouse.removeSmall();
                    removeFromWar.remove(resourceNeeded);
                }else if(warehouse.getMidInventory().contains(resourceNeeded)){
                    warehouse.removeMid();
                    removeFromWar.remove(resourceNeeded);
                }else if(warehouse.getLargeInventory().contains(resourceNeeded)){
                    warehouse.removeLarge();
                    removeFromWar.remove(resourceNeeded);
                }else{
                    //player said that there were resourced to be removed from warehouse but there isn't that res in Warehouse
                    return false;
                }

            }else if(wallet.isInExtraStorage(resourceNeeded, 0)){
                StorageAbility extra = null;
                if(player.getExtraStorage().size()>0) {
                    extra = player.getExtraStorage().get(0);
                }else
                    return false;
                if(extra.isActive() && extra.getStorageType().equals(resourceNeeded) && extra.size()>0){
                    extra.remove(resourceNeeded);
                    wallet.getExtraStorage(0).remove(resourceNeeded);
                /*}else if(extra2.isActive() && extra2.getStorageType().equals(resourceNeeded) && extra2.size()>0){
                    //player might have swapped the extraStorage cards and teh extraStorage in Wallet
                    extra2.remove(resourceNeeded);
                    wallet.getExtraStorage1().remove(resourceNeeded);*/
                }else{
                    return false;
                }
            }else if(wallet.isInExtraStorage(resourceNeeded, 1)){
                StorageAbility extra = null;
                if(player.getExtraStorage().size()>1) {
                    extra = player.getExtraStorage().get(1);
                }else
                    return false;
                if(extra.isActive() && extra.getStorageType().equals(resourceNeeded) && extra.size()>0){
                    extra.remove(resourceNeeded);
                    wallet.getExtraStorage(1).remove(resourceNeeded);
                /*}else if(extra1.isActive() && extra1.getStorageType().equals(resourceNeeded) && extra1.size()>0){
                    //player might have swapped the extraStorage cards and teh extraStorage in Wallet
                    extra1.remove(resourceNeeded);
                    wallet.getExtraStorage2().remove(resourceNeeded);*/
                }else{
                    return false;
                }
            }else{
                //player hasn't allocated any right resource to activate this production
                return false;
            }

            player.addFaithPoints(1);
            return true;
        }
        return false;
    }

    @Override
    public String toString(){
        List<ProductionCard> generalCost = cost.stream().map(x -> (ProductionCard)x).collect(Collectors.toList());
        return "BoostAbility(Victory Points: " + victoryPoints + ("\nActivation Cost: " + generalCost==null ? "null" : generalCost)
                + "\nResource Needed: " + resourceNeeded + ")";
    }

    public String getNameNew(){
        return "BoostAbility";
    }
}