package it.polimi.ingsw.view;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.client.MessagesToServer;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.messages.BuyMarketMessage;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class RemoteView extends View{

    Controller controller;
    Gson gson;

    public RemoteView(String playerNickname) {
        super(playerNickname);
    }

    public void messageFormClient(MessageEnvelope message){

        switch (message.getMessageID()){

            case RESOURCE_ORGANIZED -> controller.organizeResourceAction(gson.fromJson(message.getPayload(), new TypeToken<ArrayList<Resource>>(){}.getType()));

            case BUY_FROM_MARKET -> controller.buyFromMarAction(gson.fromJson(message.getPayload(), BuyMarketMessage.class));




        }
    }

    @Override
    protected void showMessage(String message) {

    }
}
