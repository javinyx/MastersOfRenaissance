package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;

import java.io.PrintWriter;

/**This class allows the Client to read/send messages from/to the server. */
public class MessageHandler {
    protected final ClientController controller;
    protected static final Gson gson = new Gson();
    private volatile static PrintWriter toServer;

    public MessageHandler(ClientController controller){
        this.controller = controller;
    }

    /**Set to whom this MessageHandler has to send the envelopes.*/
    public static void setToServer(PrintWriter toServer){
        MessageHandler.toServer = toServer;
    }

    public void readMessageFromServer(MessageEnvelope envelope){
        switch(envelope.getMessageID()){
            case CHOOSE_RESOURCE -> controller.chooseResourceAction(gson.fromJson(envelope.getPayload(), Integer.class));
            case CHOOSE_PLACEMENTS_IN_STORAGE -> controller.chooseStorageAction(gson.fromJson(envelope.getPayload(), ChoosePlacementsInStorageMessage.class));
            case CHOOSE_LEADER_CARDS -> controller.chooseLeadersAction(gson.fromJson(envelope.getPayload(), ChooseLeaderCardsMessage.class));
            case UPDATE -> {}
            case ACK -> {/*boh?*/}
            case INFO -> {controller.errorAction(gson.fromJson(envelope.getPayload(), String.class));} // questa cosa non funzoina devi fare tutti i casi di exception e metterli nella classe info, non posso mandare tanti messaggi di errore tutti con lo stesso message id
            default -> {/*messageID not recognised*/}
        }
    }

    public static void generateEnvelope(MessageID messageID, String payload){
        MessageEnvelope envelope = new MessageEnvelope(messageID, payload);
        sendMessageToServer(envelope);
    }


    /**Send the envelope to the server receiver.
     * <p>It's thread-safe.</p>*/
    public static void sendMessageToServer(MessageEnvelope envelope){
        synchronized (toServer) {
            if(!toServer.checkError())
                toServer.println(gson.toJson(envelope, MessageEnvelope.class));
        }
    }

}
