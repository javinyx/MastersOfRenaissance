package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

public interface ModelObserver {

    void updateEnd(Player player);
    Player countFinalPointsAndWinner();
    void updateEndTurn(ProPlayer player);
    void updatePosition(Player player);
    void alertVaticanReport(Player player, int vaticanReport);
    void alertDiscardResource(Player player);
    void alertDiscardResource(Player player, int quantity);
}
