package it.polimi.ingsw.client.CLI.printer;

import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrinterTest {

    public class gigi{
        Resource[] res = new Resource[2];

        public gigi(){
            res[0] = Resource.STONE;
            res[1] = Resource.COIN;
        }

        public String getRes() {
            return res[1].toString();
        }
    }

    @BeforeEach
    void setUp() {
        List<String> resContained = new ArrayList<>();
        gigi g = new gigi();
        System.out.println("Which leader do you want to select");
        for (int i = 0; i < 2; i++) {

            System.out.println("Resource contained: " + g.getRes());
        }
    }

    @Test
    void printTable(){

    }
}