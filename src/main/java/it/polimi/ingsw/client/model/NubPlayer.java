package it.polimi.ingsw.client.model;

import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.util.*;

public class NubPlayer implements Comparator<NubPlayer> {
    private final String nickname;
    private int currPos;
    private boolean myTurn = false;
    private List<Deque<ConcreteProductionCard>> productionStacks;
    private List<LeaderCard> leaders;
    private Map<BiElement<Resource, Storage>, Integer> allResources;
    private int turnNumber;
    private Boolean[] popePasses;

    public NubPlayer(String nickname){
        this.nickname = nickname;
        this.allResources = new HashMap<>();
        popePasses = new Boolean[3];
        for(int i=0; i<3; i++){
            popePasses[i] = false;
        }
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public Boolean[] getPopePasses(){return popePasses;}

    /**
     * @param tileIndex is the vaticanReport id that has to be activated, must be between 1 and 3
     */
    public boolean activatePopePass(int tileIndex){
        if(tileIndex<1 || tileIndex>3) return false;
        return popePasses[tileIndex-1] = true;
    }

    public String getNickname(){return nickname;}
    public int getCurrPos(){return currPos;}
    public boolean isMyTurn(){return myTurn;}
    public List<Deque<ConcreteProductionCard>> getProductionStacks(){return productionStacks;}
    public List<LeaderCard> getLeaders(){return leaders;}
    public Deque<ConcreteProductionCard> getProductionStack(int index){
        return productionStacks.get(index);
    }

    public void setMyTurn(boolean status){myTurn = status;}
    public void setCurrPos(int pos){currPos = pos;}

    public void addResources(BiElement<Resource,Storage> resources, Integer qty){
        if(allResources.containsKey(resources)){
            allResources.compute(resources, (k,v) -> v + qty);
        }else{
            allResources.put(resources, 1);
        }
    }

    public void removeResources(BiElement<Resource, Storage> resources, Integer qty){
        if(allResources.containsKey(resources)){
            allResources.compute(resources, (k,v) -> v - qty);
            if(allResources.get(resources)<1){
                allResources.remove(resources);
            }
        }
    }

    public Map<BiElement<Resource,Storage>,Integer> getAllResources(){return allResources;}
    public boolean setPosition(int pos){
        if(pos>0 && pos<25) {
            currPos = pos;
            return true;
        }
        return false;
    }

    /**@param card is the card to add to the player's stack
     * @param stackIndex is the index [from 0 to size()-1] of the stack in which the card goes*/
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
