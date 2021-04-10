package it.polimi.ingsw.model.player;

public class PopePass {

    private boolean status;
    private int level;
    private int victoryPoints;

    public PopePass(int level) {
        status = false;
        this.level = level;
        victoryPoints = level + 1;
    }
    /**
     * @return if the card is active or not
     */
    public boolean isActive() {
        return status;
    }
    /**
     * @return the card level
     */
    public int getLevel() {
        return level;
    }

    /**Active the card if it's deactivate */
    public void activate(){
        status = true;
    }
    public void disable(){status = false;}
    /**@return victory points of the card if it is active */
    public int getVictoryPoints(){
        if(status){
            return victoryPoints;
        }
        return 0;
    }
}
