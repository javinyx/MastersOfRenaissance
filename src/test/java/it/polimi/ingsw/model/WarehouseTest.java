package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.Warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    Warehouse warehouse;

    @BeforeEach
    public void testSetup(){
        warehouse = new Warehouse();
    }

    @Test
    void addSmall() {

        warehouse.addSmall(Resource.SHIELD);
        assertEquals(Resource.SHIELD, warehouse.getSmallInventory());
        warehouse.addSmall(Resource.COINS);
        assertEquals(Resource.SHIELD, warehouse.getSmallInventory());

    }

    @Test
    void addMid() {

        warehouse.addMid(Resource.STONE);
        warehouse.addMid(Resource.STONE);

        assertEquals(Resource.STONE, warehouse.getMidInventory().get(0));
        assertEquals(Resource.STONE, warehouse.getMidInventory().get(1));

        assertTrue(warehouse.check());
    }

    @Test
    void addLarge() {

        warehouse.addLarge(Resource.STONE);
        warehouse.addLarge(Resource.STONE);
        warehouse.addLarge(Resource.STONE);

        assertEquals(Resource.STONE, warehouse.getLargeInventory().get(0));
        assertEquals(Resource.STONE, warehouse.getLargeInventory().get(1));
        assertEquals(Resource.STONE, warehouse.getLargeInventory().get(2));

        assertTrue(warehouse.check());
    }

    @Test
    void removeSmall() {

        warehouse.addSmall(Resource.SHIELD);

        assertEquals(Resource.SHIELD, warehouse.removeSmall());

        assertTrue(warehouse.check());
        assertNull(warehouse.getSmallInventory());

    }

    @Test
    void removeMid() {

        warehouse.addMid(Resource.SHIELD);
        warehouse.addMid(Resource.STONE);

        assertFalse(warehouse.check());

        assertEquals(Resource.STONE, warehouse.removeMid());
        assertEquals(Resource.SHIELD, warehouse.removeMid());

        assertTrue(warehouse.check());

    }

    @Test
    void removeLarge() {

        warehouse.addLarge(Resource.STONE);
        warehouse.addLarge(Resource.STONE);
        warehouse.addLarge(Resource.SHIELD);

        assertFalse(warehouse.check());

        assertEquals(Resource.SHIELD, warehouse.removeLarge());
        assertEquals(Resource.STONE, warehouse.removeLarge());
        assertEquals(Resource.STONE, warehouse.removeLarge());

        assertTrue(warehouse.check());

        assertNull(warehouse.getSmallInventory());
        assertEquals(0, warehouse.getMidInventory().size());
        assertEquals(0, warehouse.getLargeInventory().size());
    }

    @Test
    void moveBetweenShelf() {

        warehouse.addMid(Resource.SHIELD);
        warehouse.addLarge(Resource.STONE);
        warehouse.addLarge(Resource.STONE);

        warehouse.moveBetweenInventory('m', 's');
        warehouse.moveBetweenInventory('l', 'm');
        warehouse.moveBetweenInventory('l', 'm');
        warehouse.moveBetweenInventory('s', 'l');

        assertEquals(Resource.STONE, warehouse.getMidInventory().get(0));
        assertEquals(Resource.STONE, warehouse.getMidInventory().get(1));
        assertEquals(Resource.SHIELD, warehouse.getLargeInventory().get(0));

    }

    @Test
    void check() {

        warehouse.addSmall(Resource.SHIELD);
        warehouse.addMid(Resource.SHIELD);
        warehouse.addLarge(Resource.SHIELD);
        warehouse.addLarge(Resource.SHIELD);

        assertFalse(warehouse.check());

        warehouse = new Warehouse();

        warehouse.addSmall(Resource.STONE);
        warehouse.addMid(Resource.SERVANT);
        warehouse.addLarge(Resource.SHIELD);
        warehouse.addLarge(Resource.COINS);

        assertFalse(warehouse.check());

        warehouse = new Warehouse();

        warehouse.addSmall(Resource.STONE);
        warehouse.addMid(Resource.SERVANT);
        warehouse.addLarge(Resource.SHIELD);
        warehouse.addLarge(Resource.SHIELD);

        assertTrue(warehouse.check());

        warehouse = new Warehouse();

        warehouse.addMid(Resource.COINS);
        warehouse.addLarge(Resource.STONE);
        warehouse.addLarge(Resource.STONE);
        warehouse.moveBetweenInventory('m', 's');
        warehouse.addMid(Resource.SHIELD);
        assertTrue(warehouse.check());

        warehouse.addMid(Resource.SERVANT);
        assertFalse(warehouse.check());

        warehouse.removeMid();
        warehouse.removeMid();
        warehouse.removeSmall();
        warehouse.addMid(Resource.SHIELD);
        warehouse.moveBetweenInventory('m', 's');
        warehouse.moveBetweenInventory( 'l', 'm');
        warehouse.moveBetweenInventory('l', 'm');
        warehouse.moveBetweenInventory('s', 'l');
        warehouse.addSmall(Resource.SERVANT);
        warehouse.addLarge(Resource.SHIELD);

        assertTrue(warehouse.check());

    }
}