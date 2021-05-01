package it.polimi.ingsw.view;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.BuyMarketMessage;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.ProduceMessage;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.PrintWriter;
import java.util.ArrayList;

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

        switch (envelope.getMessageID()){
            case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(envelope.getPayload(), BuyMarketMessage.class));

            case PRODUCE ->controller.activateProdAction(gson.fromJson(envelope.getPayload(), ProduceMessage.class));

            case RESOURCE_ORGANIZED -> controller.organizeResourceAction(gson.fromJson(envelope.getPayload(), new TypeToken<ArrayList<Resource>>(){}.getType()));

            case ACTIVATE_LEADER -> controller.activateLeader(gson.fromJson(envelope.getPayload(), LeaderCard.class));



        }
    }

    // MESSAGE SENDER --------------------------------------------------------------------------------------------------

    @Override
    public void update(MessageEnvelope messageToSend) {

        sendMessage(gson.toJson(messageToSend));

        if(messageToSend.getMessageID() != null && (messageToSend.getMessageID().equals(MessageID.PLAYER_WIN) || messageToSend.getMessageID().equals(MessageID.ABORT_GAME)))
            System.out.println("gg");
            //clientConnection.setActive(false);
    }
}
