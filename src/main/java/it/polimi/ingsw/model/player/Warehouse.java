package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the warehouse that every Player has. You can store item in 3 type of shelf, small, middle or large
 */

public class Warehouse {

    private Resource smallInventory;
    private List<Resource> midInventory;
    private List<Resource> largeInventory;

    /**Initialize the warehouse*/

    public Warehouse() {
        smallInventory = null;
        midInventory = new ArrayList<>();
        largeInventory = new ArrayList<>();
    }

    public Warehouse(Warehouse dupe){
        this.smallInventory = dupe.smallInventory;
        this.midInventory = new ArrayList<>(dupe.midInventory);
        this.largeInventory = new ArrayList<>(dupe.largeInventory);
    }

    public Resource getSmallInventory() {
        return smallInventory;
    }

    public List<Resource> getMidInventory() {
        return midInventory;
    }

    public List<Resource> getLargeInventory() {
        return largeInventory;
    }


    /**
     * @param resource the resource to search the ID
     * @return the ID of the the shelf that contains the Resource
     */

    public int getInventoryID(Resource resource){
        if(smallInventory.equals(resource)){
            return 1;
        }if(midInventory.contains(resource)){
            return 2;
        }
        if(largeInventory.contains(resource)){
            return 3;
        }
        return -1;
    }

    /** @param item the resource of the object to store in small inventory of the warehouse*/

    public boolean addSmall(Resource item){
        if (smallInventory == null && !largeInventory.contains(item) && !midInventory.contains(item)) {
            smallInventory = item;
            return true;
        }
        return false;
    }

    /** @param item the resource of the object to store in middle inventory of the warehouse*/

    public boolean addMid(Resource item){

        if(midInventory.size() == 0) {
            if(largeInventory.contains(item) || (smallInventory!=null && smallInventory.equals(item)))
                return false;
            midInventory.add(item);
            return true;
        }

        if (midInventory.size() == 1 && item.equals(midInventory.get(0))) {
            midInventory.add(item);
            return true;
        }
        return false;
    }

    /** @param item the resource of the object to store in large inventory of the warehouse*/

    public boolean addLarge(Resource item){
        if(largeInventory.size() == 0) {
            if(midInventory.contains(item) || (smallInventory!=null && smallInventory.equals(item))){
                return false;
            }
            largeInventory.add(item);
            return true;
        }
        if (largeInventory.size() < 3 && item.equals(largeInventory.get(0))) {
            largeInventory.add(item);
            return true;
        }
        return false;
    }


    /** Remove the selected items from small warehouse space
     * @return the resource you deleted */

    public Resource removeSmall(){

        if (smallInventory != null){
            Resource res = smallInventory;
            smallInventory = null;
            return res;
        }
        else
            return null;
    }

    /** Remove the last items from mid warehouse spaces
     * @return the resource you deleted*/

    public Resource removeMid(){

        if(!midInventory.isEmpty()){
            Resource res = midInventory.get(midInventory.size() - 1);
            midInventory.remove(midInventory.size() - 1);
            return res;
        }
        else
            return null;
    }

    /** Remove the last items from large warehouse spaces
     * @return the resource you deleted*/

    public Resource removeLarge(){

        if (!largeInventory.isEmpty()) {
            Resource res = largeInventory.get(largeInventory.size() - 1);
            largeInventory.remove(largeInventory.size() - 1);
            return res;
        }
        else
            return null;
    }

    /**@param part the inventory from you want to start
     * @param arr the inventory where you want to place the Resource
     * This method move a resource from an inventory to another
     *  */

    public void moveBetweenInventory (char part, char arr){

        if (part == 's' && arr == 'm'){
            addMid(removeSmall());
        }
        else if (part == 's' && arr == 'l'){
            addLarge(removeSmall());
        }
        else if (part == 'm' && arr == 's'){
            addSmall(removeMid());
        }
        else if (part == 'm' && arr == 'l'){
            addLarge(removeMid());
        }
        else if (part == 'l' && arr == 's'){
            addSmall(removeLarge());
        }
        else if (part == 'l' && arr == 'm'){
            addMid(removeLarge());
        }

    }

    /**This method check if all inventories are rights with the rule of the game
     * @return true if check is ok*/

    public boolean check(){

        boolean checkm = false;
        boolean checkf = false;

        if(midInventory.size() != 0) {
            if (midInventory.size() == 1 || midInventory.get(0).equals(midInventory.get(1)))
                if (smallInventory == null || !smallInventory.equals(midInventory.get(0)))
                    if(largeInventory.size() == 0 || !largeInventory.get(0).equals(midInventory.get(0)))
                        checkm = true;
        }
        else
            checkm = true;

        if(largeInventory.size() != 0) {
            if (largeInventory.size() == 1 || (largeInventory.size() == 2 && largeInventory.get(0).equals(largeInventory.get(1))) || (largeInventory.get(0).equals(largeInventory.get(1)) && largeInventory.get(1).equals(largeInventory.get(2))))
                if (smallInventory == null || !smallInventory.equals(largeInventory.get(0)))
                    if (midInventory.size() == 0 || !largeInventory.get(0).equals(midInventory.get(0)))
                        checkf = true;
        }
        else
            checkf = true;

        return checkf && checkm;
    }

    /**
     * @return a list with all the items in the warehouse
     */

    public List<Resource> allInList(){
        List<Resource> items = new ArrayList<>();
        if (smallInventory != null) items.add(smallInventory);
        if (midInventory != null) items.addAll(midInventory);
        if (largeInventory != null) items.addAll(largeInventory);

        return items;
    }

    public int numberOf(Buyable res){
        int counter = 0;
        if(res instanceof Resource){
            if(smallInventory.equals(res)){
                counter++;
            }
            if(midInventory.contains(res)){
                counter += getMidInventory().size();
            }
            if(largeInventory.contains(res)){
                counter += getLargeInventory().size();
            }
        }
        return counter;
    }

}
