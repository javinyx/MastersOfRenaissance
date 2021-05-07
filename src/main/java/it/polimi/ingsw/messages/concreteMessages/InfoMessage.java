package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.player.ProPlayer;

/**This message informs players if something went wrong. The {@code info} specifies the type of error or
 * useful information*/
public class InfoMessage extends SimpleMessage {
    private final String info;

    public InfoMessage(String info){
        this.info = info;
    }

    public String getInfo(){return info;}
}
