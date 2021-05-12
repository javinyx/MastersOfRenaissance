package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.model.market.Resource;

import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.List;

/**This class allows the Client to read/send messages from/to the server. */
public class MessageReceiver implements Runnable{
    protected final ClientController controller;
    protected static final Gson gson = new Gson();
    private final ObjectInputStream socketIn;

    public MessageReceiver(ObjectInputStream socketIn, ClientController controller){
        this.socketIn = socketIn;
        this.controller = controller;
    }

    // MESSAGE RECEIVER ------------------------------------------------------------------------------------------------

    /**
     * Receives messages from server and calls the right method on controller.
     */
    @Override
    public void run() {
        try {
            while (controller.isActive()) {
                MessageEnvelope envelope = (MessageEnvelope) socketIn.readObject();
                controller.setWaitingServerUpdate(false);
                readMessageFromServer(envelope);
            }
        } catch (Exception e){
            controller.connectionError();
        }
    }


    public void readMessageFromServer(MessageEnvelope envelope){
        switch(envelope.getMessageID()){
            case ACK -> controller.ackConfirmed(gson.fromJson(envelope.getPayload(), String.class));

            //INITIALIZATION
            case ASK_NICK -> controller.askNickname();
            case PLAYER_NUM -> controller.askNumberOfPlayers();

            case TOO_MANY_PLAYERS -> controller.displayMessage(gson.fromJson(envelope.getPayload(), String.class));
            case CHOOSE_RESOURCE -> controller.chooseResourceAction(gson.fromJson(envelope.getPayload(), String.class));


            //GAME PHASES

            case CARD_NOT_AVAILABLE,
                 BAD_PRODUCTION_REQUEST,
                 WRONG_LEVEL_REQUEST,
                 WRONG_STACK_CHOICE,
                 BAD_DIMENSION_REQUEST,
                 BAD_PAYMENT_REQUEST -> controller.displayMessage(gson.fromJson(envelope.getPayload(), String.class));

            case CHOOSE_LEADER_CARDS -> controller.chooseLeadersAction(gson.fromJson(envelope.getPayload(), ChooseLeaderCardsMessage.class));
            case STORE_RESOURCES -> controller.chooseStorageAction(gson.fromJson(envelope.getPayload(), new TypeToken<List<Resource>>(){}.getType()));

            case UPDATE -> controller.updateAction(gson.fromJson(envelope.getPayload(), UpdateMessage.class));


            default -> System.err.println("MessageID not recognised");
        }
    }
}
