package it.polimi.ingsw.client.commands;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.messages.concreteMessages.BuyMarketMessage;

public class BuyMarketCommand extends MessageHandler {

    public BuyMarketCommand(ClientController controller){
        super(controller);
    }


    public void generateEnvelope(SimpleMessage message){
        String payload = gson.toJson(message, BuyMarketMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.BUY_FROM_MARKET, payload);
        sendMessageToServer(envelope);
    }
}
