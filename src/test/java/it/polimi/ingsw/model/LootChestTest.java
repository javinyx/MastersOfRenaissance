package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.LootChest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LootChestTest {

    LootChest lootChest;

    @BeforeEach
    public void testSetup(){
        lootChest = new LootChest();
    }

    @Test
    void addResources() {

        lootChest.addResources(Resource.SHIELD);
        assertTrue(lootChest.getInventory().containsKey(Resource.SHIELD));
        assertEquals(1, lootChest.getNumberResInInventory(Resource.SHIELD));

        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        assertTrue(lootChest.getInventory().containsKey(Resource.STONE));
        assertEquals(4, lootChest.getNumberResInInventory(Resource.STONE));

        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        assertTrue(lootChest.getInventory().containsKey(Resource.COIN));
        assertEquals(4, lootChest.getNumberResInInventory(Resource.STONE));

        lootChest.addResources(Resource.SERVANT);
        assertTrue(lootChest.getInventory().containsKey(Resource.SERVANT));
        assertEquals(1, lootChest.getNumberResInInventory(Resource.SERVANT));


    }

    @Test
    void removeResources() {
        //1 Shield, 4 stone, 3 coins, 1 servant
        lootChest.addResources(Resource.SHIELD);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.SERVANT);

        lootChest.removeResources(Resource.SERVANT);
        assertEquals(0, lootChest.getNumberResInInventory(Resource.SERVANT));

        for (int i = 0; i < 2; i++)
            lootChest.removeResources(Resource.STONE);

        assertEquals(2, lootChest.getNumberResInInventory(Resource.STONE));


    }

    @Test
    void getNumberResInInventory() {

        lootChest.addResources(Resource.SHIELD);
        assertEquals(1, lootChest.getNumberResInInventory(Resource.SHIELD));

        lootChest.addResources(Resource.SHIELD);
        lootChest.addResources(Resource.SHIELD);
        lootChest.addResources(Resource.SHIELD);
        lootChest.addResources(Resource.SHIELD);
        assertEquals(5, lootChest.getNumberResInInventory(Resource.SHIELD));

        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        assertEquals(4, lootChest.getNumberResInInventory(Resource.STONE));

        lootChest.addResources(Resource.SERVANT);
        lootChest.addResources(Resource.SERVANT);
        lootChest.addResources(Resource.SERVANT);
        assertEquals(3, lootChest.getNumberResInInventory(Resource.SERVANT));

        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        assertEquals(5, lootChest.getNumberResInInventory(Resource.COIN));



    }

    @Test
    void getCountResInLootchest() {

        lootChest.addResources(Resource.SHIELD);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.STONE);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.COIN);
        lootChest.addResources(Resource.SERVANT);

        assertEquals(10, lootChest.getCountResInLootchest());

        lootChest.removeResources(Resource.STONE);

        assertEquals(9, lootChest.getCountResInLootchest());



    }
}