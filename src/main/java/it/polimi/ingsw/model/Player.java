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

    /**Move the player position forward.
     * <p>If the movement causes a Vatican Report or the end of the match, the Game will be notified.</p>
     * @param quantity number of cells the player gains*/
    public void moveOnBoard(int quantity){
        int newPos = currPos + quantity;
        int report = 0;
        if(currPos < 8 && newPos >= 8){
            report = 1;
        }else if(currPos < 16 && newPos >= 16){
            report = 2;
        }else if(currPos < 24 && newPos >= 24){
            report = 3;
        }
        currPos = (report==3) ? 24 : newPos;

        observer.updatePosition(this);
        if(report!=0){
            observer.alertVaticanReport(this, report);
        }
        if(report == 3){
            observer.updateEnd(this);
        }
    }


    public void registerObserver(Observer observer){
        this.observer = observer;
    }


}
