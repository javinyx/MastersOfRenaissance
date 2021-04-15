package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;

public class MoveShuffleToken implements ActionToken {

    public void draw(ProPlayer player, SinglePlayerGame game){

        player.moveOnBoard(1);

        game.newTokenDeck();
    }

    public String toString(){
        return "Move&Shuffle Token";
    }

}
