package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.client.model.NubPlayer;

public class Gui extends ViewInterface {
    private final GuiController controller;

    public Gui(GuiController controller){
        this.controller = controller;
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
