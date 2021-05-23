package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * This LeaderCard gives you an extra special 2-slot depot. This special depot can only store the indicated Resources.
 * You can also store the same type of Resource in a basic Warehouse depot.
 * <p>In order to activate the card, players must have all the production cards specified in {@code cost}, even if they're
 * hidden.
 * Use the {@code isActive()} method to discover its status.</p>
 */
public class StorageAbility implements LeaderCard {
        private int id;
        private final int victoryPoints;
        private final List<Resource> cost;
        private boolean status;
        private final Resource storageType;
        private Resource[] resources;

    /**
     * Instantiate a new Storage Ability Leader Card.
     *
     * @param id             the id of the card
     * @param victoryPoints  the victory points
     * @param cost           the cost in Resources
     * @param storageType   the storage type
     */
    public StorageAbility(int id, int victoryPoints, List<Resource> cost, Resource storageType) {
            this.id = id;
            this.victoryPoints = victoryPoints;
            this.cost = new ArrayList<>(cost);
            status = false;
            this.storageType = storageType;
            this.resources = new Resource[2];
        }

    /**
     * Dupe a StorageAbility Card.
     *
     * @param dupe the StorageAbility Card to dupe
     */
    public StorageAbility(StorageAbility dupe){
            this.victoryPoints = dupe.victoryPoints;
            this.cost = new ArrayList<>(dupe.cost);
            this.status = dupe.status;
            this.storageType = dupe.storageType;
            this.resources = new Resource[2];
            this.resources[0] = dupe.resources[0];
            this.resources[1] = dupe.resources[1];
        }

        @Override
        public boolean isActive() {
            return status;
        }

        @Override
        public void setStatus(boolean activate) {
            status = activate;
        }

        @Override
        public int getVictoryPoints() {
            return victoryPoints;
        }

        @Override
        public List<Buyable> getCost() {
            return new ArrayList<>(cost);
        }

        public boolean applyEffect(ProPlayer player) {
            player.setExtraStorage(this);
            return true;
        }

    /**
     * Get storage type resource.
     *
     * @return the resource
     */
    public Resource getStorageType(){ return storageType; }

    /**
     * Remove the specified resource from the extra storage, if it's possible. This happens if there are actually
     * resources stored in the card and if {@code resource}'s type matches the storage type offered by the card.
     *
     * @param resource the resource
     * @return true if it has remove the resource successfully, otherwise false.
     */
    public boolean remove(Resource resource){
            if(resources[1]!=null && resources[1].equals(resource)){
                resources[1] = null;
                return true;
            }
            if(resources[0]!=null && resources[0].equals(resource)) {
                resources[0] = null;
                return true;
            }
            return false;
        }

    /**
     * Add the specified resource to the storage. The Resource cannot be added if the card is inactive
     * or if the resource type is not correct.
     *
     * @param resource the resource to be added
     * @return true if resource can be added, false otherwise
     */
    public boolean add(Resource resource){
            if(!storageType.equals(resource) && !status){
                //cannot add if the card is inactive or if the resource type is not correct
                return false;
            }
            switch(size()){
                case 0 : resources[0] = resource;
                        return true;
                case 1 : resources[1] = resource;
                        return true;
                default : return false;
            }
        }

    /**
     * Size of the StorageAbility Card.
     *
     * @return how many slots are actually full. In other words, how many elements are stored in the card when the method is called.
     */
    public int size(){
            if(resources[0]!=null && resources[1]!=null)
                return 2;
            if(resources[0]!=null)
                return 1;
            return 0;
        }

        @Override
        public String toString(){
            return "StorageAbility(Victory Points: " + victoryPoints + "\nActivation Cost: " +
                    (/*cost==null ? "null" :*/ cost) + "\nStorage Type: " + storageType + "\nContents: " + resources + ")";
        }


        public String get0(){
            if (resources[0] != null )
                return resources[1].toString();
            return null;
        }
    public String get1(){
        if (resources[1] != null )
            return resources[1].toString();
        return null;
    }

    public String getNameNew(){
        return "StorageAbility";
    }

    public int getId() {
        return id;
    }

    public boolean isStatus() {
        return status;
    }

}
