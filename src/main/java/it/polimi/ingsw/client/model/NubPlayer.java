package it.polimi.ingsw.client.model;

import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.misc.TriElement;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class NubPlayer {
    private final String nickname;
    private final int turnID;
    private int currPos;
    private boolean isMyTurn = false;
    private List<Deque<ConcreteProductionCard>> productionStacks;
    private List<LeaderCard> leaders;
    private List<TriElement<Resource, Storage, Integer>> allResources;

    public NubPlayer(String nickname, int turnId){
        this.nickname = nickname;
        this.turnID = turnId;
        this.allResources = new ArrayList<>();
    }

    public String getNickname(){return nickname;}
    public int getTurnID(){return turnID;}
    public int getCurrPos(){return currPos;}
    public boolean isMyTurn(){return isMyTurn;}
    public List<Deque<ConcreteProductionCard>> getProductionStacks(){return productionStacks;}
    public List<LeaderCard> getLeaders(){return leaders;}
    public Deque<ConcreteProductionCard> getProductionStack(int index){
        return productionStacks.get(index);
    }

    public void setMyTurn(boolean status){isMyTurn = status;}
    public void setCurrPos(int pos){currPos = pos;}

    /**Clears all the resources owned by the player and add the new ones.*/
    public void setAllResources(List<TriElement<Resource,Storage, Integer>> resources){
        allResources.clear();
        allResources.addAll(resources);
    }
    public void addResources(TriElement<Resource,Storage,Integer> resources){this.allResources.add(resources);}
    public void addResources(List<TriElement<Resource,Storage,Integer>> resources){this.allResources.addAll(resources);}

    public List<TriElement<Resource,Storage,Integer>> getAllResources(){return allResources;}
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

}
