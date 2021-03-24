package it.polimi.ingsw.model;

public interface Observer {

    void updateEnd(Player player);
    void updateEndTurn(ProPlayer player);
    void updatePosition(Player player);
    void alertVaticanReport(Player player, int vaticanReport);
    void alertDiscardResource(Player player);
}
