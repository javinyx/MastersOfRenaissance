package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Disabled
    void getTurnType() {
        p3.buyFromMarket('c',1, null);
        p1.buyFromMarket('c', 1, null);
        p2.buyFromMarket('r', 2, null);
        assertEquals('m', p1.getTurnType());
        assertEquals('m', p2.getTurnType());

        p2.storeInWarehouse(Resource.COIN, 1);
        p2.storeInWarehouse(Resource.SERVANT, 2);
        assertEquals('p', p2.getTurnType());

        //TEST "BUY PRODUCTION CARD"

    }

    @Disabled
    void getTurnID() {
        assertEquals(1, p2.getTurnID());
        assertEquals(2, p3.getTurnID());
        assertEquals(1, p1.getTurnID());
    }
/*
    @Test
    void buyProductionCard() {
        ConcreteProductionCard card = istantiateACard();
    }
*/
    @Disabled
    void getVictoryPoints() {
        p1.addFaithPoints(24);
        assertEquals(20, p1.getVictoryPoints());
    }

    @Disabled
    void getResAcquired() {

    }

    @Disabled
    void getWarehouse() {
        assertNull(p1.getWarehouse().getSmallInventory());
        assertNull(p1.getWarehouse().getMidInventory());
        assertNull(p2.getWarehouse().getLargeInventory());
        warehouseSetup(p1);
        assertNotNull(p1.getWarehouse().getMidInventory());
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

    @Test
    void startBasicProduction() {
        //TEST: if some shelves in warehouse are null coz nothing is in there, there's a run time error
    }

    @Test
    void startProduction() {
    }

    @Test
    void setExtraStorage() {
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