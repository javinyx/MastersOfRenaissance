package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;

import java.io.PrintWriter;

public class MessageToServerHandler {

    private final Gson gson = new Gson();
    private final PrintWriter toServer;
    private final ClientController controller;

    public MessageToServerHandler(PrintWriter toServer, ClientController controller) {
        this.toServer = toServer;
        this.controller = controller;
    }

    protected PrintWriter getToServer(){return toServer;}

    public void generateEnvelope(MessageID messageID, String payload){
        MessageEnvelope envelope = new MessageEnvelope(messageID, payload);
        sendMessageToServer(gson.toJson(envelope));
    }

    public void sendMessageToServer(String message){
        try{
            controller.setWaitingServerUpdate(true);
            toServer.println(message);
            toServer.flush();
        } catch (Exception ex){
            controller.setActive(false);
        }

    }

    // UTILS
    public void manageSurrender(){
        generateEnvelope(MessageID.SURRENDER, "I want to surrender");
    }
}
