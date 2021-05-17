package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.misc.BiElement;

import java.util.List;

public class PlayersPositionMessage extends SimpleMessage {
    private final List<BiElement<Integer, Integer>> newPlayersPosition;

    public PlayersPositionMessage(List<BiElement<Integer,Integer>> newPlayersPosition){
        this.newPlayersPosition = newPlayersPosition;
    }

    public List<BiElement<Integer,Integer>> getNewPlayersPosition(){return newPlayersPosition;}
}
