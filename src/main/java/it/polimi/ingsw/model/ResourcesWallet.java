package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.exception.BadStorageException;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResourcesWallet {
    private Optional<List<Resource>> warehouseTray, lootchestTray;
    private Optional<List<List<Resource>>> extraStorage;

    public ResourcesWallet(){
        warehouseTray = Optional.empty();
        lootchestTray = Optional.empty();
        extraStorage = Optional.empty();
    }

    public boolean setWarehouseTray(List<Resource> resources){
        if(resources==null || resources.isEmpty() || !areValidResources(resources)){
            return false;
        }
        if(warehouseTray.isEmpty()){
            warehouseTray = Optional.of(new ArrayList<>(resources));
        }else {
            warehouseTray.map(x -> x.addAll(resources));
        }
        return true;
    }

    public boolean setLootchestTray(List<Resource> resources){
        if (resources==null || resources.isEmpty() || !areValidResources(resources)) {
            return false;
        }
        if(lootchestTray.isEmpty()){
            lootchestTray = Optional.of(new ArrayList<>(resources));
        }else {
            lootchestTray.map(x -> x.addAll(resources));
        }
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
     * @return true if the setting has been done correctly, false otherwise
     * @throws BadStorageException when {@code resources} contains different types of resources since a
     * StorageAbilityCard cannot hold more than 1 distinct type and not more than 2 resources. For this reason, the exception
     * will also be thrown if it's a sequential call on an already existing deposit tries to add more resources whose
     * type is different from the ones already in the deposit.*/
    public boolean setExtraStorage(List<Resource> resources, int index) /*throws BadStorageException*/ {
        if(resources==null || resources.isEmpty() ||!areValidResources(resources) || index>=ProPlayer.getMaxNumExtraStorage() || index<0){
            return false;
        }
        if(resources.stream().distinct().count()>1 || resources.size()>2){
            //throw new BadStorageException();
            return false;
        }
        List<List<Resource>> l;
        if(this.extraStorage.isEmpty()){
            if(index!=0){
                return false;
            }
            //first resources for first card
            l = new ArrayList<>();
            l.add(new ArrayList<>(resources));
            this.extraStorage = Optional.of(l);
        }else if(index< this.extraStorage.get().size()){
            //add resources for an already existing card
            if(!extraStorage.get().get(index).get(0).equals(resources.get(0))){
                //throw new BadStorageException();
                return false;
            }
            this.extraStorage.map(x -> x.get(index).addAll(resources));
        }else if(index == this.extraStorage.get().size()){
            //add resources for new card
            l = new ArrayList<>(this.extraStorage.get());
            l.add(resources);
            extraStorage = Optional.of(l);
        }else{
            return false;
        }
        return true;
    }


    public List<Resource> getWarehouseTray(){
        return warehouseTray.orElse(new ArrayList<>());
    }

    public List<Resource> getLootchestTray(){
        return lootchestTray.orElse(new ArrayList<>());
    }

    /**@param index must be from 0 to {@code extraStorageSize()-1}
     * @return the list of resources in the deposit at the specified index, or null if that index doesn't exist.*/
    public List<Resource> getExtraStorage(int index){
        if(extraStorage.isPresent() && index>=0 && index<extraStorageSize()){
            return extraStorage.get().get(index);
        }
        return new ArrayList<>();
    }

    public int extraStorageSize(){
        if(extraStorage.isPresent()){
            return extraStorage.get().size();
        }
        return 0;
    }


    public boolean isInWarehouseTray(Resource resource){
        if(resource==null || !resource.isValidForTrading()){
            return false;
        }
        if(warehouseTray.isPresent()){
            return warehouseTray.get().contains(resource);
        }
        return false;
    }

    public boolean isInLootChestTray(Resource resource){
        if(resource==null || !resource.isValidForTrading()){
            return false;
        }
        if(lootchestTray.isPresent()){
            return lootchestTray.get().contains(resource);
        }
        return false;
    }

    /**@param index from 0 to {@code extraStorageSize()-1}
     * @return true if the {@code resource} is in the deposit specified */
    public boolean isInExtraStorage(Resource resource, int index){
        if(resource==null || !resource.isValidForTrading() || index<0){
            return false;
        }
        if(extraStorage.isPresent() && index<extraStorage.get().size()){
            return extraStorage.get().get(index).contains(resource);
        }
        return false;
    }


    public boolean anyFromWarehouseTray(){return warehouseTray.isPresent();}
    public boolean anyFromLootchestTray(){return lootchestTray.isPresent();}
    public boolean anyFromExtraStorage(){return extraStorage.isPresent();}
    public boolean anyFromExtraStorage(int index){
        if(extraStorage.isEmpty()){
            return false;
        }
        return index<extraStorage.get().size() && !extraStorage.get().get(index-1).isEmpty();
    }
    /*public boolean anyFromStorageCard1(){return extraStorage1!=null;}
    public boolean anyFromStorageCard2(){return extraStorage2!=null;}*/

    public boolean isEmpty(){
        if(warehouseTray.isEmpty() && lootchestTray.isEmpty() && extraStorage.isEmpty()){
            return true;
        }
        return false;
    }
}
