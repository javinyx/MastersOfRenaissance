package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.player.ProPlayer;

public class MultiGameStub extends MultiplayerGame {

    public void setCurrPlayer(ProPlayer p){
        currPlayer = p;
    }

}
