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

import java.util.List;

/*
**guardare il tipo del paylod della message envelop
i messaggi vanno mandati a tutti o a singoli?
guardare come fare arrivare le cose dal model a qui:

fare il pattern observer (o property change) ha senso se quando il model ha problemi chiama il controller che chiama questo?
cioè chiamate vs pattern observer(property change)

 */

public class RemoteView extends View {

    Controller controller;
    Gson gson = new Gson();
    ClientConnection clientConnection;

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

    public void readMessageFromClient(MessageEnvelope envelope){

        //if (controller.getNick().equals(getNickname())) {

            switch (envelope.getMessageID()) {

                // PLAYER REGISTRATION


                // GAME PHASES

                case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(envelope.getPayload(), BuyMarketMessage.class));

                case PRODUCE -> controller.activateProdAction(gson.fromJson(envelope.getPayload(), ProduceMessage.class));

                case BUY_PRODUCTION_CARD -> controller.buyProdCardAction(gson.fromJson(envelope.getPayload(), BuyProductionMessage.class));

                case ACTIVATE_LEADER -> controller.activateLeader(gson.fromJson(envelope.getPayload(), LeaderCard.class));

                case STORE_RESOURCES -> controller.organizeResourceAction(gson.fromJson(envelope.getPayload(), StoreResourcesMessage.class));

                case PLAYERS_POSITION -> controller.faithTrackUpdate(envelope.getPayload());

                //PING PONG
                case PONG -> clientConnection.setStillConnected(true);
            }
        /*}

        else
            update(new MessageEnvelope(MessageID.WRONG_PLAYER_REQUEST, "Request from wrong player"));
    */
    }

    // MESSAGE SENDER --------------------------------------------------------------------------------------------------

    @Override
    public void update(MessageEnvelope messageToSend) {

        sendMessage(gson.toJson(messageToSend));

        if(messageToSend.getMessageID() != null && (messageToSend.getMessageID().equals(MessageID.PLAYER_WIN)
                || messageToSend.getMessageID().equals(MessageID.ABORT_GAME))) {
            clientConnection.setActive(false);
        }
    }

    private class InputMessageHandler implements Observer<String>{
        private Gson gson = new Gson();

        @Override
        public void update(String str){
            readMessageFromClient(gson.fromJson(str, MessageEnvelope.class));
        }

        @Override
        public void updateFrom(String str, String nickname){
            //niente
        }
    }
}
