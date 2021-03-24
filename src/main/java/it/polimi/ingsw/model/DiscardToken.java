package it.polimi.ingsw.model;

public class DiscardToken implements ActionToken{

    private ColorEnum typeProdCard;

    public DiscardToken(ColorEnum typeProdCard){
        this.typeProdCard = typeProdCard;
    }

    private void discard(){}

    public void draw(){}

}
