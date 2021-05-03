package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.concreteMessage.BuyMarketMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessage.BuyProductionMessage;
import it.polimi.ingsw.messages.concreteMessage.ProduceMessage;
import it.polimi.ingsw.messages.concreteMessage.StoreResourcesMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

/*
**guardare il tipo del paylod della message envelop
i messaggi vanno mandati a tutti o a singoli?
guardare come fare arrivare le cose dal model a qui:

fare il pattern observer (o property change) ha senso se quando il model ha problemi chiama il controller che chiama questo?
cioè chiamate vs pattern observer(property change)

 */

public class RemoteView extends View implements Observer<MessageEnvelope> {

    Controller controller;
    Gson gson = new Gson();

    public RemoteView(String playerNickname, Controller controller) {
        super(playerNickname);
        this.controller = controller;
        controller.registerObserver(this);

    }

    @Override
    protected void sendMessage(String message) {
        //manda messaggio a socket che si arrangia
    }

    // MESSAGE RECEIVER ------------------------------------------------------------------------------------------------

    public void readMessageFromClient(MessageEnvelope envelope){

        if (controller.getNick().equals(getNickname())) {

            switch (envelope.getMessageID()) {

                // INITIALIZATION

                case REGISTER_SINGLE -> controller.createSinglePlayerGame(gson.fromJson(envelope.getPayload(), String.class));

                case REGISTER_MULTI -> controller.createMultiplayerGame(gson.fromJson(envelope.getPayload(), Integer.class));

                // PLAYER REGISTRATION

                case ADD_PLAYER_REQUEST -> controller.addPlayer(gson.fromJson(envelope.getPayload(), String.class));

                // GAME PHASES

                case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(envelope.getPayload(), BuyMarketMessage.class));

                case PRODUCE -> controller.activateProdAction(gson.fromJson(envelope.getPayload(), ProduceMessage.class));

                case BUY_PRODUCTION_CARD -> controller.buyProdCardAction(gson.fromJson(envelope.getPayload(), BuyProductionMessage.class));

                case ACTIVATE_LEADER -> controller.activateLeader(gson.fromJson(envelope.getPayload(), LeaderCard.class));

                case STORE_RESOURCES -> controller.organizeResourceAction(gson.fromJson(envelope.getPayload(), new TypeToken<ArrayList<BiElement<MarbleAbility, Integer>>>() {}.getType()));

            }
        }

        else
            update(new MessageEnvelope(MessageID.WRONG_PLAYER_REQUEST, "Request from wrong player"));
    }

    // MESSAGE SENDER --------------------------------------------------------------------------------------------------

    @Override
    public void update(MessageEnvelope messageToSend) {

        sendMessage(gson.toJson(messageToSend));

        if(messageToSend.getMessageID() != null && (messageToSend.getMessageID().equals(MessageID.PLAYER_WIN) || messageToSend.getMessageID().equals(MessageID.ABORT_GAME))) {
            //clientConnection.setActive(false);
        }
    }
}
