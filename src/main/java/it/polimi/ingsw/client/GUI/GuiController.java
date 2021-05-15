package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiController extends ClientController {
    private Stage stage;

    public GuiController(Stage stage){
        this.stage = stage;
    }
    @Override
    public boolean setup() throws IOException {
        return true;
    }

    @Override
    public void setWaitingServerUpdate(boolean b) {
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
    public void chooseResourceAction() {

    }

    @Override
    public void chooseStorageAction(ChoosePlacementsInStorageMessage msg) {

    }

    @Override
    public void chooseLeadersAction() {

    }

    @Override
    public void buyFromMarket() {

    }

    @Override
    public void activateLeader() {

    }
}
