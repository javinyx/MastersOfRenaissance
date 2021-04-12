package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.StorageAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ProPlayer extends Player{
    private Warehouse warehouse;
    private LootChest lootChest;
    private Deque<ConcreteProductionCard> prodCards1, prodCards2, prodCards3;
    private List<LeaderCard> leaderCards;
    private final int turnID;
    private ArrayList<PopePass> passes;
    private List<Resource> resAcquired;
    private char turnType;
    private StorageAbility extraStorage1, extraStorage2;
    private Function<StorageAbility, Integer> pointsFromExtraStorage = (StorageAbility x) -> {int points=0;
        if(x!=null){
            points += x.size();
        }
        return points;
    };

    public ProPlayer(String nickname, int turnID, Game game){
        super(nickname, game);
        this.turnID = turnID;
        warehouse = new Warehouse();
        lootChest = new LootChest();
        prodCards1 = new ArrayDeque<>();
        prodCards2 = new ArrayDeque<>();
        prodCards3 = new ArrayDeque<>();
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

    public ArrayList<PopePass> getPopePasses(){
        return passes;
    }

    /**Buy the specified ProductionCard and place it onto the specified {@code stack}.
     * <p>This will remove the card from the game production deck</p>
     * <p>Player can decide to play a LeaderCard that may affect the cost of the chosen ProductionCard.
     * Its applicability will be checked by the method.</p>
     * @param card  choosen card for the transaction
     * @param stack indicates on which board's stack the player wants to place the card
     * @param leader LeaderCard: just DiscountAbility might have an effect here
     * @param removeFromWar list of resources the player has intention to use for paying by removing them from the warehouse
     * @param removeFromLoot list of resources the player has intention to use for paying by removing them from the lootchest*/
    public void buyProductionCard(ConcreteProductionCard card, int stack, LeaderCard leader, List<Resource> removeFromWar, List<Resource> removeFromLoot){
        List<ConcreteProductionCard> availableProdCards = game.getProductionDecks();
        Warehouse warBackup = new Warehouse(warehouse);
        LootChest lootBackup = new LootChest(lootChest);
        int pos;

        if(!availableProdCards.contains(card) || stack<1 || stack>3){
            throw new IllegalArgumentException();
        }
        Deque<ConcreteProductionCard> prodStack;

        switch(stack){
            case 1: prodStack = prodCards1;
                    break;
            case 2: prodStack = prodCards2;
                    break;
            case 3: prodStack = prodCards3;
                    break;
            default : throw new IndexOutOfBoundsException("Stack parameter must be between 1 and 3");
        }

        if(card.getLevel()!= prodStack.getFirst().getLevel()+1){
            throw new RuntimeException("Cannot buy this card because of its level");
        }

        turnType = 'b';

        resAcquired = card.getCost();

        if(checkLeaderAvailability(leader))
            leader.applyEffect(this);

        for (int i = 0; i < removeFromWar.size(); i++) {

            pos = resAcquired.indexOf(removeFromWar.get(i));

            if(pos >= 0){

                if (warehouse.getSmallInventory() != null && warehouse.getSmallInventory().equals(resAcquired.get(pos)))
                    warehouse.removeSmall();
                else if (warehouse.getMidInventory() != null && warehouse.getMidInventory().get(0).equals(resAcquired.get(pos)))
                    warehouse.removeMid();
                else if (warehouse.getLargeInventory() != null && warehouse.getLargeInventory().get(0).equals(resAcquired.get(pos)))
                    warehouse.removeLarge();
                else{
                    warehouse = warBackup;
                    throw new RuntimeException("Required resource isn't in the warehouse");
                }
            }
        }

        for (int i = 0; i < removeFromLoot.size(); i++) {

            pos = resAcquired.indexOf(removeFromLoot.get(i));

            if (pos >= 0) {
                if (lootChest.getInventory() != null) {
                    for (int j = 0; j < lootChest.getCountResInLootchest(); j++) {

                        if (lootChest.getInventory().get(j).equals(resAcquired.get(pos))) {
                            resAcquired.get(pos);
                            lootChest.removeResources(removeFromLoot.get(i));
                        } else {
                            warehouse = warBackup;
                            lootChest = lootBackup;
                            throw new RuntimeException("Required resource isn't in the lootchest");
                        }
                    }
                }
            }
        }

        game.removeFromProdDeck(card);
        prodStack.addFirst(card);
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
        List<ConcreteProductionCard> prodCards = new ArrayList<>();
        prodCards.addAll(prodCards1);
        prodCards.addAll(prodCards2);
        prodCards.addAll(prodCards3);
        for(ConcreteProductionCard pc : prodCards){
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

        victoryPoints += countAllResources()/5;

        return victoryPoints;
    }

    public int countAllResources(){
        return (lootChest.getCountResInLootchest() + warehouse.getMidInventory().size()
                + warehouse.getLargeInventory().size() + (warehouse.getSmallInventory()!=null ? 1 : 0)
                + pointsFromExtraStorage.apply(extraStorage1) + pointsFromExtraStorage.apply(extraStorage2));
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
            throw new IllegalArgumentException("Chosen dimension must be either 'c' (column) or 'r' (row), instead it's "+dim);
        }
        resAcquired = null;
        Market market = game.getMarket();
        if(dim == 'c'){
            if(index<1 || index>4){
                throw new IndexOutOfBoundsException("Index must be between 1 and 4, but it's " + index);
            }
            resAcquired = market.chooseColumn(index);
        }else if(dim == 'r'){
            if(index<1 || index>3){
                throw new IndexOutOfBoundsException("Index must be between 1 and 3, but it's " + index);
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

        //call controller and let the player choose on which warehouse tier it should place each resource
    }

    /**Discard resources when there is no space left in the warehouse.
     * <p>Alert Game that will add Faith Points to other players.</p>
     * @param resources resources list to discard*/
    public void discardResources(List<Resource> resources){
        for(Resource resource : resources){
            observer.alertDiscardResource(this);
        }
    }

    public void chooseLeaders(List<LeaderCard> leaders){
        if(leaders.size()!=2){
            throw new IllegalArgumentException("Must be 2 leaders");
        }
        leaderCards.addAll(leaders);
    }

    public void giveLeaders(List<LeaderCard> leaders){
        //call controller
    }
    /**Activate the leader card, only if the player has that card. From now on it's available for usage.
     * @param leader chosen leaderCard to activate */
    public void activateLeaderCard(LeaderCard leader){
        for(LeaderCard l : leaderCards){
            if(l.equals(leader)){
                l.setStatus(true);
            }
        }
    }

    /**Discard a leaderCard and give a Faith Point to the player.
     * @param leaderCard card that the player wants to remove. */
    public void discardLeaderCard(LeaderCard leaderCard){
        if(leaderCard!=null) {
            if (leaderCard.isActive()) {
                throw new RuntimeException("Cannot discard an active leader");
            }
            if (leaderCards.contains(leaderCard)) {
                leaderCards.remove(leaderCard);
                addFaithPoints(1);
            }
        }
    }

    /**Let the player choose an extra resource to add during initialization phase.*/
    public void chooseResource(){
        //wait for the player to choose a resource
        //then add to warehouse
    }

    /**Place the resource in the specified warehouse tier.
     * @param resource resource the player wants to store
     * @param tier Warehouse inventory shelf's id on which the player want to place {@code resource}*/
    public void storeInWarehouse(Resource resource, int tier){
        switch(tier){
            case 1 : warehouse.addSmall(resource);
                     break;
            case 2 : warehouse.addMid(resource);
                        break;
            case 3 : warehouse.addLarge(resource);
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
     * <p>SOON TO BE FIXED: for now it checks just the Warehouse resources.</p>
     * @param resAsCash resources that the player wants to use as currency for the basic power
     * @param output chosen resource for the exchange.*/
    private void startBasicProduction(ResourcesWallet resAsCash, Resource output) { //DA RIFARE
        if(output==null){
            throw new NullPointerException("Output cannot be null");
        }
        if(output.equals(Resource.FAITH) || output.equals(Resource.BLANK)){
            throw new IllegalArgumentException("Output cannot be FAITH or BLANK");
        }
        List<Resource> fromLoot = resAsCash.getLootchestTray();
        List<Resource> fromWar = resAsCash.getWarehouseTray();
        List<Resource> fromExtra1 = resAsCash.getExtraStorage1();
        List<Resource> fromExtra2 = resAsCash.getExtraStorage2();

        Resource input1;
        Resource input2;
        if(fromLoot.size()==2) {
            input1 = fromLoot.get(0);
            input2 = fromLoot.get(1);
            basicProductionFromLootchest(input1, input2);
        }else if(fromWar.size()==2) {
            input1 = fromWar.get(0);
            input2 = fromWar.get(1);
            basicProductionFromWarehouse(input1, input2);
        }else if(fromExtra1.size()==2) {
            input1 = fromExtra1.get(0);
            input2 = fromExtra2.get(1);
            basicProductionFromExtra(input1, input2, 1);
        } else if(fromExtra2.size()==2){
            input1 = fromExtra2.get(0);
            input2 = fromExtra2.get(0);
            basicProductionFromExtra(input1, input2, 2);
        }else if(resAsCash.anyFromLootchestTray() && resAsCash.anyFromWarehouseTray()){
            input1 = fromLoot.get(0);
            input2 = fromWar.get(0);
            partialBasicProdFromLoot(input1);
            partialBasicProdFromWarehouse(input2);
        }else if(resAsCash.anyFromLootchestTray() && resAsCash.anyFromStorageCard1()){
            input1 = fromLoot.get(0);
            input2 = fromExtra1.get(0);
            partialBasicProdFromLoot(input1);
            partialBasicProdFromExtra(input2, 1);
        }else if(resAsCash.anyFromLootchestTray() && resAsCash.anyFromStorageCard2()){
            input1 = fromLoot.get(0);
            input2 = fromExtra2.get(0);
            partialBasicProdFromLoot(input1);
            partialBasicProdFromExtra(input2, 2);
        }else if(resAsCash.anyFromWarehouseTray() && resAsCash.anyFromStorageCard1()){
            input1 = fromWar.get(0);
            input2 = fromExtra1.get(0);
            partialBasicProdFromWarehouse(input1);
            partialBasicProdFromExtra(input2, 1);
        }else if(resAsCash.anyFromWarehouseTray() && resAsCash.anyFromStorageCard2()){
            input1 = fromWar.get(0);
            input2 = fromExtra2.get(0);
            partialBasicProdFromWarehouse(input1);
            partialBasicProdFromExtra(input2, 2);
        }else if(resAsCash.anyFromStorageCard1() && resAsCash.anyFromStorageCard2()){
            input1 = fromExtra1.get(0);
            input2 = fromExtra2.get(0);
            partialBasicProdFromExtra(input1, 1);
            partialBasicProdFromExtra(input2, 2);
        }else{
            throw new NullPointerException("Some parameters are null when they shouldn't");
        }
    }

    private void basicProductionFromLootchest(Resource input1, Resource input2){
        if(input1.equals(Resource.FAITH) || input1.equals(Resource.BLANK) || input2.equals(Resource.FAITH)
                || input2.equals(Resource.BLANK)){
            throw new IllegalArgumentException();
        }
        lootChest.removeResources(input1);
        lootChest.removeResources(input2);
    }
    private void basicProductionFromWarehouse(Resource input1, Resource input2){
        if(input1.equals(Resource.FAITH) || input1.equals(Resource.BLANK) || input2.equals(Resource.FAITH)
                || input2.equals(Resource.BLANK)){
            throw new IllegalArgumentException();
        }
        Resource smallShelf = warehouse.getSmallInventory();
        List<Resource> midShelf = warehouse.getMidInventory();
        List<Resource> largeShelf = warehouse.getLargeInventory();

        if(input1.equals(input2)){
            if(midShelf.contains(input1) && midShelf.size()==2){
                warehouse.removeMid();
                warehouse.removeMid();
                return;
            }else if(largeShelf.contains(input1) && largeShelf.size()>=2){
                warehouse.removeLarge();
                warehouse.removeLarge();
                return;
            }else{
                throw new RuntimeException("Not enough resources in warehouse");
            }
        }
        if(smallShelf.equals(input1) && midShelf.contains(input2) || smallShelf.equals(input2) && midShelf.contains(input1)){
            warehouse.removeSmall();
            warehouse.removeMid();
            return;
        }
        if(smallShelf.equals(input1) && largeShelf.contains(input2) || smallShelf.equals(input2) && largeShelf.contains(input1)){
            warehouse.removeSmall();
            warehouse.removeLarge();
            return;
        }
        if(midShelf.contains(input1) && largeShelf.contains(input2) || midShelf.contains(input2) && largeShelf.contains(input1)){
            warehouse.removeMid();
            warehouse.removeLarge();
            return;
        }
        throw new RuntimeException("Not enough resources in warehouse");
    }
    private void basicProductionFromExtra(Resource input1, Resource input2, int extraStorage){
        if(input1.equals(Resource.FAITH) || input1.equals(Resource.BLANK) || input2.equals(Resource.FAITH)
                || input2.equals(Resource.BLANK)){
            throw new IllegalArgumentException();
        }
        StorageAbility storageBackup;
        if(extraStorage==1){
            storageBackup = new StorageAbility(extraStorage1);
            if(!(extraStorage1.remove(input1) && extraStorage1.remove(input2))){
                extraStorage1 = storageBackup;
                throw new RuntimeException();
            }
        }else if(extraStorage==2){
            storageBackup = new StorageAbility(extraStorage2);
            if(!(extraStorage2.remove(input1) && extraStorage1.remove(input2))){
                extraStorage2 = storageBackup;
                throw new RuntimeException();
            }
        }else{
            throw new IllegalArgumentException();
        }
    }
    private void partialBasicProdFromLoot(Resource input){
        if(input.equals(Resource.FAITH) || input.equals(Resource.BLANK)){
            throw new IllegalArgumentException();
        }
        lootChest.removeResources(input);
    }
    private void partialBasicProdFromWarehouse(Resource input){
        if(input.equals(Resource.FAITH) || input.equals(Resource.BLANK)){
            throw new IllegalArgumentException();
        }
        Resource smallShelf = warehouse.getSmallInventory();
        List<Resource> midShelf = warehouse.getMidInventory();
        List<Resource> largeShelf = warehouse.getLargeInventory();

        if(smallShelf.equals(input)){
            warehouse.removeSmall();
            return;
        }
        if(midShelf.contains(input)){
            warehouse.removeMid();
            return;
        }
        if(largeShelf.contains(input)){
            warehouse.removeLarge();
            return;
        }
        throw new RuntimeException();
    }
    private void partialBasicProdFromExtra(Resource input, int extraStorage){
        if(input.equals(Resource.FAITH) || input.equals(Resource.BLANK)){
            throw new IllegalArgumentException();
        }
        StorageAbility storageBackup;
        switch(extraStorage){
            case 1 : storageBackup = new StorageAbility(extraStorage1);
                    if(!extraStorage1.remove(input)){
                        extraStorage1 = storageBackup;
                        throw new RuntimeException();
                    }
                    return;
            case 2 : storageBackup = new StorageAbility(extraStorage2);
                    if(!extraStorage2.remove(input)){
                        extraStorage2 = storageBackup;
                        throw new RuntimeException();
                    }
                    return;
            default : throw new IllegalArgumentException();
        }
    }

    /**For each ProductionCard in {@code cards}, it tries to consume all the resources planned by the player and requested
     * by the specific ProductionCard's cost.
     * <p>Players must specify from which type of storage they want to obtain the resources needed to pay by distributing
     * them in 4 different list: {@code removeFromWar}, {@code removeFromLoot}, {@code removeFromStorage1} and
     * {@code removeFromStorage2}.</p>.
     * <p>Players express their wish of activation of the basic production power by {@code basicProd=true}</p>
     * @param cards list of ProductionCard the player wants to activate and produce
     * @param resAsCash object that contains all the resources the player wants to use as currency for the production,
     * well divided into their storage type
     * @param prodLeaders leaders whose effect the player would like to benefit. Mind that can only be BoostAbility or
     * Storage Ability
     * @param basicProd true if the player wishes to activate the basic production power
     * @param output the Resource the player wants as an outcome from the basic production power*/
    public void startProduction(List<ConcreteProductionCard> cards, ResourcesWallet resAsCash,
                                List<LeaderCard> prodLeaders, boolean basicProd, Resource output){
        //BACKUPS
        Warehouse warBackup = new Warehouse(warehouse);
        LootChest lootBackup = new LootChest(lootChest);

        List<Resource> production = new ArrayList<>();
        turnType = 'p';
        for(ConcreteProductionCard p : cards){
            if(prodCards1.peekFirst().equals(p) || prodCards2.peekFirst().equals(p) || prodCards3.peekFirst().equals(p)){
                //controller will ask the player to adjust the ProdCards selection and try again to call this method
                warehouse = warBackup;
                lootChest = lootBackup;
                throw new RuntimeException(p.toString() + " cannot produce");
            }
            try {
                production.addAll(produce(p, resAsCash));
            }catch(Exception e){
                warehouse = warBackup;
                lootChest = lootBackup;
                throw new RuntimeException("Not enough resources");
            }
        }

        //NON VA BENE: correggere le BoostAbility
        for(LeaderCard lc : prodLeaders){
            //resAcquired ....
            if(checkLeaderAvailability(lc)){
                lc.applyEffect(this);
            }
        }
        List<Resource> removeFromWar = resAsCash.getWarehouseTray();
        List<Resource> removeFromLoot = resAsCash.getLootchestTray();
        /*if(basicProd){
            Resource input1=null, input2=null;
            for(int i=0; i<2; i++){
                if(!removeFromWar.isEmpty()){
                    switch(warehouse.getInventoryID(removeFromWar.get(0))){
                        case 1: warehouse.removeSmall();
                            removeFromWar.remove(0);
                            break;
                        case 2 : warehouse.removeMid();
                            removeFromWar.remove(0);
                            break;
                        case 3 : warehouse.removeLarge();
                            removeFromWar.remove(0);
                            break;
                    }
                }if(!removeFromLoot.isEmpty()){
                    Resource o = removeFromLoot.get(0);
                    if(lootChest.getInventory().get(o)!=null){
                        lootChest.removeResources(o);
                        removeFromLoot.remove(0);
                    }
                }
            }
            startBasicProduction(input1, input2, output);
        }*/

        lootChest.addResources(production);
    }
    /**Activate production for a single ProductionCard.
     * <p>All list of Resource are optional, so can be null if the player doesn't want to draw resources from
     * the respective storage type. Though, mind that if all of them are null at the same time, exceptions will be thrown
     * because every ProductionCard requires resources to activate its power.</p>
     * <p>This method doesn't check if {@code ProductionCard} it's playable or if the player has the right to play it. However,
     * it checks if {@code removeFromStorage1} and/or {@code removeFromStorage2} are the right type of extraStorage
     * accordingly to the type of LeaderCard the player has active.</p>
     * <p>It resets both player's {@code extraStorage} status if an exception is thrown, but not Warehouse and LootChest status.</p>
     * @param card Production Card
     * @param resAsCash wallet of resources redistributed as the player wishes across the possible storage option*/
    private List<Resource> produce(ConcreteProductionCard card, ResourcesWallet resAsCash) throws Exception{
        //BACKUPS
        List<Resource> cost = card.getCost();
        List<Resource> costDupe = card.getCost(); //since cannot modify cost while forEach is running and taking elements from it
        StorageAbility extraStorage1Backup = new StorageAbility(extraStorage1);
        StorageAbility extraStorage2Backup = new StorageAbility(extraStorage2);

        List<Resource> removeFromWar = resAsCash.getWarehouseTray();
        List<Resource> removeFromLoot = resAsCash.getLootchestTray();
        List<Resource> removeFromStorage1 = resAsCash.getExtraStorage1();
        List<Resource> removeFromStorage2 = resAsCash.getExtraStorage2();

        for(Resource r : costDupe){
            if(removeFromWar.contains(r)){
               if(warehouse.getSmallInventory().equals(r)){
                   removeFromWar.remove(r);
                   warehouse.removeSmall();
                   cost.remove(r);
               }else if(warehouse.getMidInventory().contains(r)){
                   removeFromWar.remove(r);
                   warehouse.removeMid();
                   cost.remove(r);
               }else if(warehouse.getLargeInventory().contains(r)){
                   removeFromWar.remove(r);
                   warehouse.removeLarge();
                   cost.remove(r);
               }
            }else if(removeFromLoot.contains(r)){
                if(lootChest.getInventory().containsKey(r)) {
                    removeFromLoot.remove(r);
                    lootChest.removeResources(r);
                    cost.remove(r);
                }
           }else if(removeFromStorage1!=null && extraStorage1!=null && extraStorage1.getStorageType().equals(r)){
             if(extraStorage1.size()>0){
                 removeFromStorage1.remove(r);
                 extraStorage1.remove(r);
                 cost.remove(r);
             }else {
                 extraStorage1 = extraStorage1Backup;
                 extraStorage2 = extraStorage2Backup;
                 throw new Exception();
             }
           }else if(removeFromStorage2!=null && extraStorage2!=null && extraStorage2.getStorageType().equals(r)){
               if(extraStorage2.size()>0){
                   removeFromStorage2.remove(r);
                   extraStorage2.remove(r);
                   cost.remove(r);
               }else {
                   extraStorage1 = extraStorage1Backup;
                   extraStorage2 = extraStorage2Backup;
                   throw new Exception();
               }
           }else {
                extraStorage1 = extraStorage1Backup;
                extraStorage2 = extraStorage2Backup;
                throw new Exception();
            }
        }
        List<Resource> production = card.getProduction();
        for(Resource r : production){
            if(r.equals(Resource.FAITH)){
                addFaithPoints(1);
            }
        }
        return production.stream()
                .filter(x -> !x.equals(Resource.FAITH))
                .collect(Collectors.toList());
    }

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
        throw new NullPointerException("No StorageAbility card given");
    }

}
