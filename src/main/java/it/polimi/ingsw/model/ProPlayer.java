package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class ProPlayer extends Player{
    private final Warehouse warehouse;
    private final LootChest lootChest;
    private List<ProductionCard> prodCards;
    private List<LeaderCard> leaderCards;
    private final int turnID;
    private ArrayList<PopePass> passes;
    private List<Resource> resAcquired;
    private char turnType;
    private StorageAbility extraStorage1, extraStorage2;

    public ProPlayer(String nickname, int turnID, Game game){
        super(nickname, game);
        this.turnID = turnID;
        warehouse = new Warehouse();
        lootChest = new LootChest();
        prodCards = new ArrayList<>();
        leaderCards = new ArrayList<>();
        resAcquired = null;
        extraStorage1 = null;
        extraStorage2 = null;
        passes = new ArrayList<>(3);
        passes.add(0, new PopePass(1));
        passes.add(1, new PopePass(2));
        passes.add(2, new PopePass(3));
        if(turnID == 3 || turnID == 4){
            currPos++;
            chooseResource();
            if(turnID == 4){
                chooseResource();
            }
        }
    }

    //m == buymarket, b == buyproduction, p == activateProduction
    public char getTurnType() {
        return turnType;
    }

    public int getTurnID(){
        return turnID;
    }
    public void buyProductionCard(ProductionCard card){
    }

    /**Returns the sum of player's victory points taking in consideration:
     * <li>productionCards (hidden or not); </li>
     * <li>leaderCards if active;</li>
     * <li>popePasses if active;</li>
     * <li>current position on board;</li>
     * <li>all resources stored in warehouse,lootchest and StorageAbility LeaderCards. </li>*/
    public int getVictoryPoints(){
        int victoryPoints = 0;
        //sum all victory points from prodCards, leaderCards, faithTrack, Resources...
        for(ProductionCard pc : prodCards){
            victoryPoints += pc.getVictoryPoints();
        }
        for(LeaderCard lc : leaderCards){
            if(lc.isActive())
                victoryPoints += lc.getVictoryPoints();
        }
        for(PopePass pp : passes){
            if(pp.isActive())
                victoryPoints += pp.getVictoryPoints();
        }

        victoryPoints += victoryPointsFromPos();

        Function<StorageAbility, Integer> pointsFromExtraStorage = (StorageAbility x) -> {int points=0;
            if(x!=null){
                if(x.isFullFlag1())
                    points++;
                if(x.isFullFlag2())
                    points++;
            }
            return points;
        };

        victoryPoints += (lootChest.getCountResInLootchest() + warehouse.getMidInventory().size()
                        + warehouse.getLargeInventory().size() + (warehouse.getSmallInventory()!=null ? 1 : 0)
                        + pointsFromExtraStorage.apply(extraStorage1) + pointsFromExtraStorage.apply(extraStorage2))/5;

        return victoryPoints;
    }

    private int victoryPointsFromPos(){
        int victoryPoints = 0;
        switch(currPos){
            case 24: victoryPoints +=20;
            case 23: case 22: case 21: victoryPoints +=16;
            case 20: case 19: case 18: victoryPoints += 12;
            case 17: case 16: case 15: victoryPoints += 9;
            case 14: case 13: case 12: victoryPoints += 6;
            case 11: case 10: case 9: victoryPoints += 4;
            case 8: case 7: case 6: victoryPoints += 2;
            case 5: case 4: case 3: victoryPoints += 1;
            default : return victoryPoints;
        }
    }

    /**Get all the resources just bought from the market by the player. */
    public List<Resource> getResAcquired(){
        return resAcquired;
    }

    public Warehouse getWarehouse(){
        return warehouse;
    }

    public LootChest getLootChest(){
        return lootChest;
    }

    /**Obtains the resources chosen from market by column or row.
     * <p>Add faith points to the player if a red marble has been drawn.<p>
     * @param dim 'c' for column, 'r' for row.
     * @param index range 1-4 for column, 1-3 for row*/
    public void buyFromMarket(char dim, int index, LeaderCard leader){
        turnType = 'm';
        if(dim!='c' && dim!='r'){
            //throw ex
            return;
        }
        resAcquired = null;
        Market market = game.getMarket();
        if(dim == 'c'){
            if(index<1 || index>4){
                //throw ex
                return;
            }
            resAcquired = market.chooseColumn(index);
        }else if(dim == 'r'){
            if(index<1 || index>3){
                //throw ex
                return;
            }
            resAcquired = market.chooseRow(index);
        }
        //there's just one red marble in market so 1 faith points at max for each draw
        if(resAcquired.contains(Resource.FAITH)){
            addFaithPoints(1);
            resAcquired.remove(Resource.FAITH);
        }

        if(checkLeaderAvailability(leader)){
            leader.applyEffect(this);
        }

        storeInWarehouse(resAcquired);
    }

    /**Discard resources when there is no space left in the warehouse.
     * <p>Alert Game that will add Faith Points to other players.</p>
     * @param resources resources list to discard*/
    public void discardResources(List<Resource> resources){
        for(Resource resource : resources){
            observer.alertDiscardResource(this);
        }
    }

    /**Activate the leader card, only if the player has that card. From now on it's available for usage.
     * @param leader chosen leaderCard to activate */
    public void activateLeaderCard(@NotNull LeaderCard leader){
        for(LeaderCard l : leaderCards){
            if(l.equals(leader)){
                l.setStatus(true);
            }
        }
    }

    /**Discard a leaderCard and give a Faith Point to the player.
     * @param leaderCard card that the player wants to remove. */
    public void discardLeaderCard(@NotNull LeaderCard leaderCard){
        leaderCards.remove(leaderCard);
        addFaithPoints(1);
    }

    /**Let the player choose an extra resource to add during initialization phase.*/
    public Resource chooseResource(){
        //wait for the player to choose a resource
        //then add to warehouse
        return null;
    }

    public void storeInWarehouse(List<Resource> resources){

    }

    /**Place the resources in the specified warehouse tier.
     * @param resources list of resources the player wants to store
     * @param tier Warehouse inventory shelf's id on which the player want to place {@code resources}*/
    public void storeInWarehouse(Resource resources, int tier){

        switch(tier){
            case 1 : warehouse.addSmall(resources);
                     break;
            case 2 : warehouse.addMid(resources);
                        break;
            case 3 : warehouse.addLarge(resources);
            default : return;
        }
    }

    /**Add the specified quantity of Faith Points causing the player to move forward on the board.
     * <p>If the movement causes a Vatican Report or the end of the match, the Game will be notified.</p>
     * @param quantity number of Faith Points the player gains*/
    public void addFaithPoints(int quantity){moveOnBoard(quantity);}

    /**Given a {@code vaticanReport} (must be in 1-3 range), the method tells if the player is in a safe zone.
     * If it returns true, then the player should activate the pope pass relating that zone.*/
    public boolean isInRangeForReport(int vaticanReport){
        switch(vaticanReport) {
            case 1 : return currPos>4;
            case 2 : return currPos>11;
            case 3 : return currPos>18;
            default : return false;
        }
    }

    /**Given 2 resource inputs, this production power stores the chosen resource in player's lootchest.
     * <p>If the player has enough resources in the warehouse, this method will remove {@code input1} and
     * {@code input2}.</p>
     * @param input1 first resource input.
     * @param input2 second resource input.
     * @param output chosen resource for the exchange.*/
    public void startBasicProduction(@NotNull Resource input1, @NotNull Resource input2, @NotNull  Resource output){
        if(input1==null || input2==null || output==null){
            //throw ex
            return;
        }
        Resource smallShelf = warehouse.getSmallInventory();
        List<Resource> midShelf = warehouse.getMidInventory();
        List<Resource> largeShelf = warehouse.getLargeInventory();

        if(input1.equals(input2)){
            if(midShelf.contains(input1) && midShelf.size()==2){
                warehouse.removeMid();
                warehouse.removeMid();
                lootChest.addResources(output);
                return;
            }else if(largeShelf.contains(input1) && largeShelf.size()>=2){
                warehouse.removeLarge();
                warehouse.removeLarge();
                lootChest.addResources(output);
                return;
            }else{
                //cannot comply to request
                return;
            }
        }
        if(smallShelf.equals(input1) && midShelf.contains(input2) || smallShelf.equals(input2) && midShelf.contains(input1)){
            warehouse.removeSmall();
            warehouse.removeMid();
            lootChest.addResources(output);
            return;
        }
        if(smallShelf.equals(input1) && largeShelf.contains(input2) || smallShelf.equals(input2) && largeShelf.contains(input1)){
            warehouse.removeSmall();
            warehouse.removeLarge();
            lootChest.addResources(output);
            return;
        }
        if(midShelf.contains(input1) && largeShelf.contains(input2) || midShelf.contains(input2) && largeShelf.contains(input1)){
            warehouse.removeMid();
            warehouse.removeLarge();
            lootChest.addResources(output);
            return;
        }
        //error: cannot comply request
        return;
    }

    public void startProduction(ProductionCard card){}

    /**Check if {@code leader} is actually a card in player's Leader Deck and if it's active. */
    private boolean checkLeaderAvailability(LeaderCard leader){
        if(leader!=null){
            for(LeaderCard c : leaderCards){
                if(c.equals(leader) && c.isActive()){
                    return true;
                }
            }
        }
        return false;
    }

    public void setExtraStorage(StorageAbility card){
        if(extraStorage1 == null){
            extraStorage1 = card;
            return;
        }else if (extraStorage2 == null){
            extraStorage2 = card;
            return;
        }else
            //error
            return;
    }

}
