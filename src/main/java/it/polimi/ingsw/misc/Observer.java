package it.polimi.ingsw.misc;

import it.polimi.ingsw.messages.MessageID;

public interface Observer {

    void update(MessageID messageID);

    //public void organizeResourceAction(List<Resource> organizedRes);

}
