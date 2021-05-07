package it.polimi.ingsw.client.model;

public class Player {
    private final String nickname;
    private final int turnID;
    private int currPos;
    private Market market;

    public Player(String nickname, int turnId){
        this.nickname = nickname;
        this.turnID = turnId;
    }
}
