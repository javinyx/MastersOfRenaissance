package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;

import java.util.ArrayList;
import java.util.List;

public class EndTurnMessage extends SimpleMessage {
    private final Integer nextPlayerId;
    private final List<Integer> buyableProdCards;

    public EndTurnMessage(Integer nextPlayerId, List<Integer> buyableProdCards){
        this.nextPlayerId = nextPlayerId;
        this.buyableProdCards = new ArrayList<>(buyableProdCards);
    }

    public Integer getNextPlayerId(){return nextPlayerId;}
    public List<Integer> getBuyableProdCardsIds(){return buyableProdCards;}
}
