package it.polimi.ingsw.misc;

public class TriElement<T, V, E> {
    private T t;
    private V v;
    private E e;

    public TriElement(T t, V v, E e){
        this.t = t;
        this.v = v;
        this.e = e;
    }

    public T getFirstValue(){return t;}
    public V getSecondValue(){return v;}
    public E getThirdValue(){return e;}

    public void setFirstValue(T t){this.t = t;}
    public void setSecondValue(V v){this.v = v;}
    public void setThirdValue(E e){this.e = e;}
}
