package it.polimi.ingsw.model;

import java.util.List;

public class ProPlayer extends Player{
    private Warehouse warehouse;
    private LootChest lootChest;
    private List<ProductionCard> prodCards;
    private int turnID;

    public ProPlayer(String nickname, int turnID, Game game){
        super(nickname, game);
        this.turnID = turnID;
        warehouse = new Warehouse();
        lootChest = new LootChest();
        if(turnID == 3 || turnID == 4){
            currPos++;
            chooseExtraResource();
            if(turnID == 4){
                chooseExtraResource();
            }
        }
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
    /**Obtains the resources chosen from market through a column or a row.
     * <p>Add faith points to the player if a red marble ha been drawn.<p>
     * @param dim 'c' for column, 'r' for row.
     * @param index range 1-4 for column, 1-3 for row*/
    public void buyFromMarket(char dim, int index){
        if(dim!='c' && dim!='r'){
            //throw ex
            return;
        }
        Market market = game.getMarket();
        List<Resource> goodies = null;
        if(dim == 'c'){
            if(index<1 || index>4){
                //throw ex
                return;
            }
            goodies = market.chooseColumn(index);
        }else if(dim == 'r'){
            if(index<1 || index>3){
                //throw ex
                return;
            }
            goodies = market.chooseRow(index);
        }
        //there's just one red marble in market so 1 faith points at max for each draw
        if(goodies.contains(Resource.FAITH)){
            addFaithPoints(1);
            goodies.remove(Resource.FAITH);
        }
    }

    public void discardResources(List<Resource> resources){
        for(Resource resource : resources){
            observer.alertDiscardResource(this);
        }
    }

    /**Let the player choose an extra resource to add during initialization phase.*/
    private void chooseExtraResource(){
        //wait for the player to choose a resource
        //then add to warehouse
    }

    /**Place the resources in the specified warehouse tier.
     * @param resources list of resoruces the player wants to store
     * @param tier Warehouse inventory shelf's id on which the player want to place {@code resources}*/
    public void storeInWarehouse(List<Resource> resources, int tier){

        switch(tier){
            //case 1 : warehouse.addSmall(resources);
            //          break;
            case 2 : warehouse.addMid(resources);
                        break;
            case 3 : warehouse.addLarge(resources);
            default : return;
        }
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
