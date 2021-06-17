package it.polimi.ingsw.client;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.*;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.cards.leader.LeaderCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for a local single game. No network between {@link ClientController} and {@link Controller}.
 * It's the mean of communication between the pseudo-server and pseudo-client.
 * */
public class LocalAdapter implements Observer<MessageEnvelope>, MessageDispatchinatorable {
    private final ClientController viewController;
    private final Controller controller;
    private final Gson gson = new Gson();

    public LocalAdapter(ClientController viewController){
        this.viewController = viewController;
        controller = new Controller();
        controller.registerObserver(this);
    }

    /**
     * Accept messages from {@link Controller} and call the correct method in {@link ClientController}
     * @param envelope
     */
    @Override
    public void update(MessageEnvelope envelope) {

        viewController.setLastRegistrationMessage(envelope.getMessageID());
        viewController.setLastGameMessage(envelope.getMessageID());


        switch(envelope.getMessageID()){
            case ACK -> viewController.continueTurn(Boolean.parseBoolean(envelope.getPayload()));
            case CONFIRM_END_TURN -> viewController.endTurn(gson.fromJson(envelope.getPayload(), EndTurnMessage.class));

            //INITIALIZATION
            case TURN_NUMBER -> viewController.setTotalPlayers(gson.fromJson(envelope.getPayload(), TurnNumberMessage.class));
            case CHOOSE_LEADER_CARDS -> viewController.setLeaderAvailable(envelope.getPayload());
            case CONFIRM_REGISTRATION -> viewController.confirmRegistration(envelope.getPayload());

            //case CHOOSE_LEADER_CARDS -> remoteViews.get(game.getCurrPlayer().getTurnID() - 1).update(new MessageEnvelope(messageID, "You have to choose a leader card"));
            case CHOOSE_RESOURCE -> viewController.chooseResourceAction((gson.fromJson(envelope.getPayload(), ChooseResourceMessage.class)).getQuantity());

            //GAME PHASES

            case CARD_NOT_AVAILABLE -> viewController.cardNotAvailable();
            case BAD_PRODUCTION_REQUEST -> viewController.badProductionRequest();
            case BAD_PAYMENT_REQUEST -> viewController.badPaymentRequest();
            case BAD_DIMENSION_REQUEST -> viewController.badDimensionRequest();
            case WRONG_STACK_CHOICE -> viewController.wrongStackRequest();
            case LEADER_NOT_ACTIVABLE -> viewController.leaderNotActivable();
            case BAD_REARRANGE_REQUEST -> viewController.badRearrangeRequest();
            case BAD_STORAGE_REQUEST -> viewController.badStorageRequest();
            case WRONG_LEVEL_REQUEST -> viewController.wrongLevelRequest();

            case STORE_RESOURCES -> viewController.chooseStorageAfterMarketAction(envelope.getPayload());
            case UPDATE -> viewController.updateAction(envelope.deserializeUpdateMessage());

            case LORENZO_POSITION -> viewController.upLorenzoToken(envelope.getPayload());
            case PLAYERS_POSITION -> viewController.updatePositionAction(gson.fromJson(envelope.getPayload(), PlayersPositionMessage.class));

            case VATICAN_REPORT -> viewController.infoVaticanReport(gson.fromJson(envelope.getPayload(), VaticanReportMessage.class));
            case START_INITIAL_GAME -> viewController.startInitialGame();

            case ACTIVATE_LEADER -> viewController.activateLeader(Integer.parseInt(envelope.getPayload()));

            case PLAYER_WIN -> viewController.winner(envelope.getPayload());

            default -> System.err.println("MessageID " + envelope.getMessageID() + " not recognised in class " + this.getClass());
        }
    }


    /**
     * Accept request from client and redirect them to the controller
     */
    @Override
    public void generateEnvelope(MessageID messageID, String payload) {
        switch(messageID){
            case END_TURN -> controller.endTurn();

            case CHOOSE_LEADER_CARDS -> {
                controller.chooseLeaderCards(payload, viewController.getPlayer().getNickname());
            }

            case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(payload, BuyMarketMessage.class));

            case PRODUCE -> controller.activateProdAction(gson.fromJson(payload, ProduceMessage.class));

            case BUY_PRODUCTION_CARD -> controller.buyProdCardAction(gson.fromJson(payload, BuyProductionMessage.class));

            case ACTIVATE_LEADER -> {
                List<Integer> lead = new ArrayList<>();
                lead.add(Integer.parseInt(payload));
                List<LeaderCard> l = controller.convertIdToLeaderCard(lead);
                controller.activateLeader(l.get(0));
            }

            case STORE_RESOURCES -> controller.organizeResourceAction(gson.fromJson(payload, StoreResourcesMessage.class));

            case DISCARD_LEADER -> controller.discardLeader(payload);

            case REARRANGE_WAREHOUSE -> controller.rearrangeWarehouse(gson.fromJson(payload, StoreResourcesMessage.class));

            default -> System.err.println("MessageID " + messageID + " not recognised in class " + this.getClass());
        }
    }

    @Override
    public void sendMessageToServer(String nickname) {
        MessageEnvelope env = new MessageEnvelope(MessageID.CONFIRM_REGISTRATION, nickname);
        update(env);
        controller.createSinglePlayerGame(nickname);
    }

    @Override
    public void manageSurrender() {

    }
}