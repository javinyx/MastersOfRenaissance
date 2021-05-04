package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.player.ProPlayer;

public class ChooseResourceMessage extends SimpleMessage {
    private final int quantity;

    public ChooseResourceMessage(ProPlayer player, int quantity){
        super(player);
        this.quantity = quantity;
    }

    public int getQuantity(){return quantity;}
}
