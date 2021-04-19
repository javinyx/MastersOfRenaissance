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
