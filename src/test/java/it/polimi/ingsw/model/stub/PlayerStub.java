package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class PlayerStub extends ProPlayer {

    public PlayerStub(String nickname, int turnID, Game game){
        super(nickname, turnID, game);
    }

    /**<li>Tier 1: COIN(x1),</li> <li>Tier 2: SERVANT(x2),</li> <li>Tier 3: SHIELD(x3)</li>*/
    public void fullWarehouseInit(){
        warehouse.addSmall(Resource.COIN);
        warehouse.addMid(Resource.SERVANT);
        warehouse.addMid(Resource.SERVANT);
        warehouse.addLarge(Resource.SHIELD);
        warehouse.addLarge(Resource.SHIELD);
        warehouse.addLarge(Resource.SHIELD);
    }

    /**Initialize warehouse just for Purple Production Card lvl 1 */
    public boolean initWarehouse(List<Resource> res){
        int distinct = (int)res.stream().distinct().count();
        if(distinct>3 || (distinct==3 && res.size()>6))
            return false;
        if(distinct==1){
            for(Resource r : res){
                warehouse.addLarge(r);
            }
            return true;
        }
        if(distinct==2){
            for(int i=0; i<2; i++){
                warehouse.addMid(res.get(0));
            }
            for(int i=0; i<2; i++){
                warehouse.addLarge(res.get(2));
            }
            return true;
        }
        warehouse.addSmall(res.get(0));
        warehouse.addMid(res.get(1));
        warehouse.addLarge(res.get(2));
        return true;
    }

    public void setLootChest(List<Resource> res){
        lootChest.addResources(res);
    }

    public void setProductionStacks(int stack, ConcreteProductionCard card){
        switch(stack){
            case 1 -> {prodCards1.add(card);}
            case 2 -> {prodCards2.add(card);}
            case 3 -> {prodCards3.add(card);}
            default -> {return;}
        }
    }

    public void setLeaderCards(LeaderCard card){
        leaderCards.add(card);
    }

    public void activateLeader(LeaderCard card){
        for(int i=0; i<leaderCards.size(); i++){
            if(leaderCards.get(i).equals(card)){
                leaderCards.get(i).setStatus(true);
            }
        }
    }

    public void disableLeader(LeaderCard card){
        for(int i=0; i<leaderCards.size(); i++){
            if(leaderCards.get(i).equals(card)){
                leaderCards.get(i).setStatus(false);
            }
        }
    }

    public void setTurnType(char type){
        turnType = type;
    }

    public void setResAcquired(List<Resource> res){
        resAcquired = res;
    }

    public void resetPosition(){
        currPos = 0;
    }
}
