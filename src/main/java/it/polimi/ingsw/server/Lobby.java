package it.polimi.ingsw.server;

import com.google.gson.Gson;
import it.polimi.ingsw.exception.BadConfigurationException;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Lobby {
    /**
     * The number of players who will play the game
     */
    private Integer playerNum;
    /**
     * List of all the players who will play the game
     */
    private List<Player> playerList;
    /**
     * Flag that indicates if the lobby is active
     */
    private boolean isActive = false;
    /**
     * Flag that indicates if the lobby is open for new players
     */
    private boolean isOpen = true;
    /**
     * General Gson object
     */
    private final Gson gson = new Gson();
    /**
     * Object used to lock the threads
     */
    private static final Object lock = new Object();
    /**
     * Executor used for thread pools
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();
    /**
     * List of all the currently accepted socketList
     */
    private final List<Socket> socketList;
    /**
     * List of all the old accepted socketList
     */
    private final List<Socket> oldSocketList;
    /**
     * Map of nickname and linked connectionList that have been accepted in the game
     */
    private final Map<String, SocketConnection> acceptedConnection = new HashMap<>();
    /**
     * List of all the connectionList
     */
    private final List<SocketConnection> connectionList;

    /**
     * Lobby constructor, it only makes sense if the players are 2 or more
     */
    public Lobby() {
        this.playerNum = 2;
        this.playerList = new ArrayList<>();
        this.socketList = new ArrayList<>();
        this.oldSocketList = new ArrayList<>();
        this.connectionList = new ArrayList<>();
    }

    /**
     * Sends to the alive connections info about disconnections and the game ends, then invokes the method that resets the server
     *
     * @param dc             the dead connection
     * @param isLose         True if the client has lost, False otherwise
     * @param isDisconnected True if the client has disconnected, False otherwise
     */
    public synchronized void checkConnections(SocketConnection dc, boolean isDisconnected, boolean isLose) {
        Iterator<String> iterator = acceptedConnection.keySet().iterator();
        String k = null;
        for (SocketConnection cl1 : connectionList) {
            if (cl1 != dc) {
                cl1.setStopped(true);
            }
        }
        if (acceptedConnection.containsValue(dc) || acceptedConnection.size() == 0) {
            while (iterator.hasNext()) {
                k = iterator.next();
                if (acceptedConnection.get(k).equals(dc)) {
                    acceptedConnection.remove(k);
                    break;
                }
            }

            oldSocketList.clear();
            dc.deRegister();
            if (isLose)
                dc.removeFromLobby();
            if (isDisconnected) {
                socketList.remove(dc.getSocket());
                oldSocketList.remove(dc.getSocket());
                dc.closeConnection();
                connectionList.remove(dc);
                if (acceptedConnection.size() == 0) {
                    for (SocketConnection c : connectionList) {
                        if (k == null)
                            k = "Who created the game";
                        /***************************************************************************/
                        /* Something needs to be done and written instead of end the game */
                        c.sendData(gson.toJson(new MessageEnvelope(MessageID.ABORT_GAME, "end the game")));
                    }
                }
                iterator = acceptedConnection.keySet().iterator();
                while (iterator.hasNext()) {
                    /***************************************************************************/
                    /* Something needs to be done and written instead of end the game gson.toJson(new EndGameMessage(true, this.isActive)*/
                    acceptedConnection.get(iterator.next()).sendData(gson.toJson(new MessageEnvelope(MessageID.ABORT_GAME, "end the game")));
                }
            }
            endGame();
        } else if (!acceptedConnection.containsValue(dc)) {
            oldSocketList.clear();
            if (isDisconnected) {
                socketList.remove(dc.getSocket());
                oldSocketList.remove(dc.getSocket());
                dc.closeConnection();
                connectionList.remove(dc);
                dc.setFree(true);
            }
        }

    }

    /**
     * Resets the server and ends it
     */
    public synchronized void endGame() {
        List<SocketConnection> l = new ArrayList<>(acceptedConnection.values());
        if (l.size() == 0) {
            for (SocketConnection cl1 : connectionList) {
                Socket s = cl1.getSocket();
                socketList.remove(s);
                oldSocketList.add(s);
            }
        } else {
            for (SocketConnection clientSocketConnection : l) {
                clientSocketConnection.deRegister();
            }
        }
        setActive(false);
        isOpen = true;
        this.playerNum = 2;
        SocketConnection.setFree(true);
        System.out.println("Current game ended - The lobby has been reset");
        for (Socket s : oldSocketList) {
            //register(s);
        }
    }

    /**
     * Unsubscribes a connection
     *
     * @param connection the connection that will be unsubscribed
     */
    public synchronized void deRegister(SocketConnection connection) {
        acceptedConnection.keySet().removeIf(s -> acceptedConnection.get(s) == connection);
        socketList.removeIf(s -> s.equals(connection.getSocket()));
        oldSocketList.add(connection.getSocket());
    }

    /**
     * Removes the connection from the lobby
     *
     * @param connection the connection that will be removed
     */
    public synchronized void removeFromOldSocket(SocketConnection connection) {
        connectionList.remove(connection);
        oldSocketList.remove(connection.getSocket());
    }

    /**
     * Gets the status of the connection
     *
     * @return the value of isActive
     */
    public boolean isActive() {
        synchronized (lock) {
            return isActive;
        }
    }

    /**
     * Sets the status of the connection
     *
     * @param isActive the value that will be set
     */
    public void setActive(boolean isActive) {
        synchronized (lock) {
            isActive = isActive;
        }
    }

    /**
     * Manages the lobby, registers the connectionList and when there are as many connection as playerNum it starts and initializes the game
     *
     * @param envelope   the command that arrives from the client
     * @param connection the connection of the client that sent the command
     * @throws BadConfigurationException Thrown when the Nickname specified by the player is duplicated
     */
    public synchronized void game(MessageEnvelope envelope, SocketConnection connection) throws BadConfigurationException {
        if (acceptedConnection.size() == 0) {
            /***************************************************************************/
            /*
            InitMatchMessage s = gson.fromJson(envelope.getMessage(), InitMatchMessage.class);
            acceptedConnection.put(s.getNickname(), c);
            this.playerNum = s.getnPlayers();
            */
            this.isActive = true;
            if (socketList.size() > 1) {
                int i = 1;
                for (SocketConnection cl : connectionList) {
                    if (cl != connection && i < playerNum) {
                        /***************************************************************************/
                        /* ACK IS SERVERSTATE?? gson.toJson(new ServerStateMessage(true, this.isActive)*/
                        cl.sendData(gson.toJson(new MessageEnvelope(MessageID.ACK, "serverstate")));
                        i++;
                    } else if (i >= playerNum - 1 && cl != connection)
                    /***************************************************************************/
                        cl.sendData(gson.toJson(new MessageEnvelope(MessageID.ACK, "serverstate")));
                }
            }
        } /*else { ***************************************************************************
            JoinMessage b = gson.fromJson(envelope.getMessage(), JoinMessage.class);
            if(acceptedConnection.containsKey(b.getNickname())) {
                connection.sendData(gson.toJson(new MessageEnvelope(MessageID.INFO, gson.toJson(new InfoMessage(b.getNickname(), "NICKNAME")))));
                throw new BadConfigurationException("NickName already taken");
            }
            else {
                acceptedConnection.put(b.getNickname(), connection);
            }
        }*/
        if (acceptedConnection.size() == playerNum) {
            isOpen = false;
            List<SocketConnection> conn = new ArrayList<>(connectionList);
            for (SocketConnection c1 : conn) {
                if (!acceptedConnection.containsValue(c1)) {
                    /***************************************************************************/
                    c1.sendData(gson.toJson(new MessageEnvelope(MessageID.ABORT_GAME, "game ended.")));
                    socketList.remove(c1.getSocket());
                    connectionList.remove(c1);
                    c1.closeConnection();
                }
            }
            playerList = new ArrayList<>();
            List<String> names = new ArrayList<>(acceptedConnection.keySet());
            /***************************************************************************/
            /*MultiPlayerGame game = new MultiPlayerGame(){};
            try {
                model.initBoard();
                System.out.println(Server.getDateTime() + "Starting a new " + nplayer + "-players game " + (withGods ? "with" : "without") + " gods");
            } catch (BadConfigurationException e) {
                System.out.println(e.getMessage());
                endGame();
            }
            System.out.print("\tPlayers: ");
            for(int i = 0; i < nplayer; i++) {
                players.add(new Player(names.get(i), model.getBoard()));
                System.out.print(names.get(i) + " ");
            }
            model.addPlayers(players);

            MoveHandler moveHandler = new MoveHandler(model);
            BuildHandler buildHandler = new BuildHandler(model);
            PassHandler passHandler = new PassHandler(model);
            UndoHandler undoHandler = new UndoHandler(model);
            LoseHandler loseHandler = new LoseHandler(model);
            */
            List<View> viewList = new ArrayList<>();
            for (int i = 0; i < playerNum; i++) {
                /*
                viewList.add(new RemoteView(players.get(i), godInitHandler, chooseStarterHandler, workerInitHandler, moveHandler, buildHandler, passHandler, undoHandler, loseHandler, acceptedConnection.get(names.get(i))));
                model.addObserver(viewList.get(i));
                */
            }
        }
    }

    /**
     * Method that registers the socket to the lobby
     *
     * @param newSocket socket that will be registered
     */
    /*public synchronized void register(Socket newSocket) {
        try {
            SocketConnection socketConnection = null;
            if (acceptedConnection.size() < playerNum && isOpen) {

                String s;
                if (!this.isActive && socketList.size() > 0) {
                    s = gson.toJson(new ServerStateMessage(false, this.isActive));
                } else {
                    s = gson.toJson(new ServerStateMessage(true, this.isActive));
                }
                socketList.add(newSocket);
                for (SocketConnection c : connectionList) {
                    if (c.getSocket().equals(newSocket)) {
                        c.interruptPing();
                        socketConnection = new SocketConnection(newSocket, this, c.getObjectOutputStream());
                    }
                }
                if (connectionList.stream().map(x -> !x.getSocket().equals(newSocket)).reduce(true, (a, b) -> a && b))
                    socketConnection = new SocketConnection(newSocket, this);
                connectionList.removeIf(clientSocketConnection -> clientSocketConnection.getSocket().equals(newSocket));
                connectionList.add(socketConnection);
                executor.submit(socketConnection);
                socketConnection.sendData(gson.toJson(new MessageEnvelope(MessageType.SERVERSTATE, s)));
            }

            else{
                ObjectOutputStream out = new ObjectOutputStream(newSocket.getOutputStream());
                ServerStateMessage m = new ServerStateMessage(false, this.isActive);
                out.reset();
                out.writeObject(gson.toJson(new MessageEnvelope(MessageType.SERVERSTATE,gson.toJson(m))));
                out.flush();
            }

        } catch (IOException e) {
            System.out.println("Connection Error! " + e.getMessage());
    }
     */
}