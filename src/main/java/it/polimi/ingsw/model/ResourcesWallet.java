package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;

public class ResourcesWallet {
    private List<Resource> warehouseTray, lootchestTray;
    private List<List<Resource>> extraStorage;

    public ResourcesWallet(){
        warehouseTray = new ArrayList<>();
        lootchestTray = new ArrayList<>();
        extraStorage = new ArrayList<>();
    }

    public boolean setWarehouseTray(List<Resource> resources){
        if(resources==null || resources.isEmpty() || !areValidResources(resources)){
            return false;
        }
        warehouseTray.addAll(resources);
        return true;
    }

    public boolean setLootchestTray(List<Resource> resources){
        if (resources==null || resources.isEmpty() || !areValidResources(resources)) {
            return false;
        }
        lootchestTray.addAll(resources);
        return true;
    }

    private boolean areValidResources(List<Resource> res){
        if(res.contains(Resource.FAITH) || res.contains(Resource.BLANK)){
            return false;
        }
        return true;
    }

    /**Set {@code resources} as the list of resources to remove in a buy phase from a StorageAbility card.
     * The index specifies for which card. In fact, this method can be used to add {@code resources} to an
     * already existing list of resources for the same card.
     * <p>The method checks if {@code resources} are valid as currency or as a choice production. More in details,
     * it checks that the list doesn't contain FAITH or BLANK type.</p>
     * @param resources list of resources to add
     * @param index must be from 0 to (maxNumExtraStorage-1), so it must be consistent to how much leaderCard
     * a ProPlayer can obtain.
     * @return true if the setting has been done correctly, false otherwise. Mind that when {@code resources} contains
     * different types of resources, it will return false since a
     * StorageAbilityCard cannot hold more than 1 distinct type and not more than 2 resources. For this reason, the exception
     * will also be thrown if it's a sequential call on an already existing deposit tries to add more resources whose
     * type is different from the ones already in the deposit.*/
    public boolean setExtraStorage(List<Resource> resources, int index){
        if(resources==null || resources.isEmpty() ||!areValidResources(resources) || index>=ProPlayer.getMaxNumExtraStorage() || index<0){
            return false;
        }
        if(resources.stream().distinct().count()>1 || resources.size()>2){
            return false;
        }
        List<List<Resource>> l;
        if(this.extraStorage.isEmpty() && index!=0){
            return false;
            //first resources for first card
        }else if(index< this.extraStorage.size()){
            //add resources for an already existing card
            if(!extraStorage.get(index).get(0).equals(resources.get(0))){
                return false;
            }
            this.extraStorage.get(index).addAll(resources);
        }else if(index == this.extraStorage.size()){
            //add resources for new card
            extraStorage.add(new ArrayList<>(resources));
        }else{
            return false;
        }
        return true;
    }


    public List<Resource> getWarehouseTray(){
        return warehouseTray;
    }

    public List<Resource> getLootchestTray(){
        return lootchestTray;
    }

    /**@param index must be from 0 to {@code extraStorageSize()-1}
     * @return the list of resources in the deposit at the specified index, or null if that index doesn't exist.*/
    public List<Resource> getExtraStorage(int index){
        if(extraStorage.size()>index && index>=0 && index<extraStorageSize()){
            return extraStorage.get(index);
        }
        return new ArrayList<>();
    }

    public int extraStorageSize(){
        return extraStorage.size();
    }


    public boolean isInWarehouseTray(Resource resource){
        if(resource==null || !resource.isValidForTrading()){
            return false;
        }
        return warehouseTray.contains(resource);
    }

    public boolean isInLootChestTray(Resource resource){
        if(resource==null || !resource.isValidForTrading()){
            return false;
        }
        return lootchestTray.contains(resource);
    }

    /**@param index from 0 to {@code extraStorageSize()-1}
     * @return true if the {@code resource} is in the deposit specified */
    public boolean isInExtraStorage(Resource resource, int index){
        if(resource==null || !resource.isValidForTrading() || index<0 || index>=extraStorage.size()){
            return false;
        }
        return extraStorage.get(index).contains(resource);
    }


    public boolean anyFromWarehouseTray(){return warehouseTray.size()>0;}
    public boolean anyFromLootchestTray(){return lootchestTray.size()>0;}
    public boolean anyFromExtraStorage(){return extraStorage.size()>0;}
    public boolean anyFromExtraStorage(int index){
        if(extraStorage.isEmpty()){
            return false;
        }
        return index<extraStorage.size() && !extraStorage.get(index-1).isEmpty();
    }

    public boolean isEmpty(){
        return warehouseTray.isEmpty() && lootchestTray.isEmpty() && extraStorage.isEmpty();
    }
}
