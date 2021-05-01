package it.polimi.ingsw.client.commands;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.ProduceMessage;
import it.polimi.ingsw.messages.SimpleMessage;


public class ProduceCommand extends MessageToServerHandler {

    public ProduceCommand(ViewInterface view, Gson gson){
        super(view, gson);
    }

    @Override
    public void generateEnvelope(SimpleMessage message){
        String payload = gson.toJson(message, ProduceMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.PRODUCE, payload);
        sendMessageToServer(envelope);
    }
}
