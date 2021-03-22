package it.polimi.ingsw.model;

public class PopePass {

    private boolean activeState;
    private int level;
    private int victoryPoints;

    public PopePass(int level) {
        activeState = false;
        this.level = level;
        victoryPoints = level + 1;
    }
    /**
     * @return if the card is active or not
     */
    public boolean isActiveState() {
        return activeState;
    }
    /**
     * @return the card level
     */
    public int getLevel() {
        return level;
    }
    public void activate(){
        activeState = true;
        this.getVictoryPoints();
    }
    /**@returns victory points of the card if it is active */
    public int getVictoryPoints(){
        if(activeState){
            return victoryPoints;
        }
        return 0;
    }
}
