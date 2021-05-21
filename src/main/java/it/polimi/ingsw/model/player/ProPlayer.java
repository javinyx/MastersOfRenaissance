package it.polimi.ingsw.model.player;


import com.google.gson.Gson;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.cards.leader.StorageAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.market.Resource;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ProPlayer extends Player{
    public boolean isInitializationPhase() {
        return initializationPhase;
    }

    public void setInitializationPhase(boolean initializationPhase) {
        this.initializationPhase = initializationPhase;
    }
    private boolean initializationPhase = true;

    protected Warehouse warehouse;
    protected LootChest lootChest;
    protected Deque<ConcreteProductionCard> prodCards1, prodCards2, prodCards3;
    protected List<LeaderCard> leaderCards;
    protected final int turnID;
    protected ArrayList<PopePass> passes;
    protected List<Resource> resAcquired;
    protected ResourcesWallet resAsCash;
    protected char turnType;
    protected Optional<List<StorageAbility>> extraStorage;
    protected static int maxNumExtraStorage = 2;
    protected Controller controller;
    protected Function<Optional<List<StorageAbility>>, Integer> pointsFromExtraStorage = (Optional<List<StorageAbility>> x) ->
    {int points=0;
        if(x.isPresent()){
            for(StorageAbility s : x.get())
                points += s.size();
        }
        return points;
    };

    public ProPlayer(String nickname, int turnID, Game game){
        super(nickname, game);
        controller = game.getControllerObserver();
        this.turnID = turnID;
        warehouse = new Warehouse();
        lootChest = new LootChest();
        prodCards1 = new ArrayDeque<>();
        prodCards2 = new ArrayDeque<>();
        prodCards3 = new ArrayDeque<>();
        leaderCards = new ArrayList<>();
        resAcquired = new ArrayList<>();
        resAsCash = null;
        extraStorage = Optional.empty();
        passes = new ArrayList<>(3);
        passes.add(0, new PopePass(1));
        passes.add(1, new PopePass(2));
        passes.add(2, new PopePass(3));
    }
    //------------------------------------GETTERS----------------------------------
    public static int getMaxNumExtraStorage(){
        return maxNumExtraStorage;
    }

    //m == buymarket, b == buyproduction, p == activateProduction
    public char getTurnType() {
        return turnType;
    }
    public int getTurnID(){
        return turnID;
    }

    public List<PopePass> getPopePasses(){
        return passes;
    }

    public Deque<ConcreteProductionCard> getProdCards1(){return prodCards1;}
    public Deque<ConcreteProductionCard> getProdCards2(){return prodCards2;}
    public Deque<ConcreteProductionCard> getProdCards3(){return prodCards3;}
    public List<Deque<ConcreteProductionCard>> getProdStacks(){
        List<Deque<ConcreteProductionCard>> stacks = new ArrayList<>();
        stacks.add(prodCards1);
        stacks.add(prodCards2);
        stacks.add(prodCards3);
        return stacks;
    }

    /**Get all the resources just bought from the market by the player. */
    public List<Resource> getResAcquired(){
        return resAcquired;
    }
    public ResourcesWallet getResAsCash(){
        return resAsCash;
    }
    public Warehouse getWarehouse(){ return warehouse; }
    public LootChest getLootChest(){
        return lootChest;
    }
    public List<StorageAbility> getExtraStorage(){
        return extraStorage.orElse(new ArrayList<>());
    }


//-------------------------------VICTORY POINTS---------------------------------
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

    /**Count all the resources the players have in their warehouse, lootchest and extra storage given by leader cards. */
    public int countAllResources(){
        return (lootChest.getCountResInLootchest() + warehouse.getMidInventory().size()
                + warehouse.getLargeInventory().size() + (warehouse.getSmallInventory()!=null ? 1 : 0)
                + pointsFromExtraStorage.apply(extraStorage));
    }

    private int victoryPointsFromPos(){
        switch(currPos){
            case 24: return 20;
            case 23: case 22: case 21: return 16;
            case 20: case 19: case 18: return 12;
            case 17: case 16: case 15: return 9;
            case 14: case 13: case 12: return 6;
            case 11: case 10: case 9: return 4;
            case 8: case 7: case 6: return 2;
            case 5: case 4: case 3: return 1;
            default: return 0;
        }
    }

//----------------------------------BUY PRODUCTION CARDS-------------------------------
    /**Buy the specified ProductionCard and place it onto the specified {@code stack}.
     * <p>This will remove the card from the game production deck</p>
     * <p>Player can decide to play a LeaderCard that may affect the cost of the chosen ProductionCard.
     * Its applicability will be checked by the method.</p>
     * @param card  chosen card for the transaction
     * @param stack indicates on which board's stack the player wants to place the card
     * @param leaders LeaderCards: just DiscountAbility might have an effect here
     * @param resourcesWallet resources distributed among different types of storage as the player wish to spend them*/
    public void buyProductionCard(ConcreteProductionCard card, int stack, List<LeaderCard> leaders, ResourcesWallet resourcesWallet)
            throws BadStorageException{

        List<ConcreteProductionCard> availableProdCards = game.getBuyableProductionCards();
        Warehouse warBackup = new Warehouse(warehouse);
        LootChest lootBackup = new LootChest(lootChest);
        List<StorageAbility> extraStorageBackup = null;

        if(resourcesWallet==null || resourcesWallet.isEmpty()){
            throw new BadStorageException();
        }
        if(extraStorage.isEmpty() && resourcesWallet.anyFromExtraStorage()){
            throw new BadStorageException();
        }
        if(extraStorage.isPresent() && resourcesWallet.anyFromExtraStorage()){
            extraStorageBackup = new ArrayList<>();
            for(int i=0; i<extraStorage.get().size(); i++){
                extraStorageBackup.add(i, new StorageAbility(extraStorage.get().get(i)));
            }
        }

        if(!availableProdCards.contains(card)){
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

        if((prodStack.isEmpty() && card.getLevel()!=1) || (!prodStack.isEmpty() && card.getLevel()!= prodStack.peekFirst().getLevel()+1)){
            throw new RuntimeException("Cannot buy this card because of its level");
        }

        turnType = 'b';
        resAcquired.clear();
        resAcquired.addAll(card.getCost()); //we use resAcquired to see its cost, it can be modified by a DiscountAbilityCard
        List<Resource> removeFromLoot = resourcesWallet.getLootchestTray();
        List<Resource> removeFromWar = resourcesWallet.getWarehouseTray();
        List<List<Resource>> fromExtra = new ArrayList<>();
        for(int i=0; i<resourcesWallet.extraStorageSize(); i++){
            fromExtra.add(i, resourcesWallet.getExtraStorage(i));
        }

        if (leaders != null)
            for(LeaderCard leader : leaders) {
                if (checkLeaderAvailability(leader))
                    leader.applyEffect(this);
            }

        for(Resource r : removeFromWar){
            if(resAcquired.contains(r)) {
                if (warehouse.getSmallInventory() != null && warehouse.getSmallInventory().equals(r)) {
                    warehouse.removeSmall();
                    resAcquired.remove(r);
                } else if (warehouse.getMidInventory().contains(r)) {
                    warehouse.removeMid();
                    resAcquired.remove(r);
                } else if (warehouse.getLargeInventory().contains(r)) {
                    warehouse.removeLarge();
                    resAcquired.remove(r);
                }
            }
        }

        for(Resource r : removeFromLoot){
            if(lootChest.removeResources(r)){
                if(resAcquired.contains(r))
                    resAcquired.remove(r);
                else
                    lootChest.addResources(r);
            }
        }

        if(extraStorage.isPresent()){
            for(int i=0; i<extraStorage.get().size(); i++){
                for(Resource r : fromExtra.get(i)){
                    if(extraStorage.get().get(i).remove(r)){
                        resAcquired.remove(r);
                    }
                }
            }
        }

        if(!resAcquired.isEmpty()){
            //player hasn't given all the necessary resources in order to buy that production card
            repairBackup(warBackup, lootBackup, extraStorageBackup);
            throw new BadStorageException();
        }

        //if we are here, then the cost has been payed completely
        game.removeFromProdDeck(card);
        prodStack.addFirst(card);

        controller.update(MessageID.CONFIRM_END_TURN);
    }
//----------------------------------------MARKET-------------------------------
    /**Obtains the resources chosen from market by column or row.
     * <p>Add faith points to the player if a red marble has been drawn.</p>
     * @param dim 'c' for column, 'r' for row.
     * @param index range 1-4 for column, 1-3 for row*/
    public void buyFromMarket(char dim, int index, List<BiElement<MarbleAbility, Integer>> leaders){
        turnType = 'm';
        if(dim!='c' && dim!='r'){
            throw new IllegalArgumentException("Chosen dimension must be either 'c' (column) or 'r' (row), instead it's "+dim);
        }
        resAcquired.clear();
        Market market = game.getMarket();
        index--;
        if(dim == 'c'){
            if(index<0 || index>3){
                throw new IndexOutOfBoundsException("Index must be between 1 and 4, but it's " + index);
            }
            resAcquired = market.chooseColumn(index);
        }else if(dim == 'r'){
            if(index<0 || index>2){
                throw new IndexOutOfBoundsException("Index must be between 1 and 3, but it's " + index);
            }
            resAcquired = market.chooseRow(index);
        }
        //there's just one red marble in market so 1 faith points at max for each draw
        if(resAcquired.contains(Resource.FAITH)){
            addFaithPoints(1);
            resAcquired.remove(Resource.FAITH);
        }

        //TURN BLANK INTO SOMETHING: call controller to let the player choose which leaders he wants
        if(resAcquired.contains(Resource.BLANK) && leaders!=null && !leaders.isEmpty()){
            for(BiElement<MarbleAbility, Integer> bi : leaders){
                if(checkLeaderAvailability(bi.getFirstValue())) {
                    for (int i = 0; i < bi.getSecondValue() - 1; i++) {
                        if(!bi.getFirstValue().applyEffect(this)){
                            throw new RuntimeException("Invalid Leader");
                        }
                    }
                }else{
                    throw new RuntimeException("Invalid Leader");
                }
            }
        }
        //call to controller notifying that the player has to organize the resources just bought
        //controller.update(MessageID.STORE_RESOURCES);
    }

//----------------------------------RESOURCES------------------------------------------
    /**Discard resources when there is no space left in the warehouse. Blank and Faith types of resources won't
     * be taken in consideration since it's not possible to discard Faith.
     * <p>Alert Game that will add Faith Points to other players.</p>
     * @param resources resources list to discard*/
    public void discardResources(List<Resource> resources){
        for(Resource resource : resources){
            if(resource.isValidForTrading())
                observer.alertDiscardResource(this);
        }
    }

    public void discardResources(int qty){
        for(int i=0; i<qty; i++){
            observer.alertDiscardResource(this);
        }
    }


    /**Place the resource in the specified warehouse tier.
     * @param resource resource the player wants to store
     * @param tier Warehouse inventory shelf's id on which the player want to place {@code resource}
     * @return true if the storing ended successfully*/
    public boolean storeInWarehouse(Resource resource, int tier){
        if(resource==null || !resource.isValidForTrading()){
            return false;
        }
        switch(tier){
            case 1 : return warehouse.addSmall(resource);
            case 2 : return warehouse.addMid(resource);
            case 3 : return warehouse.addLarge(resource);
            default : return false;
        }
    }

    /**Place the rescources in the leader card slots.
     * <p>Mind that the method will check if the player has the specified {@code extraStorage} leader active and
     * if its storage type is compatible with {@code resource} type.</p>
     * @return true if it has correctly placed the resource, otherwise false.*/
    public boolean storeInExtraStorage(Resource resource, StorageAbility extraStorage){
        if(extraStorage==null || this.extraStorage.isEmpty() || !this.extraStorage.get().contains(extraStorage)
                || !extraStorage.getStorageType().equals(resource)){
            return false;
        }
        for(StorageAbility sa : this.extraStorage.get()){
            if(sa.equals(extraStorage) && sa.isActive() && sa.size()<2){
                sa.add(resource);
                return true;
            }
        }
        return false;
    }

//------------------------------------------LEADERS---------------------------------------
    /**Let the player communicating his/her choice of leader: 2 out of the 4 given by the game.
     * @param leaders chosen leaders*/
    public void chooseLeaders(List<LeaderCard> leaders){
        if(leaders==null || leaders.size()!=2){
            throw new IllegalArgumentException("Must be 2 leaders");
        }
        leaderCards.addAll(leaders);
        for(LeaderCard l : leaderCards){
            l.setStatus(false);
        }
    }
    /**Call the controller passing to the player the 4 options he/she has for the leader.*/
    public void giveLeadersOptions(List<LeaderCard> leaders){
        //call controller
    }
    /**Activate the leader card, only if the player has that card. From now on it's available for usage.
     * @param leader chosen leaderCard to activate */
    public boolean activateLeaderCard(LeaderCard leader){
        for(LeaderCard l : leaderCards){
            System.out.println(l + "\n");
            if(l.equals(leader) && hasEnoughResources(leader.getCost())){
                l.setStatus(true);
                return true;
            }
        }
        return false;
    }

    private boolean hasEnoughResources(List<Buyable> cost){
        BiFunction<Buyable, List<Buyable>, Boolean> isUnique = (x, list) -> {
            if(list.isEmpty()){
                return true;
            }
            for(Buyable b : list){
                if(b.isEquivalent(x)){
                    return false;
                }
            }
            return true;
        };
        List<BuyableMap> requirements = new ArrayList<>();
        List<Buyable> distinctBuyable = new ArrayList<>();
        for(Buyable b : cost){
            if (isUnique.apply(b, distinctBuyable)){
                distinctBuyable.add(b);
            }
        }
        //System.out.println("DISTINCT" + distinctBuyable);
        for(int j=0; j<distinctBuyable.size(); j++){
            requirements.add(new BuyableMap(distinctBuyable.get(j), 0));
            for(int i=0; i<cost.size(); i++){
                if(cost.get(i).isEquivalent(distinctBuyable.get(j))){
                    requirements.get(j).addOccurrence(1);
                }
            }
        }
        //System.out.println("REQUIREMENTS" + requirements);
        int x;
        //searching between storages
        for(int j=0; j<requirements.size(); j++){
            Buyable res = requirements.get(j).getBuyableResource();
            if(lootChest.getInventory().containsKey(res)){
                requirements.get(j).subOccurrence(lootChest.getInventory().get(res));
            }else if((x=warehouse.numberOf(requirements.get(j).getBuyableResource()))>0){
                requirements.get(j).subOccurrence(x);
            }else if(extraStorage.isPresent() && (extraStorage.get().get(0).getStorageType().equals(res)
                            || extraStorage.get().get(1).getStorageType().equals(res))){
                if(extraStorage.get().get(0).getStorageType().equals(res)){
                    requirements.get(j).subOccurrence(extraStorage.get().get(0).size());
                }else if(extraStorage.get().get(1).getStorageType().equals(res)){
                    requirements.get(j).subOccurrence(extraStorage.get().get(1).size());
                }
            }else{
                //System.out.println("Res type" + res);
                for(ConcreteProductionCard pp : prodCards1){
                    if(pp.isEquivalent(res)){
                        requirements.get(j).subOccurrence(1);
                    }
                }
                for(ConcreteProductionCard pp : prodCards2){
                    if(pp.isEquivalent(res)){
                        requirements.get(j).subOccurrence(1);
                    }
                }
                for(ConcreteProductionCard pp : prodCards3){
                    if(pp.isEquivalent(res)){
                        requirements.get(j).subOccurrence(1);
                    }
                }
            }
        }
        //check
        for(BuyableMap b : requirements){
            //System.out.println(b + "\n");
            if(b.getOccurrence()>0)
                return false;
        }
        return true;
    }

    private class BuyableMap{
        int occurrence;
        Buyable buyableResource;

        public BuyableMap(Buyable buyableResource, int occurrence){
            this.buyableResource = buyableResource;
            this.occurrence = occurrence;
        }


        public void addOccurrence(int adding){
            this.occurrence += adding;
        }

        public void subOccurrence(int sub){
            this.occurrence -= sub;
        }

        public int getOccurrence(){return occurrence;}

        public Buyable getBuyableResource() {
            return buyableResource;
        }

        @Override
        public String toString(){
            return "BUYABLE MAP(Type: " + buyableResource + "; Occurrence: " + occurrence + ")\n";
        }
    }

    public List<LeaderCard> getLeaderCards(){
        return leaderCards;
    }

    /**Discard a leaderCard and give a Faith Point to the player.
     * @param leaderCard card that the player wants to remove.
     * @return true if it has removed the leader, false otherwise.*/
    public boolean discardLeaderCard(LeaderCard leaderCard){
        if(leaderCard!=null) {
            if (leaderCard.isActive()) {
                return false;
            }
            if (leaderCards.remove(leaderCard)) {
                addFaithPoints(1);
                return true;
            }
        }
        return false;
    }

//--------------------------------------BOARD---------------------------------------
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

//------------------------------PRODUCTION---------------------------
    private void basicProductionFromLootchest(Resource input1, Resource input2) throws BadStorageException{
        if(!input1.isValidForTrading() || !input2.isValidForTrading()){
            throw new BadStorageException();
        }
        if(!(lootChest.removeResources(input1) && lootChest.removeResources(input2))){
            throw new BadStorageException();
        }

    }
    private void basicProductionFromWarehouse(Resource input1, Resource input2) throws BadStorageException{
        if(!input1.isValidForTrading() || !input2.isValidForTrading()){
            throw new BadStorageException();
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
                throw new BadStorageException();
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
        throw new BadStorageException();
    }
    private void basicProductionFromExtra(Resource input1, Resource input2, int index) throws BadStorageException{
        if(!input1.isValidForTrading() || !input2.isValidForTrading()){
            throw new BadStorageException();
        }
        if(index==1){
            if(!(extraStorage.get().get(0).remove(input1) && extraStorage.get().get(0).remove(input2))){
                throw new BadStorageException();
            }
        }else if(index==2){
            if(!(extraStorage.get().get(1).remove(input1) && extraStorage.get().get(1).remove(input2))){
                throw new BadStorageException();
            }
        }else{
            throw new BadStorageException();
        }
    }
    private void partialBasicProdFromLoot(Resource input) throws BadStorageException{
        if(!input.isValidForTrading()){
            throw new BadStorageException();
        }
        if(!lootChest.removeResources(input)){
            throw new BadStorageException();
        }
    }
    private void partialBasicProdFromWarehouse(Resource input) throws BadStorageException{
        if(!input.isValidForTrading()){
            throw new BadStorageException();
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
        throw new BadStorageException();
    }
    private void partialBasicProdFromExtra(Resource input, int index) throws BadStorageException{
        if(!input.isValidForTrading()){
            throw new BadStorageException();
        }
        switch(index){
            case 1 : if(!extraStorage.get().get(0).remove(input)){
                        throw new BadStorageException();
                    }
                    return;
            case 2 : if(!extraStorage.get().get(1).remove(input)){
                        throw new BadStorageException();
                    }
                    return;
            default : throw new BadStorageException();
        }
    }


    /**For each ProductionCard in {@code cards}, it tries to consume all the resources planned by the player and requested
     * by the specific ProductionCard's cost or BoostAbility's cost.
     * <p>Players must specify from which type of storage they want to obtain the resources needed to pay by distributing
     * them in the {@code resAsCash} object.</p>
     * <p>If the player wants to activate the BasicProduction power, he/she has to choose the {@code outputBasic} as well.</p>
     * <p>If something goes wrong while producing (i.e. not enough resources, misplacement), the method will revert any changes done
     * to Warehouse, LootChest and StorageAbility cards, without giving to the player the resources that it could
     * have produced before the error's encounter.</p>
     * <p>Players express their wish of activation of the basic production power by {@code basicProd=true}</p>
     * @param cards list of ProductionCard the player wants to activate and produce
     * @param resAsCash object that contains all the resources the player wants to use as currency for the production,
     * well divided into their storage type
     * @param prodLeaders leaders whose effect the player would like to benefit. Mind that can only be BoostAbility and at max
     *                    2 of them.
     * @param basicProd true if the player wishes to activate the basic production power
     * @param outputBasic the Resource the player wants as an outcome from the basic production power
     * @param outputBoost list of desired outputs from the LeaderCard production with the Boost power. Mind
     * that the player can possibly have at max 2 of these leaders, so he/she can use them to produce just 2 times
     * at max per turn. This means just 2 outputBoost max. Must be in the same order as the corresponding prodLeader.*/
    public void startProduction(List<ConcreteProductionCard> cards, ResourcesWallet resAsCash, List<BoostAbility> prodLeaders,
                                   List<Resource> outputBoost, boolean basicProd, Optional<Resource> outputBasic) throws BadStorageException{
        Warehouse warBackup = new Warehouse(warehouse);
        LootChest lootBackup = new LootChest(lootChest);
        List<StorageAbility> extraStorageBackup = null;
        if(extraStorage.isEmpty() && resAsCash.anyFromExtraStorage()){
            throw new BadStorageException();
        }
        if(extraStorage.isPresent() && resAsCash.anyFromExtraStorage()){
            extraStorageBackup = new ArrayList<>();
            for(int i=0; i<extraStorage.get().size(); i++){
                extraStorageBackup.add(i, new StorageAbility(extraStorage.get().get(i)));
            }
        }

        List<Resource> tempProduction = new ArrayList<>();
        turnType = 'p';

        //Production for Production Cards
        if(cards!=null) {
            for (ConcreteProductionCard p : cards) {
                //check if they can produce
                if (!(p.equals(prodCards1.peekFirst()) || p.equals(prodCards2.peekFirst()) || p.equals(prodCards3.peekFirst()))) {
                    //controller will ask the player to adjust the ProdCards selection and try again to call this method
                    repairBackup(warBackup, lootBackup, extraStorageBackup);
                    throw new RuntimeException("Production card " + p + " cannot produce");
                }
                try {
                    tempProduction.addAll(produce(p, resAsCash));
                } catch (BadStorageException e) {
                    repairBackup(warBackup, lootBackup, extraStorageBackup);
                    throw new BadStorageException();
                }
            }
        }

        //Leader: BoostAbility production
        if(prodLeaders!=null && !prodLeaders.isEmpty()){
            if(prodLeaders.size()==outputBoost.size() && prodLeaders.size()<=2) {
                int i = 0;
                for (LeaderCard lc : prodLeaders) {
                    resAcquired.clear();
                    if (checkLeaderAvailability(lc) && outputBoost.get(i).isValidForTrading()) {
                        //resAcquired.add(outputBoost.get(i));
                        this.resAsCash = resAsCash;
                        if(!lc.applyEffect(this)){
                            //something went wrong in applying the effect
                            repairBackup(warBackup, lootBackup, extraStorageBackup);
                            throw new BadStorageException();
                        }
                        tempProduction.add(outputBoost.get(i));
                        i++;
                    }else{
                        //something went wrong in the availability of the leader
                        repairBackup(warBackup, lootBackup, extraStorageBackup);
                        throw new BadStorageException();
                    }
                }
            }else{
                //output requested is inconsistent with the possibilities given
                repairBackup(warBackup, lootBackup, extraStorageBackup);
                throw new BadStorageException();
            }
        }

        //Basic Production
        if(basicProd && outputBasic.isPresent() && outputBasic.get().isValidForTrading()){
            try {
                startBasicProduction(resAsCash);
            }catch(BadStorageException e){
                repairBackup(warBackup, lootBackup, extraStorageBackup);
                throw new BadStorageException();
            }
            tempProduction.add(outputBasic.get());
        }

        //Adding all outputs produced
        lootChest.addResources(tempProduction);

    }

    /**It resets Warehouse, Lootchest and Storage Cards substituting them with all the backups passed
     * through parameters.*/
    private void repairBackup(Warehouse warBackup, LootChest lootBackup, List<StorageAbility> extraStorageBackup){
        warehouse = warBackup;
        lootChest = lootBackup;
        if(extraStorageBackup!=null){
            extraStorage = Optional.of(extraStorageBackup);
        }else{
            extraStorage = Optional.empty();
        }
    }

    private void startBasicProduction(ResourcesWallet resourcesWallet) throws BadStorageException{
        List<Resource> fromLoot = resourcesWallet.getLootchestTray();
        List<Resource> fromWar = resourcesWallet.getWarehouseTray();
        List<List<Resource>> fromExtra = new ArrayList<>();
        for(int i=0; i<resourcesWallet.extraStorageSize(); i++){
            fromExtra.add(i, resourcesWallet.getExtraStorage(i));
        }

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
        }else if(fromExtra.size()>0 && fromExtra.get(0).size()==2) {
            input1 = fromExtra.get(0).get(0);
            input2 = fromExtra.get(0).get(1);
            basicProductionFromExtra(input1, input2, 1);
        } else if(fromExtra.size()>1 && fromExtra.get(1).size()==2){
            input1 = fromExtra.get(1).get(0);
            input2 = fromExtra.get(1).get(1);
            basicProductionFromExtra(input1, input2, 2);
        }else if(resAsCash.anyFromLootchestTray() && resAsCash.anyFromWarehouseTray()){
            input1 = fromLoot.get(0);
            input2 = fromWar.get(0);
            partialBasicProdFromLoot(input1);
            partialBasicProdFromWarehouse(input2);
        }else if(resAsCash.anyFromLootchestTray() && resAsCash.anyFromExtraStorage(0)){
            input1 = fromLoot.get(0);
            input2 = fromExtra.get(0).get(0);
            partialBasicProdFromLoot(input1);
            partialBasicProdFromExtra(input2, 1);
        }else if(resAsCash.anyFromLootchestTray() && resAsCash.anyFromExtraStorage(1)){
            input1 = fromLoot.get(0);
            input2 = fromExtra.get(1).get(0);
            partialBasicProdFromLoot(input1);
            partialBasicProdFromExtra(input2, 2);
        }else if(resAsCash.anyFromWarehouseTray() && resAsCash.anyFromExtraStorage(0)){
            input1 = fromWar.get(0);
            input2 = fromExtra.get(0).get(0);
            partialBasicProdFromWarehouse(input1);
            partialBasicProdFromExtra(input2, 1);
        }else if(resAsCash.anyFromWarehouseTray() && resAsCash.anyFromExtraStorage(1)){
            input1 = fromWar.get(0);
            input2 = fromExtra.get(1).get(0);
            partialBasicProdFromWarehouse(input1);
            partialBasicProdFromExtra(input2, 2);
        }else if(resAsCash.anyFromExtraStorage(0) && resAsCash.anyFromExtraStorage(1)){
            input1 = fromExtra.get(0).get(0);
            input2 = fromExtra.get(1).get(0);
            partialBasicProdFromExtra(input1, 1);
            partialBasicProdFromExtra(input2, 2);
        }else{
            throw new BadStorageException();
        }
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
     * @param resourcesWallet wallet of resources redistributed as the player wishes across the possible storage option*/
    private List<Resource> produce(ConcreteProductionCard card, ResourcesWallet resourcesWallet) throws BadStorageException{
        //BACKUPS
        List<Resource> cost = card.getRequiredResources();
        List<Resource> costDupe = card.getRequiredResources(); //since cannot modify cost while forEach is running and taking elements from it

        List<Resource> removeFromWar = resourcesWallet.getWarehouseTray();
        List<Resource> removeFromLoot = resourcesWallet.getLootchestTray();
        List<List<Resource>> fromExtra = new ArrayList<>();
        for(int i=0; i<resourcesWallet.extraStorageSize(); i++){
            fromExtra.add(i, resourcesWallet.getExtraStorage(i));
        }

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
           }else if(fromExtra.size()>0 && extraStorage.isPresent() && extraStorage.get().get(0).getStorageType().equals(r)){
             if(extraStorage.get().get(0).size()>0){
                 fromExtra.get(0).remove(r);
                 extraStorage.get().get(0).remove(r);
                 cost.remove(r);
             }else {
                 throw new BadStorageException();
             }
           }else if(fromExtra.size()>1 && extraStorage.isPresent() && extraStorage.get().size()>1
                    && extraStorage.get().get(1).getStorageType().equals(r)){
               if(extraStorage.get().get(1).size()>0){
                   fromExtra.get(1).remove(r);
                   extraStorage.get().get(1).remove(r);
                   cost.remove(r);
               }else {
                   throw new BadStorageException();
               }
           }else {
                throw new BadStorageException();
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

//--------------------------EXTRA STORAGE SETTER----------------------------------------
    public void setExtraStorage(StorageAbility card){
        if(card==null){
            throw new NullPointerException();
        }
        List<StorageAbility> s;
        if(extraStorage.isPresent()){
            s = extraStorage.get();
            if(s.contains(card)){
                //card already present
                return;
            }
            if(s.size()<= maxNumExtraStorage){
                //adding another card
                extraStorage.map(x -> x.add(card));
                return;
            }
            throw new RuntimeException("Too many cards");
        }else{
            //first card
            s = new ArrayList<>();
            s.add(card);
            extraStorage = Optional.of(s);
        }
    }

//--------------------------------------TOKENS----------------------------------
    public void drawActionToken (){
        ((ActionToken)((SinglePlayerGame)game).getTokenDeck().getFirst()).draw(this, (SinglePlayerGame)game);
    }

    // UPDATE MESSAGE ------------------------------------------------------------------

    String update;
    public void setUpdate(UpdateMessage update) {
        Gson gson = new Gson();
        this.update = gson.toJson(update, UpdateMessage.class);
    }

    public String getUpdate() {
        return update;
    }
}
