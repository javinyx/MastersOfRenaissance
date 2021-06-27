package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;

import java.util.List;

public class LorenzoInformationsMessage extends SimpleMessage {

    int tokenId;
    int lorenzoPosition;
    List<ConcreteProductionCard> buyableProd;

    public LorenzoInformationsMessage(int tokenId, int lorenzoPosition, List<ConcreteProductionCard> buyableProd) {
        this.tokenId = tokenId;
        this.lorenzoPosition = lorenzoPosition;
        this.buyableProd = buyableProd;
    }

    public int getTokenId() {
        return tokenId;
    }

    public int getLorenzoPosition() {
        return lorenzoPosition;
    }

    public List<ConcreteProductionCard> getBuyableProd() {
        return buyableProd;
    }
}
