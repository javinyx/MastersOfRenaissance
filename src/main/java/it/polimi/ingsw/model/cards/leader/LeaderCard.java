package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

/**
 * The interface Leader card.
 */
public interface LeaderCard extends Card{

    /**
     * Checks if the chosen LeaderCard is active on the board.
     *
     * @return true or false
     */
    boolean isActive();

    /**
     * Sets status of the chosen LeaderCard to active if true, not active if false.
     *
     * @param activate true or false
     */
    void setStatus(boolean activate);

    /**
     * Gets victory points of the chosen LeaderCard.
     *
     * @return the victory points
     */
    int getVictoryPoints();

    /**
     * Gets the cost of the chosen LeaderCard.
     *
     * @return the cost of the card
     */
    List<Buyable> getCost();

    /**
     * Apply the effect of the chosen LeaderCard to a Player.
     *
     * @param player the player to apply the LeaderCard effect to
     * @return true if effect needs to be activated, false otherwise
     */
    boolean applyEffect(ProPlayer player);


    public String getNameNew();

}
