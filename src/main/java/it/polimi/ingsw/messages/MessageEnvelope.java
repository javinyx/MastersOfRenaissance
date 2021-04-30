package it.polimi.ingsw.messages;

public class MessageEnvelope {
    private final MessageID type;
    private final String payload;

    public MessageEnvelope(MessageID type, String payload){
        this.type = type;
        this.payload = payload;
    }

    public MessageID getMessageID(){
        return type;
    }
}
