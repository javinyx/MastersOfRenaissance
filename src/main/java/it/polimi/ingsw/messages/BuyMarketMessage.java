package it.polimi.ingsw.messages;

import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class BuyMarketMessage extends SimpleMessage{
    private final char dimension;
    private final int index;
    private final List<BiElement<MarbleAbility, Integer>> marbleUsage;

    public BuyMarketMessage(ProPlayer player, char dimension, int index, List<BiElement<MarbleAbility, Integer>> marbleUsage){
        super(player);
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

    public List<BiElement<MarbleAbility, Integer>> getMarbleUsage(){
        if(marbleUsage==null || marbleUsage.isEmpty()){
            return null;
        }
        return marbleUsage;
    }
}
