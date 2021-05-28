package it.polimi.ingsw.misc;

import it.polimi.ingsw.model.market.Resource;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class BiElementTest {

    @Test
    public void equalsTest(){
        BiElement<Resource, Storage> biElement1 = new BiElement<>(Resource.STONE, Storage.EXTRA1);
        BiElement<Resource, Storage> biElement2 = new BiElement<>(Resource.STONE, Storage.EXTRA1);

        assertEquals(biElement1, biElement2);
    }
}
