package it.polimi.ingsw.view;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.client.MessagesToServer;
import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Resource;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class RemoteView extends View{

    RemoteView view;
    Controller controller;
    Gson gson;

    public RemoteView(String playerNickname) {
        super(playerNickname);
    }

    public void messageFormClient(JsonObject clientMessageReceived){
        MessagesToServer typeOfMessage = MessagesToServer.valueOf(MessagesToServer.class, gson.fromJson(clientMessageReceived.get("code"), String.class));
        switch (typeOfMessage) {

            case RESOURCE_ORGANIZED -> controller.organizeResource(gson.fromJson(clientMessageReceived.get("payload"), new TypeToken<ArrayList<Resource>>(){}.getType()));

        }
    }

    public void manageGamePhase(JsonObject clientMessageReceived){
        MessagesToServer typeOfMessage = MessagesToServer.valueOf(MessagesToServer.class, gson.fromJson(clientMessageReceived.get("code"), String.class));
        switch (typeOfMessage) {

            case BUY_FROM_MARKET -> controller.buyFromMar(gson.fromJson(clientMessageReceived.get("payload1"), Character.class), gson.fromJson(clientMessageReceived.get("payload2"), Integer.class), gson.fromJson(clientMessageReceived.get("payload3"), new TypeToken<ArrayList<LeaderCard>>(){}.getType()));

        }
    }

    @Override
    protected void showMessage(String message) {

    }
}
