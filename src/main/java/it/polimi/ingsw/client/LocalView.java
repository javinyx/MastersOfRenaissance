package it.polimi.ingsw.client;

import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.misc.Observer;

/**View for a local single game. No network between View and Controller/Model*/
public class LocalView implements Observer<MessageEnvelope>, ViewInterface {
    private ClientController controller;

    public LocalView(ClientController controller){
        this.controller = controller;
    }

    @Override
    public void update(MessageEnvelope envelope) {

    }

    public void buyFromMarket() {

    }

    public void activateLeader() {

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
