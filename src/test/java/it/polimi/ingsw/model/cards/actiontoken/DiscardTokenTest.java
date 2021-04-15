package it.polimi.ingsw.model.cards.actiontoken;

import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.player.ProPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DiscardTokenTest {

    /*
    DiscardToken(Type ProductionCard: GREEN)
    DoubleMove Token
    DiscardToken(Type ProductionCard: YELLOW)
    DiscardToken(Type ProductionCard: BLUE)
    DoubleMove Token
    Move&Shuffle Token
    DiscardToken(Type ProductionCard: PURPLE)
     */

    SinglePlayerGame game;
    ProPlayer player;

    @BeforeEach
    public void testSetUp(){
        game = new SinglePlayerGame();
        player = new ProPlayer("ale", 1, game);
    }

    @Test
    void draw() {

        while (game.getTokenDeck().peekFirst().toString().equals("Move&Shuffle Token") || game.getTokenDeck().peekFirst().toString().equals("DoubleMove Token"))
            game.getTokenDeck().getFirst();

         if (game.getTokenDeck().peekFirst().toString().equals("DiscardToken(Type ProductionCard: GREEN)")) {
             player.drawActionToken();
             assertEquals(2, game.getProdDeck().get(0).size());
         }
         else if (game.getTokenDeck().peekFirst().toString().equals("DiscardToken(Type ProductionCard: YELLOW)")) {
             player.drawActionToken();
             assertEquals(2, game.getProdDeck().get(3).size());
         }
         else if (game.getTokenDeck().toString().equals("DiscardToken(Type ProductionCard: BLUE)")) {
             player.drawActionToken();
             assertEquals(2, game.getProdDeck().get(2).size());
         }
         else if (game.getTokenDeck().toString().equals("DiscardToken(Type ProductionCard: PURPLE)")) {
             player.drawActionToken();
             assertEquals(2, game.getProdDeck().get(1).size());
         }
    }
}