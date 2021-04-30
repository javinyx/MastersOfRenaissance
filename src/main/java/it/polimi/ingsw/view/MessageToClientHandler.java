package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.BuyMarketMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.model.market.Resource;

import java.io.PrintWriter;
import java.util.ArrayList;

public class MessageToClientHandler {
    private final Controller controller;
    private final Gson gson = new Gson();
    private final PrintWriter toClient;

    public MessageToClientHandler(Controller controller, PrintWriter toClient){
        this.controller = controller;
        this.toClient = toClient;
    }

    public void readMessageFromClient(MessageEnvelope envelope){

        switch (envelope.getMessageID()){

            case RESOURCE_ORGANIZED -> controller.organizeResourceAction(gson.fromJson(envelope.getPayload(), new TypeToken<ArrayList<Resource>>(){}.getType()));

            case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(envelope.getPayload(), BuyMarketMessage.class));

        }
    }

    public void sendMessageToClient(MessageEnvelope envelope){
        synchronized (toClient) {
            if(!toClient.checkError())
                toClient.println(envelope);
        }
    }


}
