package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.misc.BiElement;
import javafx.stage.Stage;

import java.io.IOException;

public class GuiController extends ClientController {
    private final Gui gui;
    private Stage stage;

    public GuiController(Stage stage, Gui gui){
        this.stage = stage;
        this.gui = gui;
    }
    @Override
    public boolean setup() throws IOException {
        BiElement<String, Integer> connectionInfo = gui.askIpAndPort();
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
    public void startGame() {

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
    public void chooseStorageAction(String msg) {

    }

    @Override
    public void chooseLeadersAction() {

    }

    @Override
    public void updateMarket() {

    }

    @Override
    public void updateAvailableProductionCards() {

    }

    @Override
    public void updateOtherPlayer(NubPlayer pp) {

    }

    @Override
    public void showCurrentTurn(String s) {

    }

    @Override
    public void buyFromMarket() {

    }

    @Override
    public void activateLeader() {

    }
}
