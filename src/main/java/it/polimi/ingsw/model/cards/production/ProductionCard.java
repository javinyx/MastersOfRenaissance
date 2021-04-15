package it.polimi.ingsw.model.cards.production;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;

/**
 * The abstraction of a ConcreteProductionCard.
 */
public abstract class ProductionCard implements Buyable, Card {
    /**
     * The Color.
     */
    protected ColorEnum color;
    /**
     * The Level.
     */
    protected int level;

    /**
     * Get color color enum.
     *
     * @return the color enum
     */
    public ColorEnum getColor(){return color;}

    /**
     * Get level int.
     *
     * @return the int
     */
    public int getLevel(){return level;}

    @Override
    public String toString(){
        return "Color: " + color + ", level: " + level;
    }
}
