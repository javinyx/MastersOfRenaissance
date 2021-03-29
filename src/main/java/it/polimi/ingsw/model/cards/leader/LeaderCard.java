package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.player.ProPlayer;

public interface LeaderCard extends Card{

    boolean isActive();

    void setStatus(boolean activate);

    int getVictoryPoints();

    Buyable getCost();

    void applyEffect(ProPlayer player);
}
