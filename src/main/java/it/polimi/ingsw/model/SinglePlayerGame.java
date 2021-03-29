package it.polimi.ingsw.model;

import org.jetbrains.annotations.NotNull;

public class SinglePlayerGame extends Game implements Observer{
    private ProPlayer player;
    private Player lorenzo;
    private Deck tokenDeck;

    public SinglePlayerGame() {
        market = new Market();
        player = null;
        lorenzo = new Player("Lorenzo", this);
        winner = null;
    }

    public void start(){}
    public void createPlayer(@NotNull String nickname){
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
