package it.polimi.ingsw.model.market;

public interface Buyable{
    /**Check if {@code this} is equivalent to {@code other} even though they're not the same object.
     * It checks if the buyable are compatible between each other and if they can be equivalent in a way specified
     * by the method.
     * <p>For reference, this means that if {@code this} is a ProductionCard, then it will check
     * that {@code other} is a ProductionCard or a ConcreteProductionCard as well and that both their
     * level and color match.</p>*/
    boolean isEquivalent(Buyable other);
}

