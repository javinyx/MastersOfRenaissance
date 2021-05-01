package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.SimpleMessage;

import java.io.PrintWriter;

/**This class allows the Client to read/send messages from/to the server. */
public abstract class MessageToServerHandler {
    /*protected final ViewInterface view;
    protected final Gson gson;*/
    protected volatile static PrintWriter toServer;

    /**Set to whom this MessageToServerHandler has to send the envelopes.*/
    public static void setToServer(PrintWriter toServer){
        MessageToServerHandler.toServer = toServer;
    }

    public void readMessageFromServer(MessageEnvelope envelope){

    }

    public abstract void generateEnvelope(SimpleMessage message);

    /**Send the envelope to the server receiver.
     * <p>It's thread-safe.</p>*/
    public static void sendMessageToServer(MessageEnvelope envelope){
        synchronized (toServer) {
            if(!toServer.checkError())
                toServer.println(envelope);
        }
    }

}
