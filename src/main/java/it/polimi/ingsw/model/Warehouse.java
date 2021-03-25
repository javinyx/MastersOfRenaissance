package it.polimi.ingsw.model;

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

    public Resource getSmallInventory() {
        return smallInventory;
    }

    public List<Resource> getMidInventory() {
        return midInventory;
    }

    public List<Resource> getLargeInventory() {
        return largeInventory;
    }

    /** @param item the resource of the object to store in small inventory of the warehouse*/

    public void addSmall(Resource item){
        if (smallInventory == null)
            smallInventory = item;
        else
            //throw ex
            return;

    }

    /** @param item the resource of the object to store in middle inventory of the warehouse*/

    public void addMid(Resource item){

        if(midInventory.size() == 0)
            midInventory.add(item);

        else {

            if (midInventory.size() == 1)
                midInventory.add(item);

            else
                //throw ex
                return;
        }
    }

    /** @param item the resource of the object to store in large inventory of the warehouse*/

    public void addLarge(Resource item){
        if(largeInventory.size() == 0)
            largeInventory.add(item);

        else {

            if (largeInventory.size() < 3)
                largeInventory.add(item);

            else
                //throw ex
                return;
        }
    }


    /** Remove the selected items to small warehouse space
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

    /** Remove the selected items to mid warehouse spaces
     * @param index the position of the Resource to remove
     *  @return the resource you deleted*/

    public Resource removeMid(int index){

        if(midInventory != null){
            Resource res = midInventory.get(index);
            midInventory.remove(index);
            return res;
        }
        else
            return null;
    }

    /** Remove the selected items to large warehouse spaces
     * @param index the position of the Resource to remove
     * @return the resource you deleted*/

    public Resource removeLarge(int index){

        if (largeInventory != null) {
            Resource res = largeInventory.get(index);
            largeInventory.remove(index);
            return res;
        }
        else
            return null;
    }

    /**@param part the inventory from you want to start
     * @param arr the inventory where you want to place the Resource
     * This method move a resource from an inventory to another
     *  */

    public void moveBetweenInventory (int pos, char part, char arr){

        if (part == 's' && arr == 'm'){
            addMid(removeSmall());
        }
        else if (part == 's' && arr == 'l'){
            addLarge(removeSmall());
        }
        else if (part == 'm' && arr == 's'){
            addSmall(removeMid(pos));
        }
        else if (part == 'm' && arr == 'l'){
            addLarge(removeMid(pos));
        }
        else if (part == 'l' && arr == 's'){
            addSmall(removeLarge(pos));
        }
        else if (part == 'l' && arr == 'm'){
            addMid(removeLarge(pos));
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

}
