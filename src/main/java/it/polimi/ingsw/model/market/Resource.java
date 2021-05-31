package it.polimi.ingsw.model.market;

import java.util.Random;

public enum Resource implements Buyable{
    STONE("#8697AA"),
    SERVANT("#8A2BA2"),
    SHIELD("#1697D2"),
    COIN("#FEF17E"),
    FAITH("#D63350"),
    BLANK("#FFFFFF");

    private String hexCode;

    Resource(String hexCode){
        this.hexCode = hexCode;
    }

    public String getHexCode(){return hexCode;}

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

    public boolean isEquivalent(Buyable other){
        if(other instanceof Resource && this.equals(other)){
            return true;
        }
        return false;
    }
}
