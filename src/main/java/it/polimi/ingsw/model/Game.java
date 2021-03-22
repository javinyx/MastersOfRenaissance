package it.polimi.ingsw.model;

public interface Game{

    void start();
    void createPlayer(String nickname);
    Market getMarket();
}
