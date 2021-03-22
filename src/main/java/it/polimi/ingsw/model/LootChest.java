package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class LootChest {

    private List<Resource> inventory;

    public LootChest() {
        inventory = new ArrayList<>();
    }
    /**
     * Add the selected items to inventory
     */
    public void addResources(List<Resource> items){
        inventory.addAll(items);
    }
    /**
     * Remove the selected items to inventory
     */
    public void removeResources(List<Resource> items){
        inventory.removeAll(items);
    }
}
