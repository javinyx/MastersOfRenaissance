package it.polimi.ingsw.model;

import java.util.List;

public class StorageAbility implements LeaderCard {
        private final int victoryPoints;
        private final List<Resource> cost;
        private boolean status;
        private Resource storageType;
        private boolean fullFlag1;
        private boolean fullFlag2;

        public StorageAbility(int victoryPoints, List<Resource> cost, Resource storageType) {
            this.victoryPoints = victoryPoints;
            this.cost = cost;
            status = false;
            fullFlag1 = false;
            fullFlag2 = false;
            this.storageType = storageType;
        }

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
            return (Buyable) cost;
        }

        public void applyEffect(ProPlayer player) {

            if (player.getTurnType() == 'm') {
                player.setExtraStorage(this);
            }
        }


        public boolean isFullFlag1() {
            return fullFlag1;
        }

        public void setFullFlag1(boolean fullFlag1) {
            this.fullFlag1 = fullFlag1;
        }

        public boolean isFullFlag2() {
            return fullFlag2;
        }

        public void setFullFlag2(boolean fullFlag2) {
            this.fullFlag2 = fullFlag2;
        }

        public Resource getStorageType(){ return storageType; }

        public boolean isFull(){ return true; }

}
