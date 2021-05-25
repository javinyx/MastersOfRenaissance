package it.polimi.ingsw.client.CLI.printer;

import it.polimi.ingsw.client.CLI.Color;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrinterTest {

    @BeforeEach
    void setUp() {

        Deque<ConcreteProductionCard> dp = new ArrayDeque<>();
        List<Resource> res = new ArrayList<>();
        List<Deque<ConcreteProductionCard>> pd = new ArrayList<>();
        res.add(Resource.SERVANT);res.add(Resource.SERVANT);res.add(Resource.SERVANT);res.add(Resource.FAITH);res.add(Resource.FAITH);
        ConcreteProductionCard prod = new ConcreteProductionCard(1,10, ColorEnum.PURPLE,1, res, res, res);
        it.polimi.ingsw.model.market.Market m1 = new it.polimi.ingsw.model.market.Market();
        Market m = new Market(m1.getMarketBoard(), m1.getExtraMarble());
        NubPlayer player = new NubPlayer("Luca");
        player.setCurrPos(6);
        player.setTurnNumber(1);
        player.addResources(new BiElement<>(Resource.SHIELD, Storage.WAREHOUSE_SMALL), 1);
        player.addResources(new BiElement<>(Resource.COIN, Storage.WAREHOUSE_MID), 1);
        player.addResources(new BiElement<>(Resource.SERVANT, Storage.WAREHOUSE_LARGE), 2);
        dp.add(prod);dp.add(prod);dp.add(prod);
        pd.add(dp);pd.add(dp);pd.add(dp);
        player.setProductionStacks(pd);
        player.addProductionCard(prod, 0);
        player.addProductionCard(prod, 1);
        player.addProductionCard(prod, 2);


        Printer printer = new Printer();
        printer.printBoard(player, m);

    }

    @Test
    void printBoard(){

    }
}