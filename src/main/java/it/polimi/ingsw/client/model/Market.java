package it.polimi.ingsw.client.model;

import it.polimi.ingsw.model.market.Resource;

public class Market {
    private Resource[][] marketBoard;
    private Resource extra;

    public Market(){marketBoard = new Resource[3][4];}

    public boolean initMarket(){
        return true;
    }
}
