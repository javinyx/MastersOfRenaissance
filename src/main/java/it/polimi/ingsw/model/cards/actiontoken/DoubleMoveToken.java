package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;

public class DoubleMoveToken implements ActionToken {

    /*private void doubleMoveCross(ProPlayer player, SinglePlayerGame game){
        player.moveOnBoard(2);
    }*/

    public void draw(ProPlayer player, SinglePlayerGame game){

        player.moveOnBoard(2);
    }

    @Override
    public String toString(){
        return "DoubleMove Token";
    }

}
