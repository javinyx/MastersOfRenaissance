package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.player.ProPlayer;

/**
 * The ActionToken with the Discard ability.
 */
public class DiscardToken implements ActionToken {
    private final int id;
    private ColorEnum typeProdCard;

    /**
     * Instantiate a new Discard token.
     *
     * @param typeProdCard the type prod card
     */
    public DiscardToken(int id, ColorEnum typeProdCard){
        this.id = id;
        this.typeProdCard = typeProdCard;
    }

    /**
     * Draw a DiscardToken, discard 2 Production Cards of the
     * indicated type from the bottom of the
     * grid, from the lowest level to the highest
     * (if there are no more Level I cards, you must
     * discard the Level II cards, and so on).
     *
     * @param player the player who draws the card
     * @param game the SinglePlayerGame being played
     */
    @Override
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
