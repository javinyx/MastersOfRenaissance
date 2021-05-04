package it.polimi.ingsw.client.commands;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.messages.concreteMessages.StoreResourcesMessage;

public class StoreResourcesCommand extends MessageHandler {

    public StoreResourcesCommand(ViewInterface view, Gson gson){
        super(view, gson);
    }



    public void generateEnvelope(SimpleMessage message) {
        String payload = gson.toJson(message, StoreResourcesMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.STORE_RESOURCES, payload);
        sendMessageToServer(envelope);
    }
}
