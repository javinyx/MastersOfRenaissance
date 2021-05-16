package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.player.ProPlayer;

/**
 * The Action Token used in a single player game.
 */
public interface ActionToken extends Card {

    /**
     * Draw an action token of any type, must be done after each turn; reveal the first Action
     * Token of the stack and apply the effect illustrated.
     *
     * @param player the player who draws
     * @param game the game that the player is part of
     */
    public void draw(ProPlayer player, SinglePlayerGame game);
    int getId();
}
