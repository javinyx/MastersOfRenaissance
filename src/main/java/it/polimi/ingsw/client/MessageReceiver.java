package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
                String inputObject = (String)socketIn.readObject();
                controller.setWaitingServerUpdate(false);
                MessageEnvelope envelope = gson.fromJson(inputObject, MessageEnvelope.class);
                controller.setWaitingServerUpdate(false);
                if (controller.isRegistrationPhase()) readRegistrationMessage(envelope);
                else readGameMessage(envelope);
            }
        } catch (Exception e){
            controller.connectionError();
        }
    }


    public void readRegistrationMessage(MessageEnvelope envelope){
        controller.setLastRegistrationMessage(envelope.getMessageID());

        switch(envelope.getMessageID()){

            case ASK_NICK -> controller.askNickname();
            case PLAYER_NUM -> controller.askNumberOfPlayers();
            case CONFIRM_REGISTRATION -> controller.confirmRegistration(envelope.getPayload());

            case TOO_MANY_PLAYERS -> controller.displayMessage(envelope.getPayload());
            case CHOOSE_RESOURCE -> controller.chooseResourceAction(envelope.getPayload());

            default -> System.err.println("MessageID not recognised");
        }


    }

    public void readGameMessage(MessageEnvelope envelope) {
        controller.setLastGameMessage(envelope.getMessageID());

        switch(envelope.getMessageID()){

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
