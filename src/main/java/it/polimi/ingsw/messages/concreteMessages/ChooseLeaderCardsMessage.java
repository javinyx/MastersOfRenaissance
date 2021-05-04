package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;

/**This message carries the request of choosing some LeaderCards among all the options ({@code leaders}) gave by the game.
 * The player has to choose as many leaders as indicated by the {@code quantity} attribute.*/
public class ChooseLeaderCardsMessage extends SimpleMessage {
    private final List<LeaderCard> leaders;
    private final int quantity;

    public ChooseLeaderCardsMessage(ProPlayer player, List<LeaderCard> leaders, int quantity){
        super(player);
        this.leaders = leaders;
        this.quantity = quantity;
    }

    public List<LeaderCard> getLeaders(){return leaders;}
    public int getQuantity(){return quantity;}
}
