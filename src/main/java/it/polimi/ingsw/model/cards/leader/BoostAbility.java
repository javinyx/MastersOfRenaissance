package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.player.Warehouse;

import java.util.ArrayList;
import java.util.List;

/**This LeaderCard works similarly to a ProductionCard: it has a production power that players can decide to activate
 * if they have the card and the latter is active.
 * <p>In order to activate the card, players must have all the production cards specified in {@code cost}, even if they're
 * hidden.
 * Once it's active (use {@code isActive()} method to discover its status)</p>*/
public class BoostAbility implements LeaderCard {

    private final int victoryPoints;
    private final List<ProductionCard> cost;
    private boolean status;
    private Resource resourceNeeded;

    public BoostAbility(int victoryPoints, List<ProductionCard> cost, Resource resourceNeeded) {
        this.victoryPoints = victoryPoints;
        this.cost = new ArrayList<>();
        this.cost.addAll(cost);
        status = false;
        this.resourceNeeded = resourceNeeded;
    }

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

            }else if(wallet.isInExtraStorage1Tray(resourceNeeded)){
                StorageAbility extra1 = player.getExtraStorage1();
                StorageAbility extra2 = player.getExtraStorage2();
                if(extra1.isActive() && extra1.getStorageType().equals(resourceNeeded) && extra1.size()>0){
                    extra1.remove(resourceNeeded);
                    wallet.getExtraStorage1().remove(resourceNeeded);
                /*}else if(extra2.isActive() && extra2.getStorageType().equals(resourceNeeded) && extra2.size()>0){
                    //player might have swapped the extraStorage cards and teh extraStorage in Wallet
                    extra2.remove(resourceNeeded);
                    wallet.getExtraStorage1().remove(resourceNeeded);*/
                }else{
                    return false;
                }
            }else if(wallet.isInExtraStorage2Tray(resourceNeeded)){
                StorageAbility extra2 = player.getExtraStorage2();
                StorageAbility extra1 = player.getExtraStorage1();
                if(extra2.isActive() && extra2.getStorageType().equals(resourceNeeded) && extra2.size()>0){
                    extra2.remove(resourceNeeded);
                    wallet.getExtraStorage2().remove(resourceNeeded);
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
}