package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;

public class MoveShuffleToken implements ActionToken {

    /*private void moveCrossAndShuffle(ProPlayer player, SinglePlayerGame game){
        player.moveOnBoard(1);

        game.newTokenDeck();
    }*/

    public void draw(ProPlayer player, SinglePlayerGame game){

        game.getTokenDeck().getFirst();

        player.moveOnBoard(1);

        game.newTokenDeck();
    }

}
