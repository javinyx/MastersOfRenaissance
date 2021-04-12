package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;


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

        /*tokenDeck.createProdDeck();
        leaderDeck.createDeck(leaderDeck.getClass());
        tokenDeck.createDeck(tokenDeck.getClass());*/
    }

    public SinglePlayerGame(String tokenFileName, String prodFileName, String leadFileName) {
        market = new Market();
        player = null;
        lorenzo = new Player("Lorenzo", this);
        winner = null;



        /*tokenDeck.createDeck(tokenDeck.getClass(), tokenFileName);
        leaderDeck.createDeck(leaderDeck.getClass(), leadFileName);
        tokenDeck.createDeck(tokenDeck.getClass(), prodFileName);*/
    }



    public void start(){


    }


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

    /**Check who is the winner between Lorenzo and the player.
     * <p>If Lorenzo got to the end of the board first or </p> */
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
    public void updatePosition(Player player){}
    public void updateEndTurn(ProPlayer player){} //do nothing?
    public void alertVaticanReport(Player player, int vaticanReport){}
    public void alertDiscardResource(Player player){
        lorenzo.moveOnBoard(1);
    }

    public void end(Player winner){}
}
