package it.polimi.ingsw.client.model;

public class NubPlayer {
    private final String nickname;
    private final int turnID;
    private int currPos;
    private Market market;
    private boolean isMyTurn = false;

    public NubPlayer(String nickname, int turnId){
        this.nickname = nickname;
        this.turnID = turnId;
    }

    public boolean setPosition(int pos){
        if(pos>0 && pos<25) {
            currPos = pos;
            return true;
        }
        return false;
    }

    public Market getMarket(){return market;}
}
