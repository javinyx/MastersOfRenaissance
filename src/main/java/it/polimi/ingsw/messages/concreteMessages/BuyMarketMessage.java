package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;

import java.util.List;

public class BuyMarketMessage extends SimpleMessage {
    private final char dimension;
    private final int index;
    private final List<BiElement<MarbleAbility, Integer>> marbleUsage;

    public BuyMarketMessage(char dimension, int index, List<BiElement<MarbleAbility, Integer>> marbleUsage){
        this.dimension = dimension;
        this.index = index;
        this.marbleUsage = marbleUsage;
    }

    public char getDimension() {
        return dimension;
    }

    public int getIndex() {
        return index;
    }

    /**
     * @return a {@link BiElement} containing the MarbleAbility leaderCard and the quantity of white marbles
     * to convert with that card.
     */
    public List<BiElement<MarbleAbility, Integer>> getMarbleUsage(){
        if(marbleUsage==null || marbleUsage.isEmpty()){
            return null;
        }
        return marbleUsage;
    }
}
