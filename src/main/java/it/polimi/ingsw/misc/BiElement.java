package it.polimi.ingsw.misc;

public class BiElement<T, V> {
    private final T t;
    private final V v;

    public BiElement(T t, V v){
        this.t = t;
        this.v = v;
    }

    public T getT(){
        return t;
    }

    public V getV(){
        return v;
    }
}
