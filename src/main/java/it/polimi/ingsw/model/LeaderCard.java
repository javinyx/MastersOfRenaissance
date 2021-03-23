package it.polimi.ingsw.model;

public interface LeaderCard extends Card{

    boolean isActive();

    void setStatus(boolean activate);

    int getVictoryPoints();

    Buyable getCost();

    void applyEffect(ProPlayer player);
}
