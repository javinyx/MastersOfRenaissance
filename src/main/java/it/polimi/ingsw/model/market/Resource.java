package it.polimi.ingsw.model.market;

import java.util.Random;

public enum Resource implements Buyable{
    STONE,
    SERVANT,
    SHIELD,
    COIN,
    FAITH,
    BLANK;

    /**
     * Pick a random value of the Resource.
     * @return a random Resource.
     */
    public static Resource getRandomResource() {
        Random random = new Random();
        return values()[random.nextInt(values().length)];
    }

    /**@return true if the resource is not a FAITH or BLANK, since these cannot be used as concurrency or
     * in trades. */
    public boolean isValidForTrading(){
        if(this.equals(Resource.FAITH) || this.equals(Resource.BLANK)){
            return false;
        }
        return true;
    }
}
