package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.misc.Observer;

/**View for a local single game. No network between {@link ClientController} and {@link Controller}*/
public class LocalAdapter implements Observer<MessageEnvelope>, MessageDispatchinatorable {
    private final ClientController viewController;
    private final Controller controller;

    public LocalAdapter(ClientController viewController){
        this.viewController = viewController;
        controller = new Controller();
        controller.registerObserver(this);
    }

    /**
     * Accept messages from {@link Controller} and call the correct method in {@link ClientController}
     * @param envelope
     */
    @Override
    public void update(MessageEnvelope envelope) {

    }


    @Override
    public void generateEnvelope(MessageID messageID, String payload) {
        MessageEnvelope env = new MessageEnvelope(messageID, payload);

    }

    @Override
    public void sendMessageToServer(String nickname) {
        controller.createSinglePlayerGame(nickname);
    }

    @Override
    public void manageSurrender() {

    }
}