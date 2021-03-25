package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.HashMap;
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
    /**
     * @param item the item to remove
     * Remove selected items from inventory
     */
    public void removeResources(Resource item){
        switch (item){
            case SHIELD -> inventory.compute(Resource.SHIELD, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue - 1);
            case STONE -> inventory.compute(Resource.STONE, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue - 1);
            case COINS -> inventory.compute(Resource.COINS, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue - 1);
            case SERVANT -> inventory.compute(Resource.SERVANT, (tokenKey, oldValue) -> oldValue == null ? 1 : oldValue - 1);
        }
        countResInLootchest--;
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
