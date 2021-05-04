package it.polimi.ingsw.model;

public interface Observable {
    void registerObserver(ModelObserver observer);
}
