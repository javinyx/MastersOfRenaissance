package it.polimi.ingsw.client.commands;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.ProduceMessage;
import it.polimi.ingsw.messages.SimpleMessage;


public class ProduceCommand extends MessageHandler {

    public ProduceCommand(ViewInterface view, Gson gson){
        super(view, gson);
    }


    public void generateEnvelope(SimpleMessage message){
        String payload = gson.toJson(message, ProduceMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.PRODUCE, payload);
        sendMessageToServer(envelope);
    }
}
