package it.polimi.ingsw.model;

import java.util.List;

public class ProPlayer extends Player{
    //private Warehouse warehouse;
    //private LootChest lootChest;
    private List<ProductionCard> prodCards;
    private int turnID;

    public ProPlayer(String nickname, int turnID, Game game){
        super(nickname, game);
        this.turnID = turnID;
    }

    public int getTurnID(){
        return turnID;
    }
    public void buyProductionCard(ProductionCard card){
    }

    public int getVictoryPoints(){
        int victoryPoints = 0;
        //sum all victory points from prodCards, leaderCards, faithTrack, Resources...
        return victoryPoints;
    }

    public void buyFromMarket(char dim, int index){
        Market market = game.getMarket();
        List<Resource> goodies = null;
        if(dim == 'c'){
            goodies = market.chooseColumn(index);
        }else if(dim == 'r'){
            goodies = market.chooseRow(index);
        }

        storeInWarehouse(goodies);
    }


    public void storeInWarehouse(List<Resource> resources){
        //logic that follows players needs on how to display resources in warehouse
        //call warehouse methods once the way has been decided
    }

    /**Add the specified quantity of Faith Points causing the player to move forward on the board.
     * <p>If the movement causes a Vatican Report or the end of the match, the Game will be notified.</p>*/
    public void addFaithPoints(int quantity){
        int newPos = currPos + quantity;
        int report = 0;
        if(currPos < 8 && newPos >= 8){
            report = 1;
        }else if(currPos < 16 && newPos >= 16){
            report = 2;
        }else if(currPos < 24 && newPos >= 24){
            report = 3;
        }
        currPos = (report==3) ? 24 : newPos;

        observer.updatePosition(this);
        if(report!=0){
            observer.alertVaticanReport(this, report);
        }
        if(report == 3){
            observer.updateEnd(this);
        }
    }

    /**Given a vaticanReport (must be in 1-3 range), the method tells if the player is in a safe zone.
     * If it returns true, then the player should activate the pope pass relating that zone.*/
    public boolean isInRangeForReport(int vaticanReport){
        switch(vaticanReport) {
            case 1 : return currPos>4;
            case 2 : return currPos>11;
            case 3 : return currPos>18;
            default : return false;
        }
    }
}
