package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.market.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**This class keep all the resources that arrives from the production*/
public class LootChest {

    private Map<Resource, Integer> inventory;
    protected int countResInLootchest;

    public LootChest() {
        inventory = new HashMap<>();
        inventory.put(Resource.SHIELD, null);
        inventory.put(Resource.STONE, null);
        inventory.put(Resource.COINS, null);
        inventory.put(Resource.SERVANT, null);
        countResInLootchest = 0;
    }
    public LootChest(LootChest dupe){
        this.inventory = new HashMap<>(dupe.inventory);
        this.countResInLootchest = dupe.countResInLootchest;
    }
    /**
     * @param item the item to add
     * Add selected item to inventory
     */
    public void addResources(Resource item){
        switch (item){
            case SHIELD -> inventory.compute(Resource.SHIELD, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
            case STONE -> inventory.compute(Resource.STONE, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
            case COINS -> inventory.compute(Resource.COINS, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
            case SERVANT -> inventory.compute(Resource.SERVANT, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
        }
        countResInLootchest++;
    }
    public void addResources(List<Resource> items){
        for(Resource item : items){
            switch (item){
                case SHIELD -> inventory.compute(Resource.SHIELD, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
                case STONE -> inventory.compute(Resource.STONE, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
                case COINS -> inventory.compute(Resource.COINS, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
                case SERVANT -> inventory.compute(Resource.SERVANT, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue + 1);
            }
            countResInLootchest++;
        }
    }
    /**
     * @param item the item to remove
     * Remove selected item from inventory.
     * <p>Mind that only SHIELD, STONE, COINS and SERVANT will be considered
     * removable item since there cannot be FAITH or BLANK resources in Lootchest.</p>
     */
    public void removeResources(Resource item){
        switch (item){
            case SHIELD : inventory.compute(Resource.SHIELD, (tokenKey, oldValue) -> oldValue==null ? null : oldValue - 1);
                        if(!(inventory.get(Resource.SHIELD)==null)){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.SHIELD,0,null);
                        return;
            case STONE : inventory.compute(Resource.STONE, (tokenKey, oldValue) -> oldValue == null ? null : oldValue - 1);
                        if(!(inventory.get(Resource.STONE)==null)){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.STONE,0, null);
                        return;
            case COINS : inventory.compute(Resource.COINS, (tokenKey, oldValue) -> oldValue == null ? null : oldValue - 1);
                        if(!(inventory.get(Resource.COINS)==null)){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.COINS, 0, null);
                        return;
            case SERVANT : inventory.compute(Resource.SERVANT, (tokenKey, oldValue) -> oldValue == null ? null : oldValue - 1);
                        if(!(inventory.get(Resource.SERVANT)==null)){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.SERVANT, 0, null);
                        return;
            default: return;
        }
    }
    /**
     * @param res is the Resource you want to know the number of
     * @return the number of the element
     */
    public int getNumberResInInventory(Resource res) {
        return inventory.get(res);
    }

    public Map<Resource, Integer> getInventory() {
        return inventory;
    }

    /**
     * @return the number of all the element in the inventory
     */
    public int getCountResInLootchest() {
        return countResInLootchest;
    }
}
