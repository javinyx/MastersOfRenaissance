package it.polimi.ingsw.model;

import it.polimi.ingsw.model.MarbleEnum;
import it.polimi.ingsw.model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarketTest {

    Market market;

    @BeforeEach
    public void testSetup(){
        market = new Market();
    }

    @Test
    void chooseRow() {
        Resource[] res = market.chooseRow(0);

        assertEquals(4, res.length);

        for (int i = 0; i < 3; i++)
            if (res[i] == Resource.SHIELD)
                assertEquals(market.getElem(0,i+1), MarbleEnum.BLUE);

        for (int i = 0; i < 3; i++)
            if (res[i] == Resource.STONE)
                assertEquals(market.getElem(0,i+1), MarbleEnum.GRAY);

        for (int i = 0; i < 3; i++)
            if (res[i] == Resource.COINS)
                assertEquals(market.getElem(0,i+1), MarbleEnum.YELLOW);

        for (int i = 0; i < 3; i++)
            if (res[i] == Resource.SERVANT)
                assertEquals(market.getElem(0,i+1), MarbleEnum.VIOLET);

        for (int i = 0; i < 3; i++)
            if (res[i] == Resource.BLANK)
                assertEquals(market.getElem(0,i+1), MarbleEnum.WHITE);

        for (int i = 0; i < 3; i++)
            if (res[i] == null)
                assertEquals(market.getElem(0,i+1), MarbleEnum.RED);

    }

    @Test
    void chooseColumn() {
        Resource[] res = market.chooseColumn(0);

        assertEquals(3, res.length);

        for (int i = 0; i < 2; i++)
            if (res[i] == Resource.SHIELD)
                assertEquals(market.getElem(i+1,0), MarbleEnum.BLUE);

        for (int i = 0; i < 2; i++)
            if (res[i] == Resource.STONE)
                assertEquals(market.getElem(i+1,0), MarbleEnum.GRAY);

        for (int i = 0; i < 2; i++)
            if (res[i] == Resource.COINS)
                assertEquals(market.getElem(i+1,0), MarbleEnum.YELLOW);

        for (int i = 0; i < 2; i++)
            if (res[i] == Resource.SERVANT)
                assertEquals(market.getElem(i+1,0), MarbleEnum.VIOLET);

        for (int i = 0; i < 2; i++)
            if (res[i] == Resource.BLANK)
                assertEquals(market.getElem(i+1,0), MarbleEnum.WHITE);

        for (int i = 0; i < 2; i++)
            if (res[i] == null)
                assertEquals(market.getElem(i+1,0), MarbleEnum.RED);

    }
}