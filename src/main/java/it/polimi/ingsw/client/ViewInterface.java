package it.polimi.ingsw.client;

import com.google.gson.Gson;

public abstract class ViewInterface{
    //tutti i metodi astratti che ci sarà bisogno di implementare nella cli e gui, ex: showInfo, showError, showBlaBla
    protected ClientController controller;
    protected final Gson gson = new Gson();

    public ViewInterface(ClientController controller){
        this.controller = controller;
    }
    public abstract void buyFromMarket();
    public abstract void activateLeader();

    //----------------------SHOW MESSAGE-------------------
    public abstract void showMessage(String str);
}
