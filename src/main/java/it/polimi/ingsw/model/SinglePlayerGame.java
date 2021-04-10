package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PopePass;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;


public class SinglePlayerGame extends Game implements Observer{
    private Market market;

    private ProPlayer player;
    private Player lorenzo;
    private Deck tokenDeck;
    private Player winner;

    public SinglePlayerGame() {
        market = new Market();
        player = null;
        lorenzo = new Player("Lorenzo", this);
        winner = null;
    }

    public void start(){}
    public void createPlayer(String nickname){
        if(nickname==null){
            throw new NullPointerException();
        }
        if(nickname.equals(lorenzo.getNickname())){
            throw new IllegalArgumentException("Player cannot have "+lorenzo.getNickname()+ " as nickname");
        }
        player = new ProPlayer(nickname, 1, this);
    }
    public Market getMarket(){
        return market;
    }

    public Player getWinner(){
        return winner;
    }

    /**Set the winner between Lorenzo and the player.
     * <p>If Lorenzo got to the end of the board first or the player has bought all production cards of one type,
     * then Lorenzo is the winner. Otherwise, if the player has reached the end first or has bought their 7th
     * production card, then player is the winner.</p>
     * @param player put the correct player accordingly to the occasions described above*/
    public void updateEnd(Player player){
        //lorenzo got to the end of the board before the player
        if(player.equals(lorenzo)){
            winner = lorenzo;
            end(winner);
            return;
        }
        this.player.getVictoryPoints();
        winner = this.player;
        end(winner);
    }
    public void updatePosition(Player player){
        //call controller to show the movement to the view
    }
    public void updateEndTurn(ProPlayer player){} //do nothing?

    /**If either the player or Lorenzo triggers a Vatican Report by moving on their faith track, call this method
     * that will:
     * <li>Check if the player is in range for the report triggered by Lorenzo and eventually activate their PopePass</li>
     * <li>Just activate the player's PopePass if they're the one who triggered the Report.</li>
     * @param player the one who triggered the Vatican Report
     * @param vaticanReport indicates which report has been triggered (must be between 1 and 3)*/
    public void alertVaticanReport(Player player, int vaticanReport){
        ArrayList<PopePass> passes = this.player.getPopePasses();
        if(this.player.isInVaticanReportRange(vaticanReport)){
            passes.get(vaticanReport-1).activate();
        }else{
            passes.get(vaticanReport-1).disable();
        }
    }

    /**If the player discard a resource, it gives 1 faith point to Lorenzo that will go forward on the faith track. */
    public void alertDiscardResource(Player player){
        lorenzo.moveOnBoard(1);
    }

    public void end(Player winner){}
}
