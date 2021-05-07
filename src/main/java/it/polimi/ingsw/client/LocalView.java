package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.misc.Observer;

/**View for a local single game. No network between View and Controller/Model*/
public class LocalView extends ViewInterface implements Observer<MessageEnvelope> {

    public LocalView(ClientController controller){
        super(controller);
    }

    @Override
    public void update(MessageEnvelope envelope) {

    }

    @Override
    public void buyFromMarket() {

    }

    @Override
    public void activateLeader() {

    }

    @Override
    public void showMessage(String str) {

    }
}
