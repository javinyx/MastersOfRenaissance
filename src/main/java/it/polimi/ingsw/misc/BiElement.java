package it.polimi.ingsw.misc;

public class BiElement<T, V> {
    private final T t;
    private final V v;

    public BiElement(T t, V v){
        this.t = t;
        this.v = v;
    }

    /**
     * @return the first value of the pair
     */
    public T getFirstValue(){
        return t;
    }

    /**
     * @return the second value of the pair
     */
    public V getSecondValue(){
        return v;
    }

    @Override
    public String toString() {
        return "[ " + t + ", " + v + " ]";
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof BiElement){
            return t.equals(((BiElement<?, ?>) obj).getFirstValue()) && v.equals(((BiElement<?, ?>) obj).getSecondValue());
        }
        return false;
    }


}
