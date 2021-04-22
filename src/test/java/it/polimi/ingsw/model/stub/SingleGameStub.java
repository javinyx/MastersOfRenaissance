package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.player.ProPlayer;

public class SingleGameStub extends SinglePlayerGame {

    public SingleGameStub(){
        super();
    }

    public ConcreteProductionCard getFirstProdAvailable(){
        return (ConcreteProductionCard) productionDecks.get(0).peekFirst();
    }

    public void setCurrPlayer(ProPlayer p){
        currPlayer = p;
    }
}
