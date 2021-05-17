package it.polimi.ingsw.misc;

public interface Observer<T> {

    void update(T message);

    /**This update checks if the player who want to send the message ({@code playerNickname}) is the same associated with {@this}.
     * If it is, then doesn't send anything, otherwise it send {@code message}.*/
    void updateFrom(T message, String playerNickname);
}
