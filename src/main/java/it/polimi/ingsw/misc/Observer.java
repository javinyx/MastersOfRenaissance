package it.polimi.ingsw.misc;

public interface Observer<T> {

    void update(T message);
}
