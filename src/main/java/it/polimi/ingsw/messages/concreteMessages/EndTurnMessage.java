package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;

import java.util.ArrayList;
import java.util.List;

public class EndTurnMessage extends SimpleMessage {
    private final Integer nextPlayerId;

    public EndTurnMessage(Integer nextPlayerId){
        this.nextPlayerId = nextPlayerId;
    }

    public Integer getNextPlayerId(){return nextPlayerId;}
}
