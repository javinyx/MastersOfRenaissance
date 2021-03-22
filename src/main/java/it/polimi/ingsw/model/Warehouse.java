package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {

    private Resource smallInventory;
    private List<Resource> midInventory;
    private List<Resource> largeInventory;

    public Warehouse() {
        midInventory = new ArrayList<>();
        largeInventory = new ArrayList<>();
    }

    /**
     * Add the selected items to small warehouse space
     */
    public void addSmall(Resource item){
        smallInventory = item;
    }
    /**
     * Add the selected items to mid warehouse spaces
     */
    public void addMid(List<Resource> items){
        //aggiungere i check per controllare che items siano uguali e 2 al massimo
        midInventory.addAll(items);
    }
    /**
     * Remove the selected items to large warehouse spaces
     */
    public void addLarge(List<Resource> items){
        //aggiungere i check per controllare che items siano uguali e 3 al massimo
        largeInventory.addAll(items);
    }

    public List<Resource> getMidInvetory(){
        if(midInventory.size()<1){
            return null;
        }
        List<Resource> contents = new ArrayList<>();
        contents.addAll(midInventory);
        return contents;
    }

    /**
     * Remove the selected items to small warehouse space
     */
    public void removeSmall(){
        smallInventory = null;
    }
    /**
     * Remove the selected items to mid warehouse spaces
     */
    public void removeMid(List<Resource> items){
        midInventory.removeAll(items);
    }
    /**
     * Remove the selected items to large warehouse spaces
     */
    public void removeLarge(List<Resource> items){
        largeInventory.removeAll(items);
    }
}
