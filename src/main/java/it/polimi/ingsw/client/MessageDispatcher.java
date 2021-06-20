package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.MessageID;

public interface MessageDispatcher {

    //----------------------SHOW MESSAGE-------------------
    void generateEnvelope(MessageID messageID, String payload);
    void sendMessageToServer(String message);
    void manageSurrender();
}
