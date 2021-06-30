package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.BuyMarketMessage;
import it.polimi.ingsw.messages.concreteMessages.BuyProductionMessage;
import it.polimi.ingsw.messages.concreteMessages.ProduceMessage;
import it.polimi.ingsw.messages.concreteMessages.StoreResourcesMessage;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.server.ClientConnection;

import java.util.ArrayList;
import java.util.List;

public class RemoteView extends View {

    private Controller controller;
    Gson gson = new Gson();
    private ClientConnection clientConnection;
    private Thread ping;

    public RemoteView(String playerNickname, List<String> playerNames, ClientConnection clientConnection, Controller controller) {
        super(playerNickname);
        this.controller = controller;
        this.clientConnection = clientConnection;
        clientConnection.registerObserver(new InputMessageHandler());
    }

    @Override
    protected void sendMessage(String message) {
        clientConnection.send(message);
    }

    // MESSAGE RECEIVER ------------------------------------------------------------------------------------------------

    /**
     * Message dispatcher.
     * <p>By the messageID contained in the envelope, it choose which controller's method to call in order to
     * asses players' requests.</p>
     * @param envelope
     */
    public void readMessageFromClient(MessageEnvelope envelope) {
        System.out.println(envelope.getMessageID());

        switch (envelope.getMessageID()) {

            // PLAYER REGISTRATION


            // GAME PHASES
            case END_TURN -> controller.endTurn();

            case CHOOSE_LEADER_CARDS -> controller.chooseLeaderCards(envelope.getPayload(), getNickname());

            case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(envelope.getPayload(), BuyMarketMessage.class));

            case PRODUCE -> controller.activateProdAction(gson.fromJson(envelope.getPayload(), ProduceMessage.class));

            case BUY_PRODUCTION_CARD -> controller.buyProdCardAction(gson.fromJson(envelope.getPayload(), BuyProductionMessage.class));

            case ACTIVATE_LEADER -> {
                List<Integer> lead = new ArrayList<>();
                lead.add(Integer.parseInt(envelope.getPayload()));
                List<LeaderCard> l = controller.convertIdToLeaderCard(lead);
                controller.activateLeader(l.get(0));
            }

            case STORE_RESOURCES -> controller.organizeResourceAction(gson.fromJson(envelope.getPayload(), StoreResourcesMessage.class));

            case DISCARD_LEADER -> controller.discardLeader(envelope.getPayload());

            case REARRANGE_WAREHOUSE -> controller.rearrangeWarehouse(gson.fromJson(envelope.getPayload(), StoreResourcesMessage.class));

            default -> System.err.println("MessageID not recognised in class " + this.getClass());
        }
    }

    // MESSAGE SENDER --------------------------------------------------------------------------------------------------

    @Override
    public void update(MessageEnvelope messageToSend) {

        sendMessage(gson.toJson(messageToSend));

        if (messageToSend.getMessageID() != null && (messageToSend.getMessageID().equals(MessageID.PLAYER_WIN)
                || messageToSend.getMessageID().equals(MessageID.ABORT_GAME))) {
            for (int i = 0; i <  controller.getRemoteViews().size(); i++) {
                if (!controller.getRemoteViews().get(i).equals(this)) {
                    MessageEnvelope msg = new MessageEnvelope(MessageID.ABORT_GAME, "");
                    controller.getRemoteViews().get(i).update(msg);
                }
            }
            clientConnection.setActive(false);
        }
    }

    /**
     * Read the incoming message from the client and redirect them to {@link #readMessageFromClient}
     */
    private class InputMessageHandler implements Observer<String> {
        private Gson gson = new Gson();

        @Override
        public void update(String str) {
            readMessageFromClient(gson.fromJson(str, MessageEnvelope.class));
        }

    }
}
