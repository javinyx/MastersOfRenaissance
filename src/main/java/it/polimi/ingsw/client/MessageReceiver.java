package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.model.market.Resource;

import java.io.ObjectInputStream;
import java.util.List;

/**This class allows the Client to read/send messages from/to the server. */
public class MessageReceiver implements Runnable{
    protected final ClientController controller;
    protected static final Gson gson = new Gson();
    private final ObjectInputStream socketIn;

    private volatile Object pongLock = new Object();

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

        Thread pong = getPingPongSystem();
        pong.start();

        try {
            while (controller.isActive()) {
                String inputObject = (String)socketIn.readObject();
                controller.setWaitingServerUpdate(false);
                MessageEnvelope envelope = gson.fromJson(inputObject, MessageEnvelope.class);
                if(envelope.getMessageID().equals(MessageID.PING)){
                    synchronized (pongLock){
                        pongLock.notifyAll();
                    }
                }else {
                    controller.setWaitingServerUpdate(false);
                    if (controller.isRegistrationPhase())
                        readRegistrationMessage(envelope);
                    else
                        readGameMessage(envelope);
                }
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

            case PLAYER_LIST -> controller.setPlayerList();

            case TURN_NUMBER -> controller.setTurnNumber(Integer.parseInt(envelope.getPayload()));

            case CHOOSE_LEADER_CARDS -> controller.setLeaderAvailable(envelope.getPayload());
            case TOO_MANY_PLAYERS -> controller.displayMessage(envelope.getPayload());
            case CHOOSE_RESOURCE -> controller.chooseResourceAction();

            default -> System.err.println("MessageID not recognised");
        }


    }

    public void readGameMessage(MessageEnvelope envelope) {
        controller.setLastGameMessage(envelope.getMessageID());

        switch(envelope.getMessageID()){

            case CARD_NOT_AVAILABLE -> controller.cardNotAvailable();
            case BAD_PRODUCTION_REQUEST -> controller.badProductionRequest();
            case BAD_PAYMENT_REQUEST -> controller.badPaymentRequest();
            case BAD_DIMENSION_REQUEST -> controller.badDimensionRequest();
            case WRONG_STACK_CHOICE -> controller.wrongStackRequest();
            case WRONG_LEVEL_REQUEST -> controller.wrongLevelRequest();

            case CHOOSE_LEADER_CARDS -> controller.chooseLeadersAction();
            case STORE_RESOURCES -> controller.chooseStorageAction(gson.fromJson(envelope.getPayload(), new TypeToken<List<Resource>>(){}.getType()));

            case UPDATE -> controller.updateAction(gson.fromJson(envelope.getPayload(), UpdateMessage.class));


            default -> System.err.println("MessageID not recognised");
        }

    }

    /**Return the thread that will send a PONG message to server through {@link MessageToServerHandler} class.
     * <p>In order to send the pong, it must be awakened via {@code pongLock.notify()}. So when {@link MessageReceiver}
     *'s thread gets a PING, it should invoke this thread.</p>*/
    public Thread getPingPongSystem(){
        Gson gson = new Gson();
        MessageToServerHandler msgHandler = controller.getMessageToServerHandler();
        Thread pong = new Thread(() -> {
                while (true) {
                    synchronized (pongLock){
                    try {
                        pongLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MessageEnvelope pongEnvelope = new MessageEnvelope(MessageID.PONG, "");
                    msgHandler.sendMessageToServer(gson.toJson(pongEnvelope, MessageEnvelope.class));

                }
            }
        });

        return pong;
    }

}
