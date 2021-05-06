package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.messages.concreteMessages.ChooseResourceMessage;
import it.polimi.ingsw.messages.concreteMessages.InfoMessage;

public abstract class ViewInterface{
    //tutti i metodi astratti che ci sarà bisogno di implementare nella cli e gui, ex: showInfo, showError, showBlaBla

    public abstract void showLeaderMessage(int msg);
    public abstract void showMessage(ChoosePlacementsInStorageMessage msg);
    public abstract void showMessage(ChooseLeaderCardsMessage msg);
    public abstract void showMessage(InfoMessage msg);
}
