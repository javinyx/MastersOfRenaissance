package it.polimi.ingsw.model;

import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;

public interface Game{

    void start();
    void createPlayer(String nickname);
    Market getMarket();
    Player getWinner();
}
