package it.polimi.ingsw.misc;

import java.util.ArrayList;
import java.util.List;

public class Observable<T> {
    private List<Observer<T>> obs = new ArrayList<>();

    public synchronized void registerObserver(Observer<T> observer){
        this.obs.add(observer);
    }

    public synchronized void removeObserver(Observer<T> observer){
        this.obs.remove(observer);
    }

    public synchronized void notify(T note){
        for(Observer<T> o : obs){
            o.update(note);
        }
    }
}
