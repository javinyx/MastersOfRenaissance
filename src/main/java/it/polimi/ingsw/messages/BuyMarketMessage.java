package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.cards.leader.MarbleAbility;

import java.util.List;

public class BuyMarketMessage {
    private final char dimension;
    private final int index;
    private final List<MarbleUsage> array;

    public BuyMarketMessage(char dimension, int index, List<MarbleUsage> array){
        this.dimension = dimension;
        this.index = index;
        this.array = array;
    }



    private class MarbleUsage{
        private MarbleAbility card;
        private int quantity;
    }
}
