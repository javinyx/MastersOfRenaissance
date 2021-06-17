package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;

import java.io.PrintWriter;

public class MessageToServerHandler implements MessageDispatchinatorable{

    private final Gson gson = new Gson();
    private final PrintWriter toServer;
    private final ClientController controller;

    public MessageToServerHandler(PrintWriter toServer, ClientController controller) {
        this.toServer = toServer;
        this.controller = controller;
    }

    /**
     * Automatically generate the message to send to the server by passing to it
     * @param messageID The corresponding Message ID
     * @param payload the payload you want to send
     */
    @Override
    public void generateEnvelope(MessageID messageID, String payload){
        MessageEnvelope envelope = new MessageEnvelope(messageID, payload);
        sendMessageToServer(gson.toJson(envelope));
    }

    @Override
    public synchronized void sendMessageToServer(String message){
        try{
            controller.setWaitingServerUpdate(true);
            toServer.println(message);
            toServer.flush();
        } catch (Exception ex){
            controller.setActive(false);
        }

    }

    // UTILS
    @Override
    public void manageSurrender(){
        generateEnvelope(MessageID.SURRENDER, "I want to surrender");
    }
}
