package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourcesWalletTest extends PlayerTest {
    ResourcesWallet wallet, wallet2;
    List<Resource> res;
    @BeforeEach
    void setup(){
        wallet = new ResourcesWallet();
        res = new ArrayList<>();
    }
    @Test
    void setWarehouseTray() {
        assertFalse(wallet.setWarehouseTray(null));
        assertFalse(wallet.setWarehouseTray(res));
        res.add(Resource.COIN);
        res.add(Resource.FAITH);
        assertFalse(wallet.setWarehouseTray(res));

        res.clear();
        res.add(Resource.COIN);
        res.add(Resource.STONE);
        assertTrue(wallet.setWarehouseTray(res));
        assertEquals(res, wallet.getWarehouseTray());
        //adding
        wallet.setWarehouseTray(res);
        res.addAll(res);
        assertEquals(res, wallet.getWarehouseTray());
    }

    @Test
    void setLootchestTray() {
        assertFalse(wallet.setLootchestTray(null));
        assertFalse(wallet.setLootchestTray(res));
        res.add(Resource.COIN);
        res.add(Resource.BLANK);
        assertFalse(wallet.setLootchestTray(res));

        res.clear();
        res.add(Resource.COIN);
        res.add(Resource.STONE);
        assertTrue(wallet.setLootchestTray(res));
        assertEquals(res, wallet.getLootchestTray());
        //adding
        wallet.setLootchestTray(res);
        res.addAll(res);
        assertEquals(res, wallet.getLootchestTray());
    }

    @Test
    void setExtraStorage() {
        assertFalse(wallet.setExtraStorage(null, 1));
        assertFalse(wallet.setExtraStorage(res, 0));

        res.add(Resource.COIN);
        assertTrue(wallet.setExtraStorage(res, 0));
        assertEquals(res, wallet.getExtraStorage(0));
        //adding different types of resources in 1 ability card
        res.add(Resource.STONE);
        assertFalse(wallet.setExtraStorage(res, 0));
        res.remove(Resource.STONE);
        assertEquals(res, wallet.getExtraStorage(0)); //nothing changed

        res.clear();
        res.add(Resource.COIN);
        res.add(Resource.COIN);
        assertTrue(wallet.setExtraStorage(res, 1));
        assertEquals(res, wallet.getExtraStorage(1));

        //different types all at once
        wallet2 = new ResourcesWallet();
        res.clear();
        res.add(Resource.STONE);
        assertFalse(wallet2.setExtraStorage(res, 1));
        res.add(Resource.COIN);
        assertFalse(wallet2.setExtraStorage(res, 0));
        res.clear();
        res.add(Resource.FAITH);
        assertFalse(wallet2.setExtraStorage(res, 1));
    }

    @Test
    void getWarehouseTray() {
        assertNull(wallet.getWarehouseTray());
        wallet.setWarehouseTray(res);
        assertNull(wallet.getWarehouseTray());
        res.add(Resource.FAITH);
        wallet.setWarehouseTray(res);
        assertNull(wallet.getWarehouseTray());
        res.clear();
        res.add(Resource.STONE);
        wallet.setWarehouseTray(res);
        assertNotNull(wallet.getWarehouseTray());
        assertEquals(res, wallet.getWarehouseTray());
    }

    @Test
    void getLootchestTray() {
        assertNull(wallet.getLootchestTray());
        wallet.setLootchestTray(res);
        assertNull(wallet.getLootchestTray());
        res.add(Resource.FAITH);
        wallet.setLootchestTray(res);
        assertNull(wallet.getLootchestTray());
        res.clear();
        res.add(Resource.STONE);
        wallet.setLootchestTray(res);
        assertNotNull(wallet.getLootchestTray());
        assertEquals(res, wallet.getLootchestTray());
    }

    @Test
    void getExtraStorage() {
    }

    @Test
    void extraStorageSize() {
        assertEquals(0, wallet.extraStorageSize());
        wallet.setExtraStorage(null, 0);
        assertEquals(0, wallet.extraStorageSize());
        wallet.setExtraStorage(res, 0);
        assertEquals(0, wallet.extraStorageSize());
        res.add(Resource.FAITH);
        assertEquals(0, wallet.extraStorageSize());
        res.clear();
        res.add(Resource.STONE);
        wallet.setExtraStorage(res, 1);
        assertEquals(0, wallet.extraStorageSize());
        wallet.setExtraStorage(res, 0);
        assertEquals(1, wallet.extraStorageSize());
        wallet.setExtraStorage(res, 1);
        assertEquals(2, wallet.extraStorageSize());
    }

    @Test
    void isInWarehouseTray() {
    }

    @Test
    void isInLootChestTray() {

    }

    @Test
    void isInExtraStorage() {
        List<Resource> res = new ArrayList<>();
        assertFalse(wallet.isInExtraStorage(null, 0));
        assertFalse(wallet.isInExtraStorage(Resource.COIN,0));
        assertFalse(wallet.isInExtraStorage(Resource.SHIELD, 1));
        res.add(Resource.STONE);
        wallet.setExtraStorage(res, 0);
        assertTrue(wallet.isInExtraStorage(res.get(0), 0));
        assertFalse(wallet.isInExtraStorage(res.get(0), 1));
        assertFalse(wallet.isInExtraStorage(Resource.BLANK, 0));
    }

    @Test
    void anyFromWarehouseTray() {
        assertFalse(wallet.anyFromWarehouseTray());
        List<Resource> res = new ArrayList<>();
        wallet.setWarehouseTray(res);
        assertFalse(wallet.anyFromWarehouseTray());
        res.add(Resource.FAITH);
        assertFalse(wallet.anyFromWarehouseTray());
        res.clear();
        res.add(Resource.COIN);
        wallet.setWarehouseTray(res);
        assertTrue(wallet.anyFromWarehouseTray());
        res.add(Resource.STONE);
        wallet.setWarehouseTray(res);
        assertTrue(wallet.anyFromWarehouseTray());
        res.add(Resource.FAITH);
        wallet.setWarehouseTray(res);
        assertTrue(wallet.anyFromWarehouseTray());

    }

    @Test
    void anyFromLootchestTray() {
    }

    @Test
    void anyFromExtraStorage() {
    }

    @Test
    void testAnyFromExtraStorage() {
    }

    @Test
    void isEmpty() {
        List<Resource> res = new ArrayList<>();
        assertTrue(wallet.isEmpty());
        wallet.setWarehouseTray(null);
        assertTrue(wallet.isEmpty());
        wallet.setLootchestTray(res);
        assertTrue(wallet.isEmpty());
        res.add(Resource.FAITH);
        wallet.setLootchestTray(res);
        assertTrue(wallet.isEmpty());
        res.clear();
        res.add(Resource.STONE);
        wallet.setLootchestTray(res);
        assertFalse(wallet.isEmpty());
    }
}