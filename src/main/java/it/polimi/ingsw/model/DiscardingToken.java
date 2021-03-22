package it.polimi.ingsw.model;

public class DiscardingToken extends TokenDecorator{

    private ColorEnum typeProdCard;

    public DiscardingToken(ColorEnum typeProdCard){
        this.typeProdCard = typeProdCard;
    }

    private void discard(){}

    public void draw(){}

}
