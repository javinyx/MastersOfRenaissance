package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

/**
 * The ActionToken with the Move and Shuffle ability.
 */
public class MoveShuffleToken implements ActionToken {
    private final int id;
    private final int moves;

    public MoveShuffleToken(int id, int moves){
        this.id = id;
        this.moves = moves;
    }

    /**
     * Draw a DoubleMoveToken, move the Black Cross token forward by 1 space.
     * Then, shuffle all the Action Tokens and create a new deck.
     *
     * @param player the player who draws the card
     * @param game the SinglePlayerGame being played
     */
    @Override
    public void draw(Player player, SinglePlayerGame game){

        player.moveOnBoard(1);

        game.newTokenDeck();
    }

    @Override
    public String toString(){
        return "Move&Shuffle Token";
    }

    public String print() { return "Lorenzo moved by 1 position. The Token Deck has been shuffled";}

    public int getId() {
        return id;
    }
}
