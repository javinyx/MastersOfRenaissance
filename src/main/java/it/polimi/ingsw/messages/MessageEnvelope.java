package it.polimi.ingsw.messages;

/**A MessageEnvelope's object represents a message in transit in a client-server connection.
 * It's a wrapper of the Message, since it flattens all the additional information into a string
 * called {@code payload}*/
public class MessageEnvelope {
    private final MessageID type;
    private final Object payload;

    public MessageEnvelope(MessageID type, Object payload){
        this.type = type;
        this.payload = payload;
    }

    public MessageID getMessageID(){
        return type;
    }

    public Object getPayload() {
        return payload;
    }
}
