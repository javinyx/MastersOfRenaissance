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
        inventory = new HashMap<>(4);
        inventory.put(Resource.SHIELD, 0);
        inventory.put(Resource.STONE, 0);
        inventory.put(Resource.COINS, 0);
        inventory.put(Resource.SERVANT, 0);
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
        switch(item){
            case SHIELD -> inventory.compute(Resource.SHIELD, (tokenKey, oldValue) -> oldValue += 1);
            case STONE -> inventory.compute(Resource.STONE, (tokenKey, oldValue) -> oldValue += 1);
            case COINS -> inventory.compute(Resource.COINS, (tokenKey, oldValue) -> oldValue += 1);
            case SERVANT -> inventory.compute(Resource.SERVANT, (tokenKey, oldValue) -> oldValue += 1);
        }
        countResInLootchest++;
    }
    public void addResources(List<Resource> items){
        for(Resource item : items){
            addResources(item);
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
            case SHIELD : inventory.compute(Resource.SHIELD, (tokenKey, oldValue) -> oldValue -= 1);
                        if(inventory.get(Resource.SHIELD)>=0){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.SHIELD,-1,0);
                        return;
            case STONE : inventory.compute(Resource.STONE, (tokenKey, oldValue) -> oldValue -= 1);
                        if(inventory.get(Resource.STONE)>=0){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.STONE,-1, 0);
                        return;
            case COINS : inventory.compute(Resource.COINS, (tokenKey, oldValue) -> oldValue -= 1);
                        if(inventory.get(Resource.COINS)>=0){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.COINS, -1, 0);
                        return;
            case SERVANT : inventory.compute(Resource.SERVANT, (tokenKey, oldValue) -> oldValue -= 1);
                        if(inventory.get(Resource.SERVANT)>=0){
                            countResInLootchest--;
                        }
                        inventory.replace(Resource.SERVANT, -1, 0);
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
