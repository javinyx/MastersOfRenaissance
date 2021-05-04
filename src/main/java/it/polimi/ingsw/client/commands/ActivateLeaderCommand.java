package it.polimi.ingsw.client.commands;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.concreteMessages.ActivateLeaderMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.SimpleMessage;

public class ActivateLeaderCommand extends MessageHandler {

    public ActivateLeaderCommand(ViewInterface view, Gson gson){
        super(view, gson);
    }


    public void generateEnvelope(SimpleMessage message){
        String payload = gson.toJson(message, ActivateLeaderMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.ACTIVATE_LEADER, payload);
    }

}
