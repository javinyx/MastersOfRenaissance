package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.leader.StorageAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProPlayerTest extends PlayerTest {
    ProPlayer p1, p2, p3;
    @BeforeEach
    public void testSetup(){
        Game g1 = new SinglePlayerGame();
        Game g2 = new MultiplayerGame();
        p1 = new ProPlayer("Anacleto", 1, g1);
        p1.registerObserver((Observer) g1);
        p2 = new ProPlayer("Coco", 1, g2);
        p2.registerObserver((Observer)g2);
        p3 = new ProPlayer("Noob", 2, g2);
        p3.registerObserver((Observer)g2);
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

    @Disabled
    void buyProductionCard() {
        ConcreteProductionCard productionCard = (ConcreteProductionCard) (p1.getGame()).getProductionDeck(0).peekFirst();
        ResourcesWallet wallet = new ResourcesWallet();

        assertThrows(IndexOutOfBoundsException.class, () -> p1.buyProductionCard(productionCard, 4, null, wallet));
        assertThrows(RuntimeException.class, ()-> p1.buyProductionCard(productionCard, 1, null, wallet));

        if(fillWarehouse(productionCard, p1)){
            wallet.setWarehouseTray(productionCard.getCost());
            p1.buyProductionCard(productionCard, 1, null, wallet);
            assertEquals(productionCard, p1.getProdCards1().peekFirst());
        }
    }

    private boolean fillWarehouse(ConcreteProductionCard card, ProPlayer p){
        switch(card.getId()){
            case 1: p.storeInWarehouse(Resource.SHIELD, 2);
                p.storeInWarehouse(Resource.SHIELD, 2);
                return true;
            case 5: p.storeInWarehouse(Resource.SHIELD,1);
                p.storeInWarehouse(Resource.SERVANT, 2);
                p.storeInWarehouse(Resource.COIN,3);
                return true;
            case 9: p.storeInWarehouse(Resource.SHIELD,3);
                p.storeInWarehouse(Resource.SHIELD,3);
                p.storeInWarehouse(Resource.SHIELD, 3);
                return true;
            default: return false;
        }
    }

    @Test
    void getVictoryPoints() {
        p1.addFaithPoints(24);
        assertEquals(20, p1.getVictoryPoints());
    }

    @Test
    void getResAcquired() {

    }

    @Test
    void getWarehouse() {
        ArrayList<Resource> a = new ArrayList();

        assertNull(p1.getWarehouse().getSmallInventory());
        assertEquals(a, p1.getWarehouse().getMidInventory());
        assertEquals(a, p2.getWarehouse().getLargeInventory());
        warehouseSetup(p1);
        assertNotEquals(a, p1.getWarehouse().getMidInventory());
    }

    void warehouseSetup(ProPlayer p){
        p.storeInWarehouse(Resource.SERVANT, 1);
        p.storeInWarehouse(Resource.COIN, 2);
        p.storeInWarehouse(Resource.COIN, 2);
        p.storeInWarehouse(Resource.SHIELD, 3);
    }

    @Test
    void getLootChest() {
    }

    @Test
    void buyFromMarket() {
    }

    @Test
    void discardResources() {
    }

    @Test
    void activateLeaderCard() {
    }

    @Test
    void discardLeaderCard() {
    }

    @Test
    void chooseResource() {
    }

    @Test
    void storeInWarehouse() {
    }

    @Test
    void addFaithPoints() {
    }

    @Test
    void isInRangeForReport() {
    }

    @Disabled
    void startProduction() {
        List<Deck> prodDecks = Deck.createProdDeckList();
        ConcreteProductionCard productionCard = (ConcreteProductionCard) prodDecks.get(0).getFirst();
        ResourcesWallet wallet = new ResourcesWallet();
        List<ConcreteProductionCard> cards = new ArrayList<>();
        cards.add(productionCard);
        p1.storeInWarehouse(Resource.COIN, 1);
        p1.buyProductionCard(productionCard, 1, null, wallet);

        assertThrows(RuntimeException.class, () -> p1.startProduction(cards, wallet, null, false, null, null));

        List<Resource> resFromLoot = new ArrayList<>();
        resFromLoot.add(Resource.COIN);
        wallet.setLootchestTray(resFromLoot);
    }


    @Test
    void setExtraStorage() {
        List<Resource> cardCost = new ArrayList<>();
        cardCost.add(Resource.STONE);
        StorageAbility storageCard1 = new StorageAbility(1, 1, cardCost, Resource.SERVANT);
        cardCost.add(Resource.COIN);
        StorageAbility storageCard2 = new StorageAbility(2, 1, cardCost, Resource.SHIELD);
        StorageAbility storageCard3 = new StorageAbility(3, 1, cardCost, Resource.COIN);

        assertNull(p1.getExtraStorage1());
        assertNull(p1.getExtraStorage2());
        p1.setExtraStorage(storageCard1);
        assertEquals(storageCard1, p1.getExtraStorage1());
        assertNull(p1.getExtraStorage2());

        p1.setExtraStorage(storageCard2);
        assertEquals(storageCard1, p1.getExtraStorage1());
        assertEquals(storageCard2, p1.getExtraStorage2());
        assertThrows(RuntimeException.class, () -> p1.setExtraStorage(storageCard3));

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