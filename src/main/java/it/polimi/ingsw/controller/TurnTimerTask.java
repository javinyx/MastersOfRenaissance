package it.polimi.ingsw.controller;

import java.util.TimerTask;

/**
 * Task for the timer contained in controller. It calls {@link Controller#turnTimeOver(int)} when its time expires.
 */
public class TurnTimerTask extends TimerTask {
    private final Controller controller;
    private final char turnType;

    TurnTimerTask(Controller controller, char turnType){
        this.controller= controller;
        this.turnType = turnType;
    }

    @Override
    public void run() {
        controller.turnTimeOver(turnType);
    }
}