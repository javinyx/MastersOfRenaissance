package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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

    public void generateEnvelope(MessageID messageID, String payload){
        MessageEnvelope envelope = new MessageEnvelope(messageID, payload);
        sendMessageToServer(envelope);
    }

    /**Send the envelope to the server receiver.
     * <p>It's thread-safe.</p>*/
    public synchronized void sendMessageToServer(MessageEnvelope envelope){
            if(!toServer.checkError())
                toServer.println(gson.toJson(envelope, MessageEnvelope.class));
    }


    // UTILS
    public void manageSurrender(){
        generateEnvelope(MessageID.SURRENDER, "I want to surrender");
    }
}
