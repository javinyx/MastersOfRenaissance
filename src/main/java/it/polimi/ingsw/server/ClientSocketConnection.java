package it.polimi.ingsw.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.misc.Observer;
import it.polimi.ingsw.model.ModelObserver;
import it.polimi.ingsw.model.Observable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

//public class ClientSocketConnection extends Observable<String> implements ClientConnection, Runnable {
public class ClientSocketConnection implements ClientConnection, Runnable {
    private final Socket socket;
    private ObjectOutputStream output;
    private final Server server;
    private boolean active = true;

    public ClientSocketConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    private synchronized boolean isActive(){
        return active;
    }

    @Override
    public synchronized void setActive(boolean newValue){
        active = newValue;
    }

    @Override
    public void send(Object message) {
        try {
            output.reset();
            output.writeObject(message);
            output.flush();
        } catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    @Override
    public synchronized void closeConnection() {
        send("Connection closed");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error when closing socket");
        }
    }

    @Override
    public void addObserver(Observer<String> observer) {

    }

    /**
     * Calls {@link ClientConnection#closeConnection()} and deregisters the client from the server.
     */
    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        server.deregisterConnection(this);
        System.out.println("Done");
    }


    /**
     * Manages the client connections and keeps them alive.
     */
    @Override
    public void run() {
        Scanner in;
        Gson gson = new Gson();
        JsonObject outputToSend = new JsonObject();
        ClientSocketConnection c = new ClientSocketConnection(this.socket, this.server);

        try{
            in = new Scanner(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            String readName = null;
            String readNumber = null;
            boolean correctRegistration = false;

            c.sendData(gson.toJson(new MessageEnvelope(MessageID.REGISTER_MULTI, "playerName")));

            while (!correctRegistration) {
                readName = in.nextLine();
                c.sendData(gson.toJson(new MessageEnvelope(MessageID.PLAYER_NUM, "numPlayers")));

                readNumber = in.nextLine();
                while (!readNumber.equals("1") && !readNumber.equals("2") && !readNumber.equals("3") && !readNumber.equals("4")) {
                    c.sendData(gson.toJson(new MessageEnvelope(MessageID.INFO, "numPlayers ERROR")));
                    readNumber = in.nextLine();
                }

                correctRegistration = server.isNameAvailable(readName, Integer.parseInt(readNumber));

                if (!correctRegistration){
                    c.sendData(gson.toJson(new MessageEnvelope(MessageID.INFO, "nickName ERROR")));
                }
            }

            c.sendData(gson.toJson(new MessageEnvelope(MessageID.ACK, "registration CONFIRMED")));

            server.lobby(this, readName, Integer.parseInt(readNumber));

            String read;
            while(isActive()){
                read = in.nextLine();
                notify(read);
            }
            //} catch (IOException | NoSuchElementException e) {
        } catch (IOException e) {
            e.printStackTrace();
            c.sendData(gson.toJson(new MessageEnvelope(MessageID.INFO, "player SURRENDER")));
        } finally {
            close();
        }
    }

    private void notify(String read) {
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
            e.printStackTrace();
        }
    }

}
