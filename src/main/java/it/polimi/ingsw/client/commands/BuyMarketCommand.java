package it.polimi.ingsw.client.commands;

import com.google.gson.Gson;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.BuyMarketMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;

public class BuyMarketCommand extends MessageToServerHandler {
    private final ViewInterface view;
    private final Gson gson;

    public BuyMarketCommand(ViewInterface view, Gson gson){
        this.view = view;
        this.gson = gson;
    }

    @Override
    public void generateEnvelope(){
        BuyMarketMessage buyMarketMessage = null; //costruisci secondo i desideri del player;
        String payload = gson.toJson(buyMarketMessage, BuyMarketMessage.class);
        MessageEnvelope envelope = new MessageEnvelope(MessageID.BUY_FROM_MARKET, payload);
        sendMessageToServer(envelope);
    }
}
