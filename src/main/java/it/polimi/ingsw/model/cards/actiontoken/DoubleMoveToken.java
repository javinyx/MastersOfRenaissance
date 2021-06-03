package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;
import javafx.application.Platform;

/**
 * The ActionToken with the DoubleMove ability.
 */
public class DoubleMoveToken implements ActionToken {
    private final int id;
    private final int moves;

    public DoubleMoveToken(int id, int moves){
        this.id = id;
        this.moves = moves;
    }

    /* private void doubleMoveCross(ProPlayer player, SinglePlayerGame game){
        player.moveOnBoard(2);
    }*/

    /**
     * Draw a DoubleMoveToken, move the Black Cross forward by 2 spaces.
     *
     * @param player the player who draws the card
     * @param game the SinglePlayerGame being played
     */
    @Override
    public void draw(Player player, SinglePlayerGame game){

        player.moveOnBoard(2);
    }

    @Override
    public String toString(){
        return "DoubleMove Token";
    }

    public String print() { return "Lorenzo moved by 2 positions.";}

    public int getId() {
        return id;
    }
}
