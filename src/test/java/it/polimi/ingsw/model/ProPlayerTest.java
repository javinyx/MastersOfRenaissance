package it.polimi.ingsw.model;

import it.polimi.ingsw.exception.WrongLevelException;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.StorageAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.exception.BadStorageException;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.stub.MultiPlayerGameStub;
import it.polimi.ingsw.model.stub.ProPlayerStub;
import it.polimi.ingsw.model.stub.SinglePlayerGameStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProPlayerTest extends PlayerTest {
    ProPlayer p1, p2, p3;
    ProPlayerStub p;
    Game g1, g2;
    SinglePlayerGameStub g;
    MultiPlayerGameStub gm1;
    @BeforeEach
    public void testSetup(){
        gm1 = new MultiPlayerGameStub();
        g = new SinglePlayerGameStub();
        g1 = new SinglePlayerGame(controller);
        g2 = new MultiPlayerGame(controller);
        p = new ProPlayerStub("Gatto", 1, g);
        p.registerObserver(g);
        p1 = new ProPlayer("Anacleto", 1, g1);
        p1.registerObserver(g1);
        p2 = new ProPlayer("Coco", 1, g2);
        p2.registerObserver((ModelObserver)g2);
        p3 = new ProPlayer("Noob", 2, g2);
        p3.registerObserver(g2);
    }

    @Test
    void getTurnType() {
        p3.buyFromMarket('c',1, null);
        p1.buyFromMarket('c', 1, null);
        p2.buyFromMarket('r', 2, null);
        assertEquals('m', p1.getTurnType());
        assertEquals('m', p2.getTurnType());

        p2.storeInWarehouse(Resource.COIN, 1);
        p2.storeInWarehouse(Resource.SERVANT, 2);
        assertEquals('m', p2.getTurnType());

        //TEST "BUY PRODUCTION CARD"

    }

    @Test
    void getTurnID() {
        assertEquals(1, p2.getTurnID());
        assertEquals(2, p3.getTurnID());
        assertEquals(1, p1.getTurnID());
    }

    @Test
    void buyProductionCardWithExceptions() {
        ConcreteProductionCard productionCard = g.getBuyableProductionCards().get(0);
        ResourcesWallet wallet = new ResourcesWallet();

        assertThrows(BadStorageException.class, () -> p.buyProductionCard(productionCard, 3, null, wallet));
        wallet.setWarehouseTray(productionCard.getCost());
        assertEquals(g.getBuyableProductionCards().get(0), productionCard);
        assertThrows(IndexOutOfBoundsException.class, () -> p.buyProductionCard(productionCard, 4, null, wallet));
        assertThrows(BadStorageException.class, () -> p.buyProductionCard(productionCard, 2, null, wallet));
    }

    @Test
    void buyProductionCardWithLootchest(){
        ConcreteProductionCard productionCard = g.getBuyableProductionCards().get(1);
        p.setLootChest(productionCard.getCost());
        ResourcesWallet wallet = new ResourcesWallet();
        wallet.setLootchestTray(productionCard.getCost());
        try {
            p.buyProductionCard(productionCard, 1, null, wallet);
        }catch(BadStorageException | WrongLevelException e) {
            e.printStackTrace();
        }
        assertEquals(productionCard, p.getProdCards1().peekFirst());
        assertEquals(0, p.getLootChest().getInventory().get(Resource.COIN));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.SERVANT));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.SHIELD));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.STONE));
        assertEquals('b', p.getTurnType());

        ConcreteProductionCard productionCard1 = g.getBuyableProductionCards().get(0);
        List<Resource> res = new ArrayList<>();
        res.addAll(productionCard1.getCost());
        res.add(Resource.STONE); //one res in surplus
        p.setLootChest(res);
        ResourcesWallet wallet1 = new ResourcesWallet();
        wallet1.setLootchestTray(productionCard1.getCost());
        try {
            p.buyProductionCard(productionCard1, 2, null, wallet1);
        } catch (BadStorageException | WrongLevelException e) {
            e.printStackTrace();
        }
        assertEquals(productionCard1, p.getProdCards2().peekFirst());
        assertEquals(0, p.getLootChest().getInventory().get(Resource.COIN));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.SERVANT));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.SHIELD));
        assertEquals(1, p.getLootChest().getInventory().get(Resource.STONE));
    }

    @Test
    void buyProductionCardWithWarehouse(){
        ConcreteProductionCard productionCard = g.getBuyableProductionCards().get(1);
        ResourcesWallet wallet = new ResourcesWallet();
        p.initWarehouse(productionCard.getCost());
        wallet.setWarehouseTray(productionCard.getCost());

        try {
            p.buyProductionCard(productionCard, 3, null, wallet);
        } catch (BadStorageException | WrongLevelException e) {
            e.printStackTrace();
        }
        assertEquals(productionCard, p.getProdCards3().peekFirst());
        assertNull(p.getWarehouse().getSmallInventory());
        assertTrue(p.getWarehouse().getMidInventory().isEmpty());
        assertTrue(p.getWarehouse().getLargeInventory().isEmpty());
    }

    @Test
    void buyProductionCardWithExtraStorage(){

    }

    @Test
    void getVictoryPoints() {
        p1.addFaithPoints(24);
        assertEquals(20, p1.getVictoryPoints());
    }


    @Test
    void getWarehouse() {
        ArrayList<Resource> a = new ArrayList();

        assertNull(p1.getWarehouse().getSmallInventory());
        assertEquals(a, p1.getWarehouse().getMidInventory());
        assertEquals(a, p2.getWarehouse().getLargeInventory());
        warehouseSetup(p1);
        assertNotEquals(a, p1.getWarehouse().getMidInventory());
        p.fullWarehouseInit();
        assertEquals(Resource.COIN, p.getWarehouse().getSmallInventory());
        assertEquals(2, p.getWarehouse().getMidInventory().size());
        assertEquals(3, p.getWarehouse().getLargeInventory().size());
        assertEquals(Resource.SERVANT, p.getWarehouse().getMidInventory().get(0));
        assertEquals(Resource.SERVANT, p.getWarehouse().getMidInventory().get(1));
        assertEquals(Resource.SHIELD, p.getWarehouse().getLargeInventory().get(0));
    }

    void warehouseSetup(ProPlayer p){
        p.storeInWarehouse(Resource.SERVANT, 1);
        p.storeInWarehouse(Resource.COIN, 2);
        p.storeInWarehouse(Resource.COIN, 2);
        p.storeInWarehouse(Resource.SHIELD, 3);
    }

    @Test
    void getLootChest() {
        ArrayList<Resource> res = new ArrayList<>();
        assertEquals(4, p.getLootChest().getInventory().size());
        assertEquals(0, p.getLootChest().getInventory().get(Resource.COIN));
        res.add(Resource.STONE);
        res.add(Resource.COIN);
        res.add(Resource.COIN);
        p.setLootChest(res);
        assertEquals(1, p.getLootChest().getInventory().get(Resource.STONE));
        assertEquals(2, p.getLootChest().getInventory().get(Resource.COIN));
    }

    @Test
    void buyFromMarket() {
        assertThrows(IllegalArgumentException.class, () -> p1.buyFromMarket('z', 1, null));
        assertThrows(IndexOutOfBoundsException.class, () -> p1.buyFromMarket('c', 8, null));
        p1.buyFromMarket('c', 1, null);
    }

    @Test
    void discardResources() {
        p = gm1.addPlayer("Banano");
        ProPlayerStub pm1 = gm1.addPlayer("OliOli");

        List<Resource> discarding = new ArrayList<>();
        discarding.add(Resource.COIN);
        discarding.add(Resource.STONE);
        p.discardResources(discarding);
        assertEquals(2, pm1.getCurrentPosition());
        assertEquals(0, p.getCurrentPosition());
        discarding.clear();
        discarding.add(Resource.COIN);
        discarding.add(Resource.BLANK);
        p.discardResources(discarding);
        assertEquals(3, pm1.getCurrentPosition());
        assertEquals(0, p.getCurrentPosition());
    }

    @Test
    void chooseLeaders(){
        assertThrows(IllegalArgumentException.class, () -> p.chooseLeaders(null));
        List<LeaderCard> leaderCards = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> p.chooseLeaders(leaderCards));
        leaderCards.add((LeaderCard) g.getLeaderDeckNew().getFirst());
        leaderCards.add((LeaderCard) g.getLeaderDeckNew().getFirst());
        assertEquals(2,(int)leaderCards.stream().distinct().count());
        p.chooseLeaders(leaderCards);
        assertEquals(2, p.getLeaderCards().size());
        assertEquals(leaderCards.get(0), p.getLeaderCards().get(0));
        assertEquals(leaderCards.get(1), p.getLeaderCards().get(1));
        assertFalse(p.getLeaderCards().get(0).isActive());
    }

    @Test
    void activateLeaderCard() {
        LeaderCard leader1 = (LeaderCard) g.leaderDeck.getFirst();
        LeaderCard leader2 = (LeaderCard) g.leaderDeck.getFirst();

        assertNotEquals(leader1,leader2);
        assertFalse(leader1.isActive());
        assertFalse(leader2.isActive());

        assertFalse(p.activateLeaderCard(leader1)); //doesn't have the card yet
        p.setLeaderCards(leader1);
        assertFalse(p.activateLeaderCard(leader1));//doesn't have resources to activate it

        List<Buyable> cost1 = leader1.getCost();
        if(cost1.get(0) instanceof Resource) {
            List<Resource> cost1R = cost1.stream().map(x -> (Resource)x).collect(Collectors.toList());
            p.setLootChest(cost1R);
        }else{
            List<ProductionCard> cost1P = cost1.stream().map(x -> (ProductionCard)x).collect(Collectors.toList());
            int id=1;
            List<Resource> prodCost= new ArrayList<>();
            prodCost.add(Resource.STONE);
            List<Resource> input, output;
            input = output = prodCost;
            for(ProductionCard pc : cost1P){
                ConcreteProductionCard actualCard = new ConcreteProductionCard(id,1, pc.getColor(), pc.getLevel(), prodCost, input, output);
                p.setProductionStacks(id, actualCard);
                id++;
            }
        }
        Deque<ConcreteProductionCard> cardsBackup1 = p.getProdCards1();
        Deque<ConcreteProductionCard> cardsBackup2 = p.getProdCards2();
        assertTrue(p.activateLeaderCard(leader1));
        assertEquals(cardsBackup1, p.getProdCards1());
        assertEquals(cardsBackup2, p.getProdCards2());
        assertTrue(p.activateLeaderCard(leader1));
        assertTrue(leader1.isActive());
        assertFalse(p.activateLeaderCard(leader2));
        assertFalse(leader2.isActive());
    }

    @Test
    void discardLeaderCard() {
        LeaderCard leaderCard = (LeaderCard) g.leaderDeck.getFirst();
        List<LeaderCard> playerList = p.getLeaderCards();
        p.discardLeaderCard(leaderCard);
        assertEquals(playerList, p.getLeaderCards());
        playerList = new ArrayList<>();
        playerList.add(leaderCard);
        playerList.add((LeaderCard) g.leaderDeck.getFirst());
        p.chooseLeaders(playerList);
        p.activateLeader(leaderCard);
        assertThrows(RuntimeException.class, () -> p.discardLeaderCard(leaderCard));
        p.disableLeader(leaderCard);
        p.discardLeaderCard(leaderCard);
        playerList.remove(leaderCard);
        assertEquals(playerList.size(), p.getLeaderCards().size());
        assertEquals(playerList, p.getLeaderCards());
    }

    @Test
    void chooseResource() {
    }

    @Test
    void storeInWarehouse() {
        assertFalse(p1.storeInWarehouse(null, 1));
        assertTrue(p1.storeInWarehouse(Resource.SHIELD, 1));
        assertEquals(Resource.SHIELD, p1.getWarehouse().getSmallInventory());
        assertFalse(p1.storeInWarehouse(Resource.BLANK,2));
        assertEquals(0, p1.getWarehouse().getMidInventory().size());
        assertTrue(p1.storeInWarehouse(Resource.COIN, 2));

        //different resources on the same shelf
        assertFalse(p1.storeInWarehouse(Resource.SERVANT, 2));
        assertEquals(1, p1.getWarehouse().getMidInventory().size());
        assertEquals(Resource.COIN, p1.getWarehouse().getMidInventory().get(0));

        //same type on different shelves
        assertFalse(p1.storeInWarehouse(Resource.SHIELD, 3));
        assertEquals(0, p1.getWarehouse().getLargeInventory().size());

        assertFalse(p1.storeInWarehouse(Resource.SERVANT,4));
    }

    @Test
    void isInRangeForReport() {
        assertFalse(p1.isInRangeForReport(1));
        assertFalse(p1.isInRangeForReport(2));
        assertFalse(p1.isInRangeForReport(3));
        p1.addFaithPoints(5);
        assertTrue(p1.isInRangeForReport(1));
        assertFalse(p1.isInRangeForReport(2));
        p1.addFaithPoints(8);
        assertTrue(p1.isInRangeForReport(1));
        assertTrue(p1.isInRangeForReport(2));
        assertFalse(p1.isInRangeForReport(3));
        p1.addFaithPoints(7);
        assertTrue(p1.isInRangeForReport(1));
        assertTrue(p1.isInRangeForReport(2));
        assertTrue(p1.isInRangeForReport(3));
    }

    @Test
    void startProduction() {
        ResourcesWallet wallet = new ResourcesWallet();
        ConcreteProductionCard card1, card2, card3, card4;
        p.setProductionStacks(1, card1 = g.getBuyableProductionCards().get(0)); //lv1 green
        p.setProductionStacks(1, card2 = g.getBuyableProductionCards().get(4)); //lvl2 green
        p.setProductionStacks(2, card3 = g.getBuyableProductionCards().get(1)); //lvl1 purple
        p.setProductionStacks(3, card4 = g.getBuyableProductionCards().get(11)); //lvl3 yellow

        //THAT CARD CANNOT PRODUCE
        List<ConcreteProductionCard> productionCards = new ArrayList<>();
        productionCards.add(card1);
        assertThrows(RuntimeException.class, () ->
            p.startProduction(productionCards, wallet, null, null, false, Optional.empty()));

        //NOT ENOUGH RES
        productionCards.clear();
        productionCards.add(card2);
        assertThrows(BadStorageException.class, () ->
                p.startProduction(productionCards, wallet, null, null, false, Optional.empty()));

        //NOT ENOUGH RES: check that storages are saved
        List<Resource> resources = new ArrayList<>();
        productionCards.clear();
        productionCards.add(card4);
        resources.add(Resource.COIN);
        wallet.setLootchestTray(resources);
        p.setLootChest(resources);
        assertThrows(BadStorageException.class, () ->
                p.startProduction(productionCards, wallet,null, null, false, Optional.empty()));
        assertEquals(1, p.getLootChest().getInventory().get(Resource.COIN));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.SERVANT));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.SHIELD));
        assertEquals(0, p.getLootChest().getInventory().get(Resource.STONE));


        //ACTUAL PRODUCTION
        p.setLootChest(card2.getRequiredResources());
        productionCards.clear();
        productionCards.add(card2);
        ResourcesWallet wallet2 = new ResourcesWallet();
        wallet2.setLootchestTray(card2.getRequiredResources());
        assertDoesNotThrow(() -> p.startProduction(productionCards, wallet2, null, null, false, Optional.empty()));
        int coins=1, servants=0, shields=0, stones=0;
        for(Resource r : card2.getProduction()){
            switch(r){
                case COIN -> {coins++;}
                case SERVANT -> {servants++;}
                case SHIELD -> {shields++;}
                case STONE -> {stones++;}
                default -> {}
            }
        }
        System.out.println(card2);
        assertEquals(coins, p.getLootChest().getInventory().get(Resource.COIN));
        assertEquals(servants, p.getLootChest().getInventory().get(Resource.SERVANT));
        assertEquals(shields, p.getLootChest().getInventory().get(Resource.SHIELD));
        assertEquals(stones, p.getLootChest().getInventory().get(Resource.STONE));
    }

    @Test
    void startProductionWithLeaders(){
        BoostAbility leaderCard = (BoostAbility) g.createBoostAbility();
        List<BoostAbility> leaderList = new ArrayList<>();
        leaderList.add(leaderCard);
        ConcreteProductionCard card1, card2, card3;
        List<ConcreteProductionCard> productionCards = new ArrayList<>();
        int levelIndex = 0;
        p.setLeaderCards(leaderCard);
        leaderCard.setStatus(true);
        switch(((ProductionCard) leaderCard.getCost().get(0)).getColor()){
            case GREEN -> {levelIndex = 4;}
            case PURPLE -> {levelIndex = 5;}
            case BLUE -> {levelIndex = 6;}
            case YELLOW -> {levelIndex = 7;}
        }
        p.setProductionStacks(1, card1 = g.getBuyableProductionCards().get(1)); //lv1 green
        p.setProductionStacks(1, card2 = g.getBuyableProductionCards().get(levelIndex)); //lvl2 color unknown
        p.setProductionStacks(2, card3 = g.getBuyableProductionCards().get(1)); //lvl1 purple

        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(leaderCard.getResource());
        resourceList.addAll(card2.getRequiredResources());
        p.setLootChest(resourceList);
        ResourcesWallet wallet = new ResourcesWallet();
        wallet.setLootchestTray(resourceList);

        productionCards.add(card2);
        List<Resource> leaderOutput = new ArrayList<>();
        leaderOutput.add(Resource.STONE);

        int coins=0, servants=0, shields=0, stones=1, faith=1; //stone because of leaderOutput, faith because of LeaderCard
        for(Resource r : card2.getProduction()){
            switch(r){
                case COIN -> {coins++;}
                case SERVANT -> {servants++;}
                case SHIELD -> {shields++;}
                case STONE -> {stones++;}
                case FAITH -> {faith++;}
                default -> {}
            }
        }
        System.out.println(card2);

        assertDoesNotThrow(() -> p.startProduction(productionCards, wallet, leaderList, leaderOutput, false, Optional.empty()));
        assertEquals(faith, p.getCurrentPosition());
        assertEquals(coins, p.getLootChest().getInventory().get(Resource.COIN));
        assertEquals(servants, p.getLootChest().getInventory().get(Resource.SERVANT));
        assertEquals(shields, p.getLootChest().getInventory().get(Resource.SHIELD));
        assertEquals(stones, p.getLootChest().getInventory().get(Resource.STONE));

    }

    @Test
    void startProductionBasic(){
        BoostAbility leaderCard = (BoostAbility) g.createBoostAbility();
        List<BoostAbility> leaderList = new ArrayList<>();
        leaderList.add(leaderCard);
        ConcreteProductionCard card1, card2, card3;
        List<ConcreteProductionCard> productionCards = new ArrayList<>();
        int levelIndex = 0;
        p.setLeaderCards(leaderCard);
        leaderCard.setStatus(true);
        switch(((ProductionCard) leaderCard.getCost().get(0)).getColor()){
            case GREEN -> {levelIndex = 4;}
            case PURPLE -> {levelIndex = 5;}
            case BLUE -> {levelIndex = 6;}
            case YELLOW -> {levelIndex = 7;}
        }
        p.setProductionStacks(1, card1 = g.getBuyableProductionCards().get(1)); //lv1 green
        p.setProductionStacks(1, card2 = g.getBuyableProductionCards().get(levelIndex)); //lvl2 color unknown
        p.setProductionStacks(2, card3 = g.getBuyableProductionCards().get(1)); //lvl1 purple

        List<Resource> resourceList = new ArrayList<>();
        resourceList.add(leaderCard.getResource());
        resourceList.addAll(card2.getRequiredResources());
        //for basic input
        resourceList.add(Resource.SERVANT);
        resourceList.add(Resource.COIN);
        p.setLootChest(resourceList);
        ResourcesWallet wallet = new ResourcesWallet();
        wallet.setLootchestTray(resourceList);

        productionCards.add(card2);
        List<Resource> leaderOutput = new ArrayList<>();
        leaderOutput.add(Resource.STONE);

        int coins=0, servants=0, shields=1, stones=1, faith=1; //stone because of leaderOutput, faith because of LeaderCard, shield for basicOpt
        for(Resource r : card2.getProduction()){
            switch(r){
                case COIN -> {coins++;}
                case SERVANT -> {servants++;}
                case SHIELD -> {shields++;}
                case STONE -> {stones++;}
                case FAITH -> {faith++;}
                default -> {}
            }
        }

        assertDoesNotThrow(() -> p.startProduction(productionCards, wallet, leaderList, leaderOutput, true, Optional.of(Resource.SHIELD)));
        assertEquals(faith, p.getCurrentPosition());
        assertEquals(coins, p.getLootChest().getInventory().get(Resource.COIN));
        assertEquals(servants, p.getLootChest().getInventory().get(Resource.SERVANT));
        assertEquals(shields, p.getLootChest().getInventory().get(Resource.SHIELD));
        assertEquals(stones, p.getLootChest().getInventory().get(Resource.STONE));
    }


    @Disabled
    void setExtraStorage() {
        List<Resource> cardCost = new ArrayList<>();
        cardCost.add(Resource.STONE);
        StorageAbility storageCard1 = new StorageAbility(1, 1, cardCost, Resource.SERVANT);
        cardCost.add(Resource.COIN);
        StorageAbility storageCard2 = new StorageAbility(2, 1, cardCost, Resource.SHIELD);
        StorageAbility storageCard3 = new StorageAbility(3, 1, cardCost, Resource.COIN);

        assertThrows(NullPointerException.class, () -> p.setExtraStorage(null));

        p.setExtraStorage(storageCard1);
        assertEquals(1, p.getExtraStorage().size());
        assertEquals(storageCard1, p.getExtraStorage().get(0));

        p.setExtraStorage(storageCard2);
        assertEquals(2, p.getExtraStorage().size());
        assertEquals(storageCard1, p.getExtraStorage().get(0));
        assertEquals(storageCard2, p.getExtraStorage().get(1));

        //too many cards
        p.setExtraStorage(storageCard3);
        assertThrows(RuntimeException.class, () -> p.setExtraStorage(storageCard3));

        //nothing changes cause trying to add an already present card
        p.setExtraStorage(storageCard1);
        assertEquals(2, p.getExtraStorage().size());
        assertEquals(storageCard1, p.getExtraStorage().get(0));
        assertEquals(storageCard2, p.getExtraStorage().get(1));

        assertDoesNotThrow(()-> p1.setExtraStorage(storageCard1));

    }

    @Test
    void drawActionToken(){

    }

    /* private ConcreteProductionCard istantiateACard(){
        List<Resource> required = new ArrayList<>();
        required.add(Resource.STONE);
        List<Resource> cost = new ArrayList<>();
        cost.add(Resource.COIN);
        List<Resource> prod = new ArrayList<>();
        prod.add(Resource.SERVANT);
        return new ConcreteProductionCard(ColorEnum.GREEN, required, prod, cost, 1, 1);
    }*/
}