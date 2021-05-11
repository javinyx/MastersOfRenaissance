package it.polimi.ingsw.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The type Server.
 */
public class Server {

    private final ServerSocket serverSocket;
    private final Lobby lobby;

    /**
     * Instantiates a new Server.
     *
     * @param port the port
     * @throws IOException the io exception
     */
    public Server(Integer port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.lobby = new Lobby();
    }


    /**
     * Run.
     */
    public void run() {
        System.out.println("Server started!");
        while(true){
            try {
                Socket newSocket = serverSocket.accept();
                /***************************************************************************/
                //lobby.register(newSocket);
            } catch (IOException e) {
                System.out.println("Connection Error: "+ e.getMessage());
            }
        }
    }
}
