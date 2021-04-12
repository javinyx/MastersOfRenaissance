package it.polimi.ingsw.model.cards.leader;

import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;

public class StorageAbility implements LeaderCard {
        private final int victoryPoints;
        private final List<Resource> cost;
        private boolean status;
        private final Resource storageType;
        private Resource[] resources;

        public StorageAbility(int victoryPoints, List<Resource> cost, Resource storageType) {
            this.victoryPoints = victoryPoints;
            this.cost = cost;
            status = false;
            this.storageType = storageType;
            this.resources = new Resource[2];
        }

        public StorageAbility(StorageAbility dupe){
            this.victoryPoints = dupe.victoryPoints;
            this.cost = new ArrayList<>(dupe.cost);
            this.status = dupe.status;
            this.storageType = dupe.storageType;
            this.resources = new Resource[2];
            this.resources[0] = dupe.resources[0];
            this.resources[1] = dupe.resources[1];
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
        public List<Buyable> getCost() {
            return new ArrayList<>(cost);
        }

        public void applyEffect(ProPlayer player) {

            if (player.getTurnType() == 'm') {
                player.setExtraStorage(this);
            }
        }

        public Resource getStorageType(){ return storageType; }

        /**Remove the specified resource from the extra storage, if it's possible. This happens if there are actually
         * resources stored in the card and if {@code resource}'s type matches the storage type offered by the card.
         * @return true if it has remove the resource successfully, otherwise false. */
        public boolean remove(Resource resource){
            if(resources[1]!=null && resources[1].equals(resource)){
                resources[1] = null;
                return true;
            }
            if(resources[0]!=null && resources[0].equals(resource)) {
                resources[0] = null;
                return true;
            }
            return false;
        }

        public boolean add(Resource resource){
            if(!storageType.equals(resource)){
                return false;
            }
            switch(size()){
                case 0 : resources[0] = resource;
                        return true;
                case 1 : resources[1] = resource;
                        return true;
                default : return false;
            }
        }

        /**@return how many slots are actually full. In other words, how many elements are stored in the card when
         * the method is called. */
        public int size(){
            if(resources[0]!=null && resources[1]!=null)
                return 2;
            if(resources[0]!=null)
                return 1;
            return 0;
        }

}
