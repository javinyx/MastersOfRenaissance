package it.polimi.ingsw.client.model;

import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that contains all the information that the client logic need
 */
public class NubPlayer implements Comparator<NubPlayer> {
    private final String nickname;
    private int currPos;
    private boolean myTurn = false;
    private List<Deque<ConcreteProductionCard>> productionStacks = new ArrayList<>();
    private List<LeaderCard> leaders = new ArrayList<>();
    private Map<BiElement<Resource, Storage>, Integer> allResources;
    private int turnNumber;
    private Boolean[] popePasses;

    public NubPlayer(String nickname){
        this.nickname = nickname;
        this.allResources = new HashMap<>();
        popePasses = new Boolean[3];
        for(int i=0; i<3; i++){
            popePasses[i] = false;
            productionStacks.add(new ArrayDeque<>());
        }
    }

    /**
     * @param turnNumber the player's turn into the whole party order
     */
    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    /**
     * @return the array of status of each PopePass for the player: if true then it's active, otherwise inactive
     */
    public Boolean[] getPopePasses(){return popePasses;}

    /**
     * @param tileIndex is the vaticanReport id that has to be activated, must be between 1 and 3
     */
    public boolean activatePopePass(int tileIndex){
        if(tileIndex<1 || tileIndex>3) return false;
        return popePasses[tileIndex-1] = true;
    }

    public String getNickname(){return nickname;}
    public int getTurnNumber() {
        return turnNumber;
    }
    public int getCurrPos(){return currPos;}
    public boolean isMyTurn(){return myTurn;}

    /**
     * @return the list of the deck of Development cards bought by the player
     */
    public List<Deque<ConcreteProductionCard>> getProductionStacks(){return productionStacks;}
    public List<LeaderCard> getLeaders(){return leaders;}

    /**
     * @param index the index of which development cards' stack is needed (1 to 3).
     * @return
     */
    public Deque<ConcreteProductionCard> getProductionStack(int index){
        return productionStacks.get(index);
    }

    /**
     * Set true if is the player current turn
     */
    public void setMyTurn(boolean status){myTurn = status;}
    public void setCurrPos(int pos){currPos = pos;}

    /**
     * Add a resources to the player
     * @param resources The resoure and the place that he want to store it
     * @param qty how many resource he want to store
     */
    public void addResources(BiElement<Resource,Storage> resources, Integer qty){
        AtomicBoolean found = new AtomicBoolean(false);
        allResources.forEach((x,y) -> {
            if (x.equals(resources)) {
                found.set(true);
                allResources.compute(x, (k,v) -> v + qty);
            }
        });

        if(!found.get()){
            allResources.put(resources, qty);
        }
    }

    /**
     * Remove a resources to the player
     * @param resources The resoure and the place that he want to store it
     * @param qty how many resource he want to store
     */
    public void removeResources(BiElement<Resource, Storage> resources, Integer qty){
        List<BiElement<Resource, Storage>> toRemove = new ArrayList<>();

        allResources.forEach((x,y) -> {
            if(x.equals(resources)){
                allResources.compute(x, (k,v) -> v - qty);
                if(allResources.get(x)<1){
                    toRemove.add(x);
                }
            }
        });

        for(BiElement<Resource, Storage> x : toRemove){
            allResources.remove(x);
        }
    }

    /**
     * @return the elements in the warehouse of the player
     */
    public Map<BiElement<Resource, Storage>, Integer> getWarehouse(){
        Map<BiElement<Resource, Storage>, Integer> war = new HashMap<>();
        allResources.forEach((x,y) ->{
            if(x.getSecondValue().equals(Storage.WAREHOUSE_SMALL) || x.getSecondValue().equals(Storage.WAREHOUSE_MID)
                    || x.getSecondValue().equals(Storage.WAREHOUSE_LARGE)){
                war.put(x, y);
            }
        });
        return war;
    }

    /**
     * @return the number of a certain resource r in the storage s
     */
    public int getQtyInStorage(Resource r, Storage s){
        if(r==null){
            return 0;
        }
        AtomicInteger qty = new AtomicInteger(0);
        allResources.forEach((x,y) -> {
            if(x.getFirstValue().equals(r) && x.getSecondValue().equals(s)){
                qty.set(y);
            }
        });
        return qty.get();
    }

    /**
     * @return the type of the resource that is in a certain storage
     */
    public Resource getResourceFromStorage(Storage type){
        final Resource[] res = {null};
        allResources.forEach((x,y) -> {
            if(x.getSecondValue().equals(type)){
                res[0] = x.getFirstValue();
            }
        });

        return res[0];
    }

    /**
     * @return the resources in the player's Lootchest
     */
    public Map<BiElement<Resource, Storage>, Integer> getLootchest(){
        Map<BiElement<Resource, Storage>, Integer> loot = new HashMap<>();
        allResources.forEach((x,y) ->{
            if(x.getSecondValue().equals(Storage.LOOTCHEST)){
                loot.put(x, y);
            }
        });
        return loot;
    }

    /**
     * @return all the resources currently spread among the player's storages.
     */
    public Map<BiElement<Resource,Storage>,Integer> getAllResources(){return allResources;}

    public boolean setPosition(int pos){
        if(pos>0 && pos<25) {
            currPos = pos;
            return true;
        }
        return false;
    }

    /**@param card is the card to add to the player's stack
     * @param stackIndex is the index [from 0 to size()-1] of the stack in which the card goes
     */
    public boolean addProductionCard(ConcreteProductionCard card, int stackIndex){
        if(stackIndex>=productionStacks.size())
            return false;
        productionStacks.get(stackIndex).addFirst(card);
        return true;
    }

    public void setProductionStacks(List<Deque<ConcreteProductionCard>> productionStacks){
        this.productionStacks = productionStacks;
    }

    public void setLeaders(List<LeaderCard> leaders){
        this.leaders = leaders;
    }

    //---------------------COMPARATOR---------------

    /**
     * Comparator needed to order players by increasing turn number
     * @param o1 player 1
     * @param o2 player 2
     */
    @Override
    public int compare(NubPlayer o1, NubPlayer o2) {
        if(o1.getTurnNumber() < o2.getTurnNumber()){
            return -1;
        }else if(o1.getTurnNumber() == o2.getTurnNumber()){
            return 0;
        }else{
            return 1;
        }
    }

    public static Comparator<NubPlayer> getComparator(){
        return new Comparator<NubPlayer>() {
            @Override
            public int compare(NubPlayer o1, NubPlayer o2) {
                if(o1.getTurnNumber() < o2.getTurnNumber()){
                    return -1;
                }else if(o1.getTurnNumber() == o2.getTurnNumber()){
                    return 0;
                }else{
                    return 1;
                }
            }
        };
    }

}
