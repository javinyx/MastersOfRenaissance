package it.polimi.ingsw.server;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SocketConnection {
    /**
     * Client socket
     */
    private final Socket clientSocket;
    /**
     * Server lobby
     */
    private final Lobby lobby;
    /**
     * Input scanner
     */
    private Scanner input;
    /**
     * Output stream
     */
    private ObjectOutputStream output;
    /**
     * Flag that indicates if the connection is active
     */
    private boolean isActive = false;
    /**
     * Flag that indicates if the connection is not alive
     */
    private boolean isDead = false;
    /**
     * Flag that indicates if, among all the connections, no one has started the de-registration process
     */
    private static boolean free;
    /**
     * General Gson object
     */
    private final Gson gson = new Gson();
    /**
     * Thread that manages the ping connection
     */
    private Thread pt;
    /**
     * Flag that indicates if the socket has been stopped
     */
    private boolean isStopped = false;

    /**
     * Constructor of the socket connection between server and client
     * @param clientSocket the client socket
     * @param lobby  the server lobby
     */
    public SocketConnection(Socket clientSocket, Lobby lobby) {
        this.clientSocket = clientSocket;
        this.lobby = lobby;
        if (!free)
            this.free = true;
        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            System.out.println("Creating new socket connection from " + clientSocket.getRemoteSocketAddress().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the output stream
     * @return the value of output
     */
    public ObjectOutputStream getOutputStream() {
        return output;
    }

    /**
     * Sets the connection as dead or alive
     * @param isDead the value that will be set
     */
    public synchronized void setDead(boolean isDead) {
        this.isDead = isDead;
    }

    /**
     * Sets the static flag free
     * @param t the value that will be set
     */
    public static synchronized void setFree(boolean t) {
        free = t;
    }

    /**
     * Closes the socket connection
     */
    public synchronized void closeConnection() {
        try {
            clientSocket.close();
            System.out.println("Connection with " + clientSocket.getRemoteSocketAddress().toString() + " closed");
        } catch (IOException e) {
            System.out.println("Error when closing socket! " + e.getMessage());
        }
        isActive = false;
    }

    /**
     * Sets the socket as stopped
     * @param isStopped the value that will be set
     */
    public void setStopped(boolean isStopped) {
        this.isStopped = isStopped;
    }

    /**
     * De-registers the connection
     */
    public synchronized void deRegister() {
        if (isActive) {
            lobby.deRegister(this);
            System.out.println("Connection from " + clientSocket.getRemoteSocketAddress().toString() + " unregistered");
        }
    }

    /**
     * Invokes the server method that manages the disconnection of the client of this connection
     * @param isDisconnected True if the client is disconnected, False otherwise
     * @param isLose True if the client has lost, False otherwise
     */
    public synchronized void checkClients(boolean isDisconnected, boolean isLose) {
        lobby.checkConnections(this, isDisconnected, isLose);
    }

    /**
     * Sends an object trough the socket
     * @param o the object that will be sent
     */
    public synchronized void sendData(Object o) {
        try {
            output.reset();
            output.writeObject(o);
            output.flush();
        } catch (IOException e) {
            // Does nothing in case of exception
        }
    }

    /**
     * Method that interrupts the thread that manages the ping
     */
    public void interruptPing() {
        if(pt != null)
            pt.interrupt();
    }

    /**
     * Method that creates a thread for async ping
     *
     * @return the created thread
     */
    public Thread asyncPing() {
        Gson gson = new Gson();
        /***************************************************************************/
        /*
        PingMessage pingMessage = new PingMessage("ping");
        return new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    sendData(gson.toJson(new MessageEnvelope(MessageType.PING, gson.toJson(pingMessage, PingMessage.class)), MessageEnvelope.class));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                // Does nothing in case of exception
            }
        });*/
        return null;
    }

    /**
     * Gets the socket
     * @return return the value of the socket
     */
    public synchronized Socket getSocket() {
        return clientSocket;
    }

    /**
     * Removes the connection from the lobby
     */
    public synchronized void removeFromLobby() {
        System.out.println("Connection from " + clientSocket.getRemoteSocketAddress().toString() + " removed from the lobby");
        lobby.removeFromOldSocket(this);
    }

    /**
     * This method registers the connection to the lobby and sends the messages from the socket to the view
     */
    public void run() {
        /*try {
            if (!stopped) {
                in = new Scanner(socket.getInputStream());
                InputStreamReader inn = new InputStreamReader(socket.getInputStream());
                t1 = asyncPing();
                t1.start();
                while (!isActive && !stopped) {
                    Timer t = new Timer();
                    t.schedule(new TimerTimeout(this), 5000);
                    while (!dead && !inn.ready()) {
                        // Waits for a change of state
                    }
                    if (dead && free && !isActive && !stopped) {
                        free = false;
                        System.out.println(Server.getDateTime() + "Connection from " + socket.getRemoteSocketAddress().toString() + " lost");
                        checkClients(true, false);
                    }
                    String input = in.nextLine();
                    MessageEnvelope envelope = gson.fromJson(input, MessageEnvelope.class);
                    if (envelope.getType().equals(MessageType.REGISTER)) {
                        try {
                            lobby.game(envelope, this);
                            isActive = true;
                        } catch (BadConfigurationException e) {
                            // Thrown only if a client tries to use an already taken nickname; ignored
                        }
                    }
                    t.cancel();
                    t.purge();
                }
                while (isActive && lobby.isActive()) {
                    Timer t = new Timer();
                    t.schedule(new TimerTimeout(this), 5000);
                    while (!dead && !inn.ready()) {
                    }
                    if (dead && free && isActive && !stopped) {
                        free = false;
                        System.out.println(Server.getDateTime() + "Connection from " + socket.getRemoteSocketAddress().toString() + " lost");
                        checkClients(true, false);
                    }
                    String message = in.nextLine();
                    MessageEnvelope envelope = gson.fromJson(message, MessageEnvelope.class);
                    if (envelope.getType().equals(MessageType.PING)) {
                    } else if (lobby.isActive()) {
                        notify(envelope);
                    }
                    t.cancel();
                    t.purge();
                }
            }
        } catch (IOException e) {
            // Does nothing in case of exception

        }
     */
    }
}
