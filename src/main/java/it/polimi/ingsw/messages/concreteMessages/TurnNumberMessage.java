package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.misc.BiElement;

import java.util.List;

public class TurnNumberMessage extends SimpleMessage {

    private List<BiElement<String, Integer>> turnAss;

    public TurnNumberMessage(List<BiElement<String, Integer>> turnAss) {
        this.turnAss = turnAss;
    }
    public List<BiElement<String, Integer>> getTurnAss() {
        return turnAss;
    }

}
