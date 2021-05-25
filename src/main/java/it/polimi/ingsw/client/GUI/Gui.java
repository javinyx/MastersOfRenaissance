package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.client.model.NubPlayer;
import javafx.application.Application;
import javafx.stage.Stage;


public class Gui extends Application implements ViewInterface {
    private GuiController controller;

    public static void main(String[] args){
        Gui.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        controller = new GuiController(stage, this);
    }

    @Override
    public void showMessage(String str) {

    }


    @Override
    public void updateMarket() {

    }

    @Override
    public void updateAvailableProductionCards() {

    }

    @Override
    public void updateThisPlayer() {

    }

    @Override
    public void updateOtherPlayer(NubPlayer player) {

    }


}
