package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.cards.production.ColorEnum;

public class DiscardToken implements ActionToken {

    private ColorEnum typeProdCard;

    public DiscardToken(ColorEnum typeProdCard){
        this.typeProdCard = typeProdCard;
    }

    private void discard(){}

    public void draw(){}

}
