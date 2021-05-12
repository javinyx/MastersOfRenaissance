package it.polimi.ingsw.server;

import it.polimi.ingsw.misc.Observer;

public interface ClientConnection {
    /**
     * Closes the connection.
     */
    void closeConnection();

    /**
     * Adds a new observer.
     * @param observer the new observer
     */
    void addObserver(Observer<String> observer);

    /**
     * Sends the specified message to the client.
     * @param message message for the client
     */
    void send(Object message);

    /**
     * Activates or deactivates the {@link ClientConnection}.
     * @param updated boolean specifying whether the connection should be set to active ({@code true}) or inactive ({@code false}).
     */
    void setActive(boolean updated);

}
