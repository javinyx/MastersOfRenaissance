package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

public class ChoosePlacementsInStorageMessage extends SimpleMessage {
    private final List<Resource> resources;

    public ChoosePlacementsInStorageMessage(List<Resource> resources){
        this.resources = resources;
    }

    public List<Resource> getResources(){return resources;}
}
