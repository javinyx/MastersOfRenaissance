package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.player.ProPlayer;

public interface ActionToken extends Card {

    public void draw(ProPlayer player, SinglePlayerGame game);

}
