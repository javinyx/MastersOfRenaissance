package it.polimi.ingsw.model;

public class StorageAbility extends LeaderDecorator{

        private Resource storageType;
        private boolean fullFlag1;
        private boolean fullFlag2;

        public StorageAbility(int victoryPoints, Buyable cost, boolean fullFlag1, boolean fullFlag2, Resource storageType) {
            super(victoryPoints, cost);
            this.storageType = storageType;
        }

        public Resource getStorageType(){ return storageType; }

        public boolean isFull(){return true;}

        public void applyEffect(){}

}
