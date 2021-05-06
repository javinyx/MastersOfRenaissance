package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.messages.concreteMessages.ChooseResourceMessage;
import it.polimi.ingsw.messages.concreteMessages.InfoMessage;
import it.polimi.ingsw.misc.Observer;

/**View for a local single game. No network between View and Controller/Model*/
public class LocalView extends ViewInterface implements Observer<MessageEnvelope> {

    @Override
    public void update(MessageEnvelope envelope) {

    }

    @Override
    public void showLeaderMessage(int msg) {

    }

    @Override
    public void showMessage(ChoosePlacementsInStorageMessage msg) {

    }

    @Override
    public void showMessage(ChooseLeaderCardsMessage msg) {

    }

    @Override
    public void showMessage(InfoMessage msg) {

    }
}
