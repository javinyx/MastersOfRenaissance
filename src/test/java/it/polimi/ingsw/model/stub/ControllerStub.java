package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.model.Game;

public class ControllerStub extends Controller {

    public ControllerStub(){
        super();
    }

    public void update(MessageID id){
        //do nothing
    }

    public void registerGame(Game game){
        this.game = game;
    }
}
