package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.messages.concreteMessages.ChooseResourceMessage;
import it.polimi.ingsw.messages.concreteMessages.InfoMessage;

import java.io.PrintWriter;

/**This class allows the Client to read/send messages from/to the server. */
public class MessageHandler {
    private final ViewInterface view;
    protected final Gson gson;
    private volatile static PrintWriter toServer;

    public MessageHandler(ViewInterface view, Gson gson){
        this.view = view;
        this.gson = gson;
    }

    /**Set to whom this MessageHandler has to send the envelopes.*/
    public static void setToServer(PrintWriter toServer){
        MessageHandler.toServer = toServer;
    }

    public void readMessageFromServer(MessageEnvelope envelope){
        switch(envelope.getMessageID()){
            case CHOOSE_RESOURCE -> {view.showMessage(gson.fromJson(envelope.getPayload(), ChooseResourceMessage.class));}
            case CHOOSE_PLACEMENTS_IN_STORAGE -> {view.showMessage(gson.fromJson(envelope.getPayload(), ChoosePlacementsInStorageMessage.class));}
            case CHOOSE_LEADER_CARDS -> {view.showMessage(gson.fromJson(envelope.getPayload(), ChooseLeaderCardsMessage.class));}
            case ACK -> {/*boh?*/}
            case INFO -> {view.showMessage(gson.fromJson(envelope.getPayload(), InfoMessage.class));}
            default -> {/*messageID not recognised*/}
        }
    }


    /**Send the envelope to the server receiver.
     * <p>It's thread-safe.</p>*/
    public static void sendMessageToServer(MessageEnvelope envelope){
        synchronized (toServer) {
            if(!toServer.checkError())
                toServer.println(envelope);
        }
    }

}
