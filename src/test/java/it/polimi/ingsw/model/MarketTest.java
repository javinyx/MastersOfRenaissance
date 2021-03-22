package it.polimi.ingsw.model;

import it.polimi.ingsw.model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {

    Market market;

    @BeforeEach
    public void testSetup(){
        market = new Market();
    }

    @Test
    void chooseRow() {

        List<Resource> res = market.chooseRow(0);

        for (int i = 0; i < 3; i++)
            if (res.get(i) == Resource.SHIELD)
                assertEquals(market.getElem(0,i+1), Resource.SHIELD);

        for (int i = 0; i < 3; i++)
            if (res.get(i) == Resource.STONE)
                assertEquals(market.getElem(0,i+1), Resource.STONE);

        for (int i = 0; i < 3; i++)
            if (res.get(i) == Resource.COINS)
                assertEquals(market.getElem(0,i+1), Resource.COINS);

        for (int i = 0; i < 3; i++)
            if (res.get(i) == Resource.SERVANT)
                assertEquals(market.getElem(0,i+1), Resource.SERVANT);

        for (int i = 0; i < 3; i++)
            if (res.get(i) == Resource.BLANK)
                assertEquals(market.getElem(0,i+1), Resource.BLANK);

        for (int i = 0; i < 3; i++)
            if (res.get(i) == null)
                assertEquals(market.getElem(0,i+1), Resource.FAITH);

    }

    @Test
    void chooseColumn() {

        List<Resource> res = market.chooseColumn(0);

        for (int i = 0; i < 2; i++)
            if (res.get(i) == Resource.SHIELD)
                assertEquals(market.getElem(i+1,0), Resource.SHIELD);

        for (int i = 0; i < 2; i++)
            if (res.get(i) == Resource.STONE)
                assertEquals(market.getElem(i+1,0), Resource.STONE);

        for (int i = 0; i < 2; i++)
            if (res.get(i) == Resource.COINS)
                assertEquals(market.getElem(i+1,0), Resource.COINS);

        for (int i = 0; i < 2; i++)
            if (res.get(i) == Resource.SERVANT)
                assertEquals(market.getElem(i+1,0), Resource.SERVANT);

        for (int i = 0; i < 2; i++)
            if (res.get(i) == Resource.BLANK)
                assertEquals(market.getElem(i+1,0), Resource.BLANK);

        for (int i = 0; i < 2; i++)
            if (res.get(i) == null)
                assertEquals(market.getElem(i+1,0), Resource.FAITH);
    }
}