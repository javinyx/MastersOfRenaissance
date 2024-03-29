package it.polimi.ingsw.model.player;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.Observable;

public class Player implements Observable {

    protected String nickname;
    protected int currPos;
    protected ModelObserver observer;
    protected Game game;

    public Player(String nickname, Game game){
        this.nickname = nickname;
        currPos = 0;
        this.game = game;
        observer = game;
    }

    public Game getGame(){
        return game;
    }
    public String getNickname(){
        return nickname;
    }

    public int getCurrentPosition(){
        return currPos;
    }

    /**Move the player position forward.
     * <p>If the movement causes a Vatican Report or the end of the match, the Game will be notified.</p>
     * @param quantity number of cells the player gains.*/
    public void moveOnBoard(int quantity){
        if(quantity<0 || currPos==24){
            throw new IndexOutOfBoundsException("Cannot go backward neither go over 24th cell");
        }
        if(quantity==0){
            return;
        }
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


        if(report!=0){
            observer.alertVaticanReport(this, report);
        }
        if(report == 3){
            observer.updateEnd(this);
        }
        observer.updatePosition(this);
    }

    /**
     * @param report First, second o third vatican report space
     *
     * @return true if the player is in a cell that active the vatican report
     */
    public boolean isInVaticanReportRange(int report){
        switch(report){
            case 1 : return currPos>=5;
            case 2 : return currPos>=12;
            case 3 : return currPos>=19;
            default : throw new IllegalArgumentException("Report must be between 1 and 3");
        }
    }


    public void registerObserver(ModelObserver observer){
        this.observer = observer;
    }

    public ModelObserver getObserver(){
        return observer;
    }


}
