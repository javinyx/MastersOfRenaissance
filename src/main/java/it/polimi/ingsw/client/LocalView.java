package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.misc.Observer;

/**View for a local single game. No network between View and Controller/Model*/
public class LocalView extends ViewInterface implements Observer<MessageEnvelope> {

    @Override
    public void update(MessageEnvelope envelope) {

    }
}
