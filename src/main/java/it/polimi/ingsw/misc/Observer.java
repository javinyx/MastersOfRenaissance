package it.polimi.ingsw.misc;

import it.polimi.ingsw.messages.MessageID;

public interface Observer<T> {

    void update(T message);

    //public void organizeResourceAction(List<Resource> organizedRes);

}
