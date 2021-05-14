package it.polimi.ingsw;

import it.polimi.ingsw.server.Server;

import java.io.IOException;


/**
 * The runnable server application.
 */
public class ServerApp {
    /**
     * The main method of the server, handles execution arguments and exceptions.
     * @param args the input arguments used when starting the server
     */
    public static void main(String[] args) {

        Server server;
        try {
            server = new Server((args.length > 0) ? Integer.parseInt(args[0]) : -1);
            server.run();
        } catch (IOException e) {
            System.err.println("Unable to initialize the server: " + e.getMessage());
        }

    }
}
