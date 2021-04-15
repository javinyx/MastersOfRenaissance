package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;

public abstract class ProductionCard implements Buyable, Card {
    protected ColorEnum color;
    protected int level;

    public ColorEnum getColor(){return color;}

    public int getLevel(){return level;}

    @Override
    public String toString(){
        return "Color: " + color + ", level: " + level;
    }
}
