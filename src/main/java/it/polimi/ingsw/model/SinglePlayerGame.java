package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PopePass;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class SinglePlayerGame extends Game implements ModelObserver {
    protected ProPlayer player;
    private Player lorenzo;
    private Deck tokenDeck;
    private Player winner;


    public SinglePlayerGame(Controller controller) {
        super(new Market());
        player = null;
        lorenzo = new Player("Lorenzo", this);
        winner = null;

        tokenDeck = new Deck("ActionToken");
        leaderDeck = new Deck("LeaderCard");
        productionDecks = Deck.createProdDeckList();
        this.controller = controller;
    }

    /**
     * Start the game and give the leader cards to players
     */
    public void start(ProPlayer p){
        UpdateMessage msg = new UpdateMessage(currPlayer.getTurnID(), currPlayer.getCurrentPosition(), 1,
                getMarket().getMarketBoard(), getMarket().getExtraMarble(), getBuyableProductionID(),
                null, null, null, null);

        getCurrPlayer().setUpdate(msg);
    }

    /**
     * Create the player
     * @param nickname the nickname of the player
     */

    public boolean createPlayer(String nickname){
        if(totalPlayers>0){
            //too many players
            return false;
        }
        if(nickname == null){
            throw new NullPointerException();
        }
        if(nickname.equals(lorenzo.getNickname())){
            throw new IllegalArgumentException("Player cannot have "+lorenzo.getNickname()+ " as nickname");
        }
        totalPlayers = 1;
        player = new ProPlayer(nickname, 1, this);
        currPlayer = player;
        return true;
    }

    public ProPlayer getPlayerFromNickname(String nickname){
        if(player.getNickname().equals(nickname))
            return player;
        return null;
    }

    public Market getMarket(){
        return market;
    }

    public int getLorenzoPosition(){return lorenzo.getCurrentPosition();}

    public Player getWinner(){
        return winner;
    }

    /**Check who is the winner between Lorenzo and the player.
     * <p>If Lorenzo got to the end of the board first or </p> */
    public void updateEnd(Player player){
        //lorenzo got to the end of the board before the player
        if(player.equals(lorenzo)){
            winner = lorenzo;
        }
        this.player.getVictoryPoints();
        winner = this.player;
        //end(winner);
        controller.initEndPhase(winner.getNickname().equals("lorenzo") ? 0 : 1);
    }

    public Player countFinalPointsAndWinner(){
        return winner;
    }

    public void updatePosition(Player player){
        controller.update(MessageID.PLAYERS_POSITION);
    }

    public void updateEndTurn(ProPlayer player){} //do nothing?

    /**Alert the observer that {@code player} has triggered the {@code vaticanReport}.
     * @param vaticanReport must be between 1 and 3. */
    public void alertVaticanReport(Player player, int vaticanReport){
        if(reportTriggered < vaticanReport) {
            triggerPlayerReport = player;
            reportTriggered = vaticanReport;
            List<PopePass> passes = this.player.getPopePasses();
            if (player.equals(lorenzo)) {
                if (!this.player.isInVaticanReportRange(vaticanReport)) {
                    passes.get(vaticanReport - 1).disable();
                    controller.update(MessageID.VATICAN_REPORT);
                    return;
                }
            }
            passes.get(vaticanReport - 1).activate();

            controller.update(MessageID.VATICAN_REPORT);
        }
    }

    public List<ProPlayer> getPlayers(){
        List<ProPlayer> players = new ArrayList<>();
        players.add(player);
        return players;
    }
//---------------------------------------------------------------------------------------------------------
    public Deck getTokenDeck() {
        return tokenDeck;
    }

    public List<Integer> getTokenDeckByID() {
        return new ArrayList<>().stream().map(x -> ((ActionToken)x).getId())
                .collect(Collectors.toList());
    }


    /**
     * Create a new shuffled token deck
     */
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
        controller.update(MessageID.PLAYERS_POSITION);
    }

    public void alertDiscardResource(Player player, int quantity){
        lorenzo.moveOnBoard(quantity);
        controller.update(MessageID.PLAYERS_POSITION);
    }

    public void end(Player winner){}

    public ActionToken drawActionToken(){
        ActionToken act;

        act = ((ActionToken)tokenDeck.getFirst());
        act.draw(lorenzo, this);
        return act;
    }

}
