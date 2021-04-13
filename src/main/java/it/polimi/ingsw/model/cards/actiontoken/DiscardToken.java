package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.player.ProPlayer;
import java.util.List;

public class DiscardToken implements ActionToken {

    private ColorEnum typeProdCard;

    public DiscardToken(ColorEnum typeProdCard){
        this.typeProdCard = typeProdCard;
    }

    /*private void discard(){

    }*/

    public void draw(ProPlayer player, SinglePlayerGame game){

        game.getTokenDeck().getFirst();

        ProductionCard prod;

        for (int i = 0; i < 12; i++) {

            prod = (ProductionCard) game.getProdDeck().get(i).getFirst();

            if (prod != null && prod.getColor().equals(typeProdCard)) {

                game.getTokenDeck().getFirst();
                game.getTokenDeck().getFirst();
                break;

            }

        }



    }

}
