package it.polimi.ingsw.model;

public class SinglePlayerGame implements Game, Observer{
    private Market market;
    private ProPlayer player;
    private Player lorenzo;
    private Player winner;

    public SinglePlayerGame() {
        market = new Market();
        player = null;
        lorenzo = new Player("Lorenzo", this);
        winner = null;
    }

    public void start(){}
    public void createPlayer(String nickname){
        player = new ProPlayer(nickname, 1, this);
    }
    public Market getMarket(){
        return market;
    }

    public void updateEnd(Player player){}
    public void updatePosition(Player player){}
    public void alertVaticanReport(Player player, int vaticanReport){}
}
