package it.polimi.ingsw.messages;

import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;

/**Players inform the server of how they wish to place the resources into the different types of storage.*/
public class StoreResourcesMessage extends SimpleMessage{
    List<BiElement<Resource, Storage>> placements;

    public StoreResourcesMessage(ProPlayer player){
        super(player);
        placements = new ArrayList<>();
    }


    public void addResource(Resource resource, Storage storageType){
        placements.add(new BiElement<>(resource, storageType));
    }

    /**Returns the list of all couples [Resource, Storage].*/
    public List<BiElement<Resource, Storage>> getPlacements(){
        return placements;
    }

    /**Returns the BiElement at the specified position in the list
     * @throws IndexOutOfBoundsException if the index is out of range*/
    public BiElement<Resource, Storage> getResource(int index){
        return placements.get(index);
    }

    public BiElement<Resource, Storage> removeResource(int index){
        return placements.remove(index);
    }

    public boolean removeResource(BiElement<Resource, Storage> biElement){
        return placements.remove(biElement);
    }

    public boolean isEmpty(){
        return placements.isEmpty();
    }

}
