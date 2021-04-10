package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

public class ResourcesWallet {
    private List<Resource> warehouseTray, lootchestTray, extraStorage;

    public ResourcesWallet(){
        warehouseTray = new ArrayList<>();
        lootchestTray = new ArrayList<>();
        extraStorage = new ArrayList<>();
    }

    public void setWarehouseTray(List<Resource> resources){
        warehouseTray.addAll(resources);
    }

    public void setLootchestTray(List<Resource> resources){
        lootchestTray.addAll(resources);
    }

    public void setExtraStorage(List<Resource> resources){
        extraStorage.addAll(resources);
    }

    public List<Resource> getWarehouseTray(){
        return warehouseTray;
    }

    public List<Resource> getLootchestTray(){
        return lootchestTray;
    }

    public List<Resource> getExtraStorage(){
        return extraStorage;
    }

    public boolean fromWarehouse(Resource resource){
        return warehouseTray.contains(resource);
    }

    public boolean fromLootChest(Resource resource){
        return lootchestTray.contains(resource);
    }

    public boolean fromExtraStorage(Resource resource){
        return extraStorage.contains(resource);
    }

    /**Tells if the ExtraStorage field holds 2 different types of resources, meaning that the player wants
     * to use 2 different Storage Ability LeaderCard*/
    public boolean isExtraStorageDouble(){
        if((int)(extraStorage.stream().distinct().count())==2){
            return true;
        }
        return false;
    }
}
