package it.polimi.ingsw.model;

import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

public interface Observer {

    void updateEnd(Player player);
    void updateEndTurn(ProPlayer player);
    void updatePosition(Player player);
    void alertVaticanReport(Player player, int vaticanReport);
    void alertDiscardResource(Player player);
}
