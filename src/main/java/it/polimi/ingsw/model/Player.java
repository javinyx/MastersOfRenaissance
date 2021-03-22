package it.polimi.ingsw.model;

public class Player implements Observable{

    protected String nickname;
    protected int currPos;
    protected Observer observer;
    protected Game game;

    public Player(String nickname, Game game){
        this.nickname = nickname;
        currPos = 0;
        this.game = game;
        observer = null;
    }


    public String getNickname(){
        return nickname;
    }

    public int getCurrentPosition(){
        return currPos;
    }


    public void registerObserver(Observer observer){
        this.observer = observer;
    }


}
