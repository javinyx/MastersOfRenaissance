package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.EndTurnMessage;
import it.polimi.ingsw.messages.concreteMessages.TurnNumberMessage;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;

import java.io.ObjectInputStream;

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

        System.out.println(envelope.getMessageID());

        switch(envelope.getMessageID()){
            case ASK_NICK -> controller.askNickname();
            case PLAYER_NUM -> controller.askNumberOfPlayers();
            case CONFIRM_REGISTRATION -> controller.confirmRegistration(envelope.getPayload());

            case TURN_NUMBER -> controller.setTotalPlayers(gson.fromJson(envelope.getPayload(), TurnNumberMessage.class));

            case CHOOSE_LEADER_CARDS -> controller.setLeaderAvailable(envelope.getPayload());
            case TOO_MANY_PLAYERS -> controller.displayMessage(envelope.getPayload());
            case CHOOSE_RESOURCE -> controller.chooseResourceAction();

            case UPDATE -> controller.updateAction(gson.fromJson(envelope.getPayload(), UpdateMessage.class));
            case CONFIRM_END_TURN -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));

            default -> System.err.println("MessageID not recognised Registration");
        }


    }

    public void readGameMessage(MessageEnvelope envelope) {
        controller.setLastGameMessage(envelope.getMessageID());

        System.out.println(envelope.getMessageID());

        switch(envelope.getMessageID()){

            case CARD_NOT_AVAILABLE -> controller.cardNotAvailable();
            case BAD_PRODUCTION_REQUEST -> controller.badProductionRequest();
            case BAD_PAYMENT_REQUEST -> controller.badPaymentRequest();
            case BAD_DIMENSION_REQUEST -> controller.badDimensionRequest();
            case WRONG_STACK_CHOICE -> controller.wrongStackRequest();
            case WRONG_LEVEL_REQUEST -> controller.wrongLevelRequest();
            case BAD_STORAGE_REQUEST -> controller.badStorageRequest();

            case CHOOSE_LEADER_CARDS -> controller.chooseLeadersAction();
            case STORE_RESOURCES -> controller.chooseStorageAfterMarketAction(envelope.getPayload());

            case UPDATE -> controller.updateAction(gson.fromJson(envelope.getPayload(), UpdateMessage.class));
            case CONFIRM_END_TURN -> {controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));}

            case LORENZO_POSITION -> controller.moveLorenzo(Integer.parseInt(envelope.getPayload()));
            case PLAYERS_POSITION -> {}


            default -> System.err.println("MessageID not recognised Game");
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
