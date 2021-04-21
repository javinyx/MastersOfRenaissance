package it.polimi.ingsw.controller;

import java.util.TimerTask;

/**
 * Task for the timer contained in controller. It calls {@link Controller#turnTimeOver(int)} when its time expires.
 */
public class TurnTimerTask extends TimerTask {
    private final Controller controller;
    private final int turnId;

    TurnTimerTask(Controller controller, int turnId){
        this.controller= controller;
        this.turnId = turnId;
    }

    @Override
    public void run() {
        controller.turnTimeOver(turnId);
    }
}