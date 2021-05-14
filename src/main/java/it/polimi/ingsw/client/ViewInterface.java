package it.polimi.ingsw.client;

import it.polimi.ingsw.client.model.NubPlayer;

public interface ViewInterface{
    //tutti i metodi astratti che ci sarà bisogno di implementare nella cli e gui, ex: showInfo, showError, showBlaBla
    //protected ClientController controller;

    //----------------------SHOW MESSAGE-------------------
    public abstract void showMessage(String str);

    public abstract void updateMarket();
    public abstract void updateAvailableProductionCards();
    public abstract void updateThisPlayer();
    public abstract void updateOtherPlayer(NubPlayer player);
}
