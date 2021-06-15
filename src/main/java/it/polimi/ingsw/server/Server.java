package it.polimi.ingsw.server;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.view.RemoteView;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int PORT = 27001;
    private final ServerSocket serverSocket;
    private ExecutorService executor = Executors.newFixedThreadPool(128);

    private Map<String, ClientConnection> singlePlayerWait = new HashMap<>();
    private Map<String, ClientConnection> twoPlayerWait = new HashMap<>();
    private Map<String, ClientConnection> threePlayerWait = new HashMap<>();
    private Map<String, ClientConnection> fourPlayerWait = new HashMap<>();

    private ClientConnection singlePlayerPlay;
    private Map<ClientConnection, ClientConnection> twoPlayerPlay = new HashMap<>();
    private Map<ClientConnection, List<ClientConnection>> threePlayerPlay = new HashMap<>();
    private Map<ClientConnection, List<ClientConnection>> fourPlayerPlay = new HashMap<>();

    private Map<String, SinglePlayerGame> singlePlayerRejoiningRoom = new HashMap<>(1);
    private Map<String, MultiPlayerGame> twoPlayerRejoiningRoom = new HashMap<>();
    private Map<String, MultiPlayerGame> threePlayerRejoiningRoom = new HashMap<>();
    private Map<String, MultiPlayerGame> fourPlayerRejoiningRoom = new HashMap<>();

    private Controller controller;

    /**
     * Deregisters a connection from any type of game.
     * @param c the connection that will be removed
     */
    public synchronized void deregisterConnection(ClientConnection c) {
        if (singlePlayerPlay != null && singlePlayerPlay.equals(c))
            deregFromSinglePlayerGame(c);
        else if (twoPlayerPlay.containsKey(c))
            deregFromTwoPlayerGame(c);
        else if (threePlayerPlay.containsKey(c))
            deregFromThreePlayerGame(c);
        else if (fourPlayerPlay.containsKey(c))
            deregFromFourPlayerGame(c);

        Iterator<String> iterator;

        iterator = singlePlayerWait.keySet().iterator();
        while(iterator.hasNext()) {
            if (singlePlayerWait.get(iterator.next()) == c)
                iterator.remove();
        }
        iterator = twoPlayerWait.keySet().iterator();
        while(iterator.hasNext()) {
            if (twoPlayerWait.get(iterator.next()) == c)
                iterator.remove();
        }
        iterator = threePlayerWait.keySet().iterator();
        while(iterator.hasNext()) {
            if (threePlayerWait.get(iterator.next()) == c)
                iterator.remove();
        }
        iterator = fourPlayerWait.keySet().iterator();
        while(iterator.hasNext()) {
            if (fourPlayerWait.get(iterator.next()) == c)
                iterator.remove();
        }
    }

    /**
     * Deregisters a connection from a single player game.
     * @param c the connection that will be removed
     */
    private void deregFromSinglePlayerGame(ClientConnection c) {
        singlePlayerPlay = null;
    }

    /**
     * Deregisters a connection from a two player game.
     * @param c the connection that will be removed
     */
    private void deregFromTwoPlayerGame(ClientConnection c) {
        ClientConnection opponent = twoPlayerPlay.get(c);
        if(opponent != null) {
            opponent.closeConnection();
        }
        twoPlayerPlay.remove(c);
        twoPlayerPlay.remove(opponent);
    }

    /**
     * Deregisters a connection from a three player game.
     * @param c the connection that will be removed
     * */
    private void deregFromThreePlayerGame(ClientConnection c) {
        List<ClientConnection> opponents = threePlayerPlay.get(c);
        if (opponents != null){
            switch(opponents.size()){
                case 1 -> {
                    opponents.get(0).closeConnection();
                    threePlayerPlay.remove(c);
                    threePlayerPlay.remove(opponents.get(0));
                }
                case 2 -> {
                    threePlayerPlay.get(opponents.get(0)).remove(c);
                    threePlayerPlay.get(opponents.get(1)).remove(c);
                    threePlayerPlay.remove(c);
                }
            }
        }
    }

    /**
     * Deregisters a connection from a four player game.
     * @param c the connection that will be removed
     * */
    private void deregFromFourPlayerGame(ClientConnection c) {
        List<ClientConnection> opponents = fourPlayerPlay.get(c);
        if (opponents != null){
            switch(opponents.size()){
                case 1 -> {
                    opponents.get(0).closeConnection();
                    fourPlayerPlay.remove(c);
                    fourPlayerPlay.remove(opponents.get(0));
                }
                case 2 -> {
                    fourPlayerPlay.get(opponents.get(0)).remove(c);
                    fourPlayerPlay.get(opponents.get(1)).remove(c);
                    fourPlayerPlay.remove(c);
                }
                case 3 -> {
                    fourPlayerPlay.get(opponents.get(0)).remove(c);
                    fourPlayerPlay.get(opponents.get(1)).remove(c);
                    fourPlayerPlay.get(opponents.get(2)).remove(c);
                    fourPlayerPlay.remove(c);
                }
            }
        }
    }

    /**
     * Adds players to a single, two, three or four player game.
     * @param c the connection that will be added
     * @param name the player's nickname
     * @param playerNum the number of players who will play the game
     */
    public synchronized void lobby(ClientConnection c, String name, int playerNum) {
        try {
            switch(playerNum) {
                case 1 -> {
                    if(singlePlayerRejoiningRoom.containsKey(name)){
                        SinglePlayerGame rejoinGame = singlePlayerRejoiningRoom.get(name);
                        controller.rejoin(rejoinGame, name);
                        singlePlayerRejoiningRoom.remove(name);
                        break;
                    }
                    singlePlayerWait.put(name, c);
                    createSinglePlayerGame();
                }
                case 2 -> {
                    if(twoPlayerRejoiningRoom.containsKey(name)){
                        MultiPlayerGame rejoinGame = twoPlayerRejoiningRoom.get(name);
                        controller.rejoin(rejoinGame, name);
                        twoPlayerRejoiningRoom.remove(name);
                        break;
                    }
                    twoPlayerWait.put(name, c);
                    if (twoPlayerWait.size() == 2)
                        createTwoPlayerGame();
                }
                case 3 -> {
                    if(threePlayerRejoiningRoom.containsKey(name)){
                        MultiPlayerGame rejoinGame = threePlayerRejoiningRoom.get(name);
                        controller.rejoin(rejoinGame, name);
                        threePlayerRejoiningRoom.remove(name);
                        break;
                    }
                    threePlayerWait.put(name, c);
                    if (threePlayerWait.size() == 3)
                        createThreePlayerGame();
                }
                case 4 -> {
                    if(fourPlayerRejoiningRoom.containsKey(name)){
                        MultiPlayerGame rejoinGame = fourPlayerRejoiningRoom.get(name);
                        controller.rejoin(rejoinGame, name);
                        fourPlayerRejoiningRoom.remove(name);
                        break;
                    }
                    fourPlayerWait.put(name, c);
                    if (fourPlayerWait.size() == 4)
                        createFourPlayerGame();
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a single player game.
     */
    public void createSinglePlayerGame() {
        List<String> keys = new ArrayList<>(singlePlayerWait.keySet());
        ClientConnection c1 = singlePlayerWait.get(keys.get(0));
        String p1 = keys.get(0);
        singlePlayerPlay = c1;

        createGame(new ArrayList<>(Arrays.asList(c1)), new ArrayList<>(Arrays.asList(p1)));

        singlePlayerWait.clear();
    }

    /**
     * Connects the two players and creates the game.
     */
    public void createTwoPlayerGame() {
        List<String> keys = new ArrayList<>(twoPlayerWait.keySet());
        ClientConnection c1 = twoPlayerWait.get(keys.get(0));
        ClientConnection c2 = twoPlayerWait.get(keys.get(1));
        String p1 = keys.get(0);
        String p2 = keys.get(1);

        twoPlayerPlay.put(c1, c2);
        twoPlayerPlay.put(c2, c1);

        createGame(new ArrayList<>(Arrays.asList(c1, c2)), new ArrayList<>(Arrays.asList(p1, p2)));

        twoPlayerWait.clear();
    }

    /**
     * Connects the three players and creates the game.
     */
    public void createThreePlayerGame() {
        List<String> keys = new ArrayList<>(threePlayerWait.keySet());
        ClientConnection c1 = threePlayerWait.get(keys.get(0));
        ClientConnection c2 = threePlayerWait.get(keys.get(1));
        ClientConnection c3 = threePlayerWait.get(keys.get(2));
        String p1 = keys.get(0);
        String p2 = keys.get(1);
        String p3 = keys.get(2);

        threePlayerPlay.put(c1, new ArrayList<>(Arrays.asList(c2, c3)));
        threePlayerPlay.put(c2, new ArrayList<>(Arrays.asList(c1, c3)));
        threePlayerPlay.put(c3, new ArrayList<>(Arrays.asList(c1, c2)));

        createGame(new ArrayList<>(Arrays.asList(c1, c2, c3)), new ArrayList<>(Arrays.asList(p1, p2, p3)));

        threePlayerWait.clear();
    }

    /**
     * Connects the four players and creates the game.
     */
    public void createFourPlayerGame() {
        List<String> keys = new ArrayList<>(fourPlayerWait.keySet());
        ClientConnection c1 = fourPlayerWait.get(keys.get(0));
        ClientConnection c2 = fourPlayerWait.get(keys.get(1));
        ClientConnection c3 = fourPlayerWait.get(keys.get(2));
        ClientConnection c4 = fourPlayerWait.get(keys.get(3));
        String p1 = keys.get(0);
        String p2 = keys.get(1);
        String p3 = keys.get(2);
        String p4 = keys.get(3);

        fourPlayerPlay.put(c1, new ArrayList<>(Arrays.asList(c2, c3, c4)));
        fourPlayerPlay.put(c2, new ArrayList<>(Arrays.asList(c1, c3, c4)));
        fourPlayerPlay.put(c3, new ArrayList<>(Arrays.asList(c1, c2, c4)));
        fourPlayerPlay.put(c4, new ArrayList<>(Arrays.asList(c1, c2, c3)));

        createGame(new ArrayList<>(Arrays.asList(c1, c2, c3, c4)), new ArrayList<>(Arrays.asList(p1, p2, p3, p4)));

        fourPlayerWait.clear();
    }

    /**
     * Creates a new game from the specified players.
     * @param connectionList the list containing the connections with the clients
     * @param playerNames the list containing the nicknames of all the players
     */
    public void createGame(List<ClientConnection> connectionList, List<String> playerNames) {
        controller = new Controller();
        for (int i = 0; i < connectionList.size(); i++){
            View v = new RemoteView(playerNames.get(i), playerNames, connectionList.get(i), controller);
            controller.registerObserver(v);
        }

        if(playerNames.size() > 1)
            controller.createMultiplayerGame(playerNames);
        else
            controller.createSinglePlayerGame(playerNames.get(0));

        /*for(ClientConnection cc : connectionList){
            Thread pingPong = cc.getPingPongSystem();
            pingPong.start();
        }*/
    }


    /**
     * Checks if the player's nickname is available.
     * @param name the player's nickname to be checked
     * @param playerNum the number of players in the game
     * @return boolean true if the name is available, false otherwise
     */
    public synchronized boolean isNameAvailable(String name, int playerNum) {
        if(name.equals("")){
            return false; //we don't want an empty string as a valid name
        }
        try {
            return switch (playerNum) {
                case 1 -> true;
                case 2 -> (!twoPlayerWait.containsKey(name));
                case 3 -> (!threePlayerWait.containsKey(name));
                case 4 -> (!fourPlayerWait.containsKey(name));
                default -> throw new IllegalArgumentException();
            };
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Creates a {@link ServerSocket} and binds it to a specific port number.
     * @throws IOException if the socket's creation or binding fails
     * @param port the port on which the server will run
     */
    public Server(int port) throws IOException {
        if (port != -1) PORT = port;
        this.serverSocket = new ServerSocket(PORT);
    }

    /**
     * Runs the server, starts to listen for connections from the client and then creates the client socket.
     */
    public void run() {
        System.out.println("Server is ready.");
        while(true) {
            try {
                Socket newSocket = serverSocket.accept();
                newSocket.setKeepAlive(true);
                ClientSocketConnection socketConnection = new ClientSocketConnection(newSocket, this);
                executor.submit(socketConnection);
            } catch (IOException e) {
                System.out.println("Connection Error");
            }
        }
    }

    /**If the player crashes, then this method will move him/her in a server's Rejoin Room and
     * communicate to the actual game that this player is not active anymore.
     * @param connection player's connection (see {@link ClientConnection})
     * @param name player's nickname
     * @param gameSize game size in which the player was participating (1, 2, 3, 4)
     * @return true if it can set the player as inactive, false otherwise.*/
    public boolean playerCrashed(ClientConnection connection, String name, int gameSize){
        switch (gameSize) {
            case 1 -> {if(!singlePlayerPlay.equals(connection)) {
                return false;
            }else{
                singlePlayerRejoiningRoom.put(name, (SinglePlayerGame) controller.getGame());
            }}
            case 2 -> {if(!twoPlayerPlay.containsKey(connection)) {
                return false;
            }else{
                twoPlayerRejoiningRoom.put(name, (MultiPlayerGame) controller.getGame());
            }}
            case 3 -> { if(!threePlayerPlay.containsKey(connection)) {
                return false;
            }else{
                threePlayerRejoiningRoom.put(name, (MultiPlayerGame) controller.getGame());
            }}
            case 4 -> {if(!fourPlayerPlay.containsKey(connection)) {
                return false;
            }else{
                fourPlayerRejoiningRoom.put(name, (MultiPlayerGame) controller.getGame());
            }}

            default -> {return false;}
        }
        controller.setInactivePlayer(name);
        return true;
    }

}
