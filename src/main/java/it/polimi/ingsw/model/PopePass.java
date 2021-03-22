package it.polimi.ingsw.model;

public class PopePass {

    private boolean activeState;
    private int level;

    public PopePass() {

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
}
