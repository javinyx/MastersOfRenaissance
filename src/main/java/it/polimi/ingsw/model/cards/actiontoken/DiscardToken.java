package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.player.ProPlayer;

public class DiscardToken implements ActionToken {

    private ColorEnum typeProdCard;

    public DiscardToken(ColorEnum typeProdCard){
        this.typeProdCard = typeProdCard;
    }

    public void draw(ProPlayer player, SinglePlayerGame game){

        ProductionCard prod;

        for (int i = 0; i < 12; i++) {

            prod = (ProductionCard) game.getProdDeck().get(i).peekFirst(); //prende la prima carta del i-esimo prod deck (1 dei 12)

            if (prod != null && prod.getColor().equals(typeProdCard)){     //se colore uguale allora och

                game.getProdDeck().get(i).getFirst();                      //scarta le prime 2 carte dell'i-esimo prod deck
                game.getProdDeck().get(i).getFirst();

                return;
            }
        }
    }

    @Override
    public String toString(){
        return "DiscardToken(Type ProductionCard: " + typeProdCard + ")";
    }
}
