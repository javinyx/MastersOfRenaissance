package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

public class ResourcesWallet {
    private List<Resource> warehouseTray, lootchestTray, extraStorage1, extraStorage2;

    public ResourcesWallet(){
        warehouseTray = new ArrayList<>();
        lootchestTray = new ArrayList<>();
        extraStorage1 = new ArrayList<>();
        extraStorage2 = new ArrayList<>();
    }

    public void setWarehouseTray(List<Resource> resources){
        warehouseTray.addAll(resources);
    }

    public void setLootchestTray(List<Resource> resources){
        lootchestTray.addAll(resources);
    }

    public void setExtraStorage1(List<Resource> resources){
        if(resources.size()>2){
            throw new IllegalArgumentException("Too many arguments");
        }
        if((int)resources.stream().distinct().count()>1){
            throw new IllegalArgumentException("Different types of resources");
        }
        if(!extraStorage1.isEmpty() && !extraStorage1.get(0).equals(resources.get(0))){
            throw new IllegalArgumentException("Different type of resources: "+ extraStorage1.get(0)+" already present");
        }
        extraStorage1.addAll(resources);
    }

    public void setExtraStorage2(List<Resource> resources){
        if(resources.size()>2){
            throw new IllegalArgumentException("Too many arguments");
        }
        if((int)resources.stream().distinct().count()>1){
            throw new IllegalArgumentException("Different types of resources");
        }
        if(!extraStorage2.isEmpty() && !extraStorage2.get(0).equals(resources.get(0))){
            throw new IllegalArgumentException("Different type of resources: "+ extraStorage2.get(0)+" already present");
        }
        extraStorage2.addAll(resources);
    }

    public List<Resource> getWarehouseTray(){
        return warehouseTray;
    }

    public List<Resource> getLootchestTray(){
        return lootchestTray;
    }

    public List<Resource> getExtraStorage1(){
        return extraStorage1;
    }

    public List<Resource> getExtraStorage2(){return extraStorage2;}

    public boolean isInWarehouseTray(Resource resource){
        return warehouseTray.contains(resource);
    }

    public boolean isInLootChestTray(Resource resource){
        return lootchestTray.contains(resource);
    }

    public boolean isInExtraStorage1Tray(Resource resource){
        return extraStorage1.contains(resource);
    }

    public boolean isInExtraStorage2Tray(Resource resource){
        return extraStorage2.contains(resource);
    }

    public boolean anyFromWarehouseTray(){return warehouseTray!=null;}
    public boolean anyFromLootchestTray(){return lootchestTray!=null;}
    public boolean anyFromStorageCard1(){return extraStorage1!=null;}
    public boolean anyFromStorageCard2(){return extraStorage2!=null;}
}
