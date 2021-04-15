package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;

/**
 * The ActionToken with the DoubleMove ability.
 */
public class DoubleMoveToken implements ActionToken {

    /* private void doubleMoveCross(ProPlayer player, SinglePlayerGame game){
        player.moveOnBoard(2);
    }*/

    /**
     * Draw a DoubleMoveToken, move the Black Cross forward by 2 spaces.
     *
     * @param player the player who draws the card
     * @param game the SinglePlayerGame being played
     */
    public void draw(ProPlayer player, SinglePlayerGame game){

        player.moveOnBoard(2);
    }

    @Override
    public String toString(){
        return "DoubleMove Token";
    }

}
