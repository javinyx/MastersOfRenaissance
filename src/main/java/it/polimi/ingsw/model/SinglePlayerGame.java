package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PopePass;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SinglePlayerGame extends Game implements Observer{
    private Market market;

    private ProPlayer player;
    private Player lorenzo;
    private Deck tokenDeck;
    private Player winner;

    /*
    protected Market market;
    protected Player winner;
    protected List<Deck> productionDecks;
    protected Deck leaderDeck;
     */

    public SinglePlayerGame() {
        market = new Market();
        player = null;
        lorenzo = new Player("Lorenzo", this);
        winner = null;

        tokenDeck = new Deck("ActionToken");
        leaderDeck = new Deck("LeaderCard");
        productionDecks = Deck.createProdDeckList();
    }

/*
    1 creare il player
    2 distribuire le carte leader
    3 inizializzare action token
    4 inizializzare prodcard
    5 mercato, gia fatto
     */

    public void start(String nick, int numPlayers){

        List<Card> tempList = new ArrayList<>();

        //distribuisci leader
        for (int i = 0; i < 4; i++) {
            tempList.add(leaderDeck.getFirst());
        }
        //player.gaveLeaderOption(tempList);

    }


    public void createPlayer(String nickname){
        if(nickname == null){
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
    public void updatePosition(Player player){
        //controller
    }
    public void updateEndTurn(ProPlayer player){} //do nothing?
    /**Alert the observer that {@code player} has triggered the {@code vaticanReport}.
     * @param vaticanReport must be between 1 and 3. */
    public void alertVaticanReport(Player player, int vaticanReport){
        if(player.equals(lorenzo)){
            ArrayList<PopePass> passes = this.player.getPopePasses();
            if(this.player.isInVaticanReportRange(vaticanReport)){
                passes.get(vaticanReport-1).activate();
            }else{
                passes.get(vaticanReport-1).disable();
            }
        }
    }
//---------------------------------------------------------------------------------------------------------
    public Deck getTokenDeck() {
        return tokenDeck;
    }

    public void newTokenDeck() {
        tokenDeck = new Deck("ActionToken");
    }

    public List<Deck> getProdDeck() {
        return productionDecks;
    }
//------------------------------------------------------------------------------------------------------------
    /**By discarding 1 resource, Lorenzo will be gifted 1 faith point. */
    public void alertDiscardResource(Player player){
        lorenzo.moveOnBoard(1);
    }

    public void end(Player winner){}
}
