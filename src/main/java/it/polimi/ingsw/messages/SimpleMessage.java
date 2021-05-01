package it.polimi.ingsw.messages;

import it.polimi.ingsw.model.player.ProPlayer;

public abstract class SimpleMessage {
    private final ProPlayer player;

    public SimpleMessage(ProPlayer player){
        this.player = player;
    }

    public ProPlayer getPlayer(){
        return player;
    }
}
