package it.polimi.ingsw.client.commands;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.concreteMessage.BuyMarketMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.SimpleMessage;

public class BuyMarketCommand extends MessageToServerHandler {

    public BuyMarketCommand(ViewInterface view, Gson gson){
        super(view, gson);
    }

    @Override
    public void generateEnvelope(SimpleMessage message){
        String payload = gson.toJson(message, BuyMarketMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.BUY_FROM_MARKET, payload);
        sendMessageToServer(envelope);
    }
}
