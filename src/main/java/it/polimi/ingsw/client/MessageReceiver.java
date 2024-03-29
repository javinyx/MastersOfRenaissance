package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.concreteMessages.*;

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

        try {
            while (controller.isActive()) {
                String inputObject = (String)socketIn.readObject();
                MessageEnvelope envelope = gson.fromJson(inputObject, MessageEnvelope.class);
                controller.setWaitingServerUpdate(false);
                if (controller.isRegistrationPhase()) {
                    readRegistrationMessage(envelope);
                }else {
                    readGameMessage(envelope);
                }
            }
        } catch (Exception e){
            System.err.println("A player crashed: disconnecting the whole party");
            controller.connectionError();
        }

    }

    /**
     * Read the initial messages passed by the receiver socket
     * @param envelope the message to read
     */
    public void readRegistrationMessage(MessageEnvelope envelope){
        controller.setLastRegistrationMessage(envelope.getMessageID());

        switch(envelope.getMessageID()){
            case ACK -> controller.continueTurn(Boolean.parseBoolean(envelope.getPayload()));
            case ASK_NICK -> controller.askNickname();
            case PLAYER_NUM -> controller.askNumberOfPlayers();
            case CONFIRM_REGISTRATION -> controller.confirmRegistration(envelope.getPayload());

            case TURN_NUMBER -> controller.setTotalPlayers(gson.fromJson(envelope.getPayload(), TurnNumberMessage.class));

            case CHOOSE_LEADER_CARDS -> controller.setLeaderAvailable(envelope.getPayload());
            case TOO_MANY_PLAYERS -> controller.displayMessage(envelope.getPayload());
            case CHOOSE_RESOURCE -> controller.chooseResourceAction(Integer.parseInt(envelope.getPayload()));

            case UPDATE -> controller.updateAction(envelope.deserializeUpdateMessage());
            case CONFIRM_END_TURN -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));
            case PLAYERS_POSITION -> controller.updatePositionAction(gson.fromJson(envelope.getPayload(), PlayersPositionMessage.class));

            case START_INITIAL_GAME -> controller.startInitialGame();

            case NICK_ERR -> controller.nickError();

            default -> System.err.println("MessageID not recognised Registration");
        }


    }

    /**
     * Read messages passed by the receiver socket during the game phases
     * @param envelope the message to read
     */
    public void readGameMessage(MessageEnvelope envelope) {
        controller.setLastGameMessage(envelope.getMessageID());

        switch(envelope.getMessageID()){

            case START_INITIAL_GAME -> controller.startInitialGame();
            case CONFIRM_REGISTRATION -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));

            case ACK -> controller.continueTurn(Boolean.parseBoolean(envelope.getPayload()));
            case CARD_NOT_AVAILABLE -> controller.cardNotAvailable();
            case BAD_PRODUCTION_REQUEST -> controller.badProductionRequest();
            case BAD_PAYMENT_REQUEST -> controller.badPaymentRequest();
            case BAD_DIMENSION_REQUEST -> controller.badDimensionRequest();
            case WRONG_STACK_CHOICE -> controller.wrongStackRequest();
            case WRONG_LEVEL_REQUEST -> controller.wrongLevelRequest();
            case BAD_STORAGE_REQUEST -> controller.badStorageRequest();
            case LEADER_NOT_ACTIVABLE -> controller.leaderNotActivable();
            case BAD_REARRANGE_REQUEST -> controller.badRearrangeRequest();

            case CHOOSE_LEADER_CARDS -> controller.chooseLeadersAction();
            case STORE_RESOURCES -> controller.chooseStorageAfterMarketAction(envelope.getPayload());

            case UPDATE -> controller.updateAction(envelope.deserializeUpdateMessage());
            case CONFIRM_END_TURN -> controller.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));

            case LORENZO_POSITION -> controller.upLorenzoToken(gson.fromJson(envelope.getPayload(), LorenzoInformationsMessage.class));
            case VATICAN_REPORT -> controller.infoVaticanReport(gson.fromJson(envelope.getPayload(), VaticanReportMessage.class));
            case PLAYERS_POSITION -> controller.updatePositionAction(gson.fromJson(envelope.getPayload(), PlayersPositionMessage.class));

            case ACTIVATE_LEADER -> controller.activateLeader(Integer.parseInt(envelope.getPayload()));

            case ABORT_GAME -> controller.abortGame();

            case PLAYER_WIN -> controller.winner(envelope.getPayload());
            case PLAYER_CRASHED -> controller.playerCrashed(gson.fromJson(envelope.getPayload(), EndTurnMessage.class).getNextPlayerId());

            default -> System.err.println("MessageID not recognised Game");
        }

    }

}
