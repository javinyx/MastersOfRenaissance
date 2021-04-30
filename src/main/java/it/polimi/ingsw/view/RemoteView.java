package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.Controller;

public class RemoteView extends View{

    Controller controller;
    Gson gson;

    public RemoteView(String playerNickname) {
        super(playerNickname);
    }



    @Override
    protected void showMessage(String message) {

    }
}
