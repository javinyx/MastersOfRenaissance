package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.misc.Observable;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class ClientSocketConnection extends Observable<String> implements ClientConnection, Runnable {
    private final Socket socket;
    private ObjectOutputStream output;
    private final Server server;
    private String readName;
    private int gameSize;

    private boolean active = true;
    /**Indicates if the player has responded to server's PINGs. If yes, then the client-socket connection is
     * still viable.*/
    private Boolean stillConnected = false;

    public ClientSocketConnection(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    private synchronized boolean isActive(){
        return active;
    }

    @Override
    public synchronized void setActive(boolean isActive){
        active = isActive;
    }

    @Override
    public void setStillConnected(boolean isStillConnected) {
        synchronized (stillConnected) {
            stillConnected = isStillConnected;
        }
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

        try{
            in = new Scanner(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            //String readName = null;
            String readNumber = null;
            boolean correctRegistration = false;

            send(gson.toJson(new MessageEnvelope(MessageID.ASK_NICK, "playerName")));

            while (!correctRegistration) {
                readName = in.nextLine();
                System.out.println(readName);
                send(gson.toJson(new MessageEnvelope(MessageID.PLAYER_NUM, "numPlayers")));

                readNumber = in.nextLine();
                System.out.println(readNumber);
                while (!readNumber.equals("1") && !readNumber.equals("2") && !readNumber.equals("3") && !readNumber.equals("4")) {
                    send(gson.toJson(new MessageEnvelope(MessageID.TOO_MANY_PLAYERS, "numPlayers ERROR")));
                    readNumber = in.nextLine();
                }
                gameSize = Integer.parseInt(readNumber);

                correctRegistration = server.isNameAvailable(readName, Integer.parseInt(readNumber));

                if (!correctRegistration){
                    send(gson.toJson(new MessageEnvelope(MessageID.NICK_ERR, "nickName ERROR")));
                }
            }

            send(gson.toJson(new MessageEnvelope(MessageID.CONFIRM_REGISTRATION, readName)));

            server.lobby(this, readName, Integer.parseInt(readNumber));

            String read;
            while(active){
                read = in.nextLine();
                notify(read);
            }

        } catch (IOException e) {
            e.printStackTrace();
            send(gson.toJson(new MessageEnvelope(MessageID.INFO, "player SURRENDER")));
        } finally {
            /*if(active) {
                System.err.println("PLAYER CRASHED");
                server.playerCrashed(this, readName, gameSize);
            }*/
            close();
        }
    }

    @Override
    public Thread getPingPongSystem(){
        Gson gson = new Gson();
        stillConnected = true;
        Thread pingPong = new Thread(() -> {
            while(!socket.isClosed() && stillConnected){
                System.out.println("PING");
                //MessageEnvelope pingEnvelope = new MessageEnvelope(MessageID.PING, "");
                //send(gson.toJson(pingEnvelope, MessageEnvelope.class));
                synchronized (stillConnected) {
                    stillConnected = false;
                }
                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //if we're here, then the socket has been closed because hasn't received any PONG back
            //inform model and close socket
            System.err.println("Ping not matched");
            //server.playerCrashed(this, readName, gameSize);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return pingPong;
    }
}
