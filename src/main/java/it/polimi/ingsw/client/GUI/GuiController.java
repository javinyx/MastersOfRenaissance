package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;

public class GuiController extends ClientController {
    @Override
    protected void setWaitingServerUpdate(boolean b) {

    }

    @Override
    public void askNickname() {

    }

    @Override
    public void askNumberOfPlayers() {

    }

    @Override
    public boolean ackConfirmed(String msg) {
        return false;
    }

    @Override
    public void displayMessage(String str) {

    }

    @Override
    public void chooseResourceAction(String quantity) {

    }

    @Override
    public void chooseStorageAction(ChoosePlacementsInStorageMessage msg) {

    }

    @Override
    public void chooseLeadersAction(ChooseLeaderCardsMessage msg) {

    }

    @Override
    public void buyFromMarket() {

    }

    @Override
    public void activateLeader() {

    }
}
