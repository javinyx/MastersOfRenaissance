package it.polimi.ingsw.model;

public class StorageAbility implements LeaderCard{
        private final int victoryPoints;
        private final Buyable cost;
        private boolean status;
        private Resource storageType;
        private boolean fullFlag1;
        private boolean fullFlag2;

        public StorageAbility(int victoryPoints, Buyable cost, Resource storageType) {
            this.victoryPoints = victoryPoints;
            this.cost = cost;
            status = false;
            fullFlag1 = false;
            fullFlag2 = false;
            this.storageType = storageType;
        }

        public Resource getStorageType(){ return storageType; }

        public boolean isFull(){return true;}

    @Override
    public boolean isActive() {
        return status;
    }

    @Override
    public void setStatus(boolean activate) {
        status = activate;
    }

    @Override
    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public Buyable getCost() {
        return cost;
    }

    public void applyEffect(ProPlayer player){}

}
