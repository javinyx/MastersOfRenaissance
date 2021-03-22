package it.polimi.ingsw.model;

import java.util.Random;

public enum MarbleEnum {
    WHITE,
    BLUE,
    GRAY,
    YELLOW,
    VIOLET,
    RED;

    /**
     * Pick a random value of the MarbleEnum.
     * @return a random MarbleEnum.
     */
    public static MarbleEnum getRandomMarble() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }
}
