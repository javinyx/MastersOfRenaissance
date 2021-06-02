package it.polimi.ingsw.client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI.sceneHandlers.GamePhaseHandler;
import it.polimi.ingsw.client.GUI.sceneHandlers.InitialPhaseHandler;
import it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum;
import it.polimi.ingsw.client.MessageReceiver;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.PlayersPositionMessage;
import it.polimi.ingsw.messages.concreteMessages.StoreResourcesMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Resource;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GuiController extends ClientController {
    protected final Gui gui;
    private final Stage stage;
    private final InitialPhaseHandler initialPhaseHandler;
    private GamePhaseHandler gamePhaseHandler;
    private final Object lock = new Object();
    List<Integer> chosenLeadersId = new ArrayList<>();

    private String nickName;
    private Integer gameSize;
    private Boolean monke = false;

    public final Gson gson = new Gson();

    public GuiController(Stage stage, Gui gui) {
        this.stage = stage;

        Font.loadFont(getClass().getResourceAsStream("/fonts/godofwar.ttf"), 14);

        this.gui = gui;
        initialPhaseHandler = new InitialPhaseHandler(this, stage);
        start();
    }

    private void start() {
        stage.setTitle("Masters of Renaissance");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/ui/inkwell.png")));
        stage.setResizable(false);
        stage.setOnCloseRequest(closeEvent -> {
            //closeEvent.consume();
            //TODO: PopUp asking if the user is sure that they want to exit, maybe use the same popup when they click Esc
            stage.close();
            System.exit(0);
        });
        stage.setScene(initialPhaseHandler.getScene(ScenesEnum.WELCOME));
        stage.show();
        initialPhaseHandler.start();
    }

    @Override
    public boolean setup() throws IOException {
        initialPhaseHandler.setScene(ScenesEnum.CONNECTION);
        initialPhaseHandler.retrieveIpAndPort();

        return true;
    }

    public void setIpAndPort(String ip, String port) {
        if (ip.equals("0") && port.equals("0")) {
            startLocalGame();
            return;
        }

        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            socket.setKeepAlive(true);

            PrintWriter toServer = new PrintWriter(socket.getOutputStream());

            messageToServerHandler = new MessageToServerHandler(toServer, this);

            System.out.println("Connection OK");

            setActive(true);

            ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream());
            Thread t1 = new Thread(new MessageReceiver(socketIn, this));
            t1.start();

        } catch (IOException e) {
            System.err.println(e.getClass() + "Socket error");
        }

    }

    @Override
    public void setWaitingServerUpdate(boolean b) {
    }

    @Override
    public void askNickname() {
        Platform.runLater(() -> stage.setScene(initialPhaseHandler.getScene(ScenesEnum.REGISTRATION)));
        initialPhaseHandler.getNickNameAndGameSize();
    }

    public void setNickname(String receivedName) {
        nickName = receivedName;
        messageToServerHandler.sendMessageToServer(receivedName);
    }

    @Override
    public void askNumberOfPlayers() {
        messageToServerHandler.sendMessageToServer(gameSize.toString());
        if (gameSize != 1) {
            Platform.runLater(() -> initialPhaseHandler.setScene(ScenesEnum.WAITING_ROOM));
            initialPhaseHandler.setWaitingRoomName(nickName);
        }
    }

    public void setGameSize(String size) {
        gameSize = Integer.parseInt(size);
    }

    public void setSelectedLeaders(List<Boolean> leadersChoice) {
        List<LeaderCard> availableLeaders = getPlayer().getLeaders();

        for (int i = 0; i < leadersChoice.size(); i++) {
            if (leadersChoice.get(i)) {
                chosenLeadersId.add(availableLeaders.get(i).getId());
            }
        }
        synchronized (lock) {
            messageToServerHandler.generateEnvelope(MessageID.CHOOSE_LEADER_CARDS, chosenLeadersId.toString());
            lock.notifyAll();
        }
    }

    public void setInitialResourcePlacements(List<BiElement<Resource, Storage>> placements) {
        StoreResourcesMessage msg = new StoreResourcesMessage(placements, getPlayer().getTurnNumber());
        messageToServerHandler.generateEnvelope(MessageID.STORE_RESOURCES, gson.toJson(msg, StoreResourcesMessage.class));
        //WARNING: don't touch Monke, it will break everything
        monke = true;
    }

    @Override
    public void startGame() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        if(!monke) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> initialPhaseHandler.setScene(ScenesEnum.MAIN_BOARD));
                initialPhaseHandler.initiateBoard(chosenLeadersId, availableProductionCard);
                gamePhaseHandler = new GamePhaseHandler(this, stage);

                /*stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());*/

                stage.setMaximized(true);
                stage.centerOnScreen();
                //stage.sizeToScene();
            }
        } else {
            Platform.runLater(() -> initialPhaseHandler.setScene(ScenesEnum.MAIN_BOARD));
            initialPhaseHandler.initiateBoard(chosenLeadersId, availableProductionCard);
            gamePhaseHandler = new GamePhaseHandler(this, stage);

            /*stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());*/

            stage.setMaximized(true);
            stage.centerOnScreen();
            //stage.sizeToScene();
        }
    }

    @Override
    public void refreshView() {

    }

    @Override
    public void cardNotAvailable() {

    }

    @Override
    public void badProductionRequest() {

    }

    @Override
    public void badRearrangeRequest() {

    }

    @Override
    public void badPaymentRequest() {

    }

    @Override
    public void badDimensionRequest() {

    }

    @Override
    public void wrongStackRequest() {

    }

    @Override
    public void wrongLevelRequest() {

    }

    @Override
    public void badStorageRequest() {

    }

    @Override
    public void leaderNotActivable() {

    }

    @Override
    public boolean ackConfirmed(String msg) {
        return false;
    }

    @Override
    public void displayMessage(String str) {

    }

    @Override
    public void chooseStorageAfterMarketAction(String s) {

    }

    @Override
    public void chooseResourceAction(int quantity) {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> initialPhaseHandler.setScene(ScenesEnum.CHOOSE_RESOURCES));
            initialPhaseHandler.chooseResources(quantity);
        }
    }

    @Override
    public void chooseStorageAction(List<Resource> msg) {

    }

    @Override
    public void chooseLeadersAction() {
        List<LeaderCard> availableLeaders = getPlayer().getLeaders();
        initialPhaseHandler.displayLeaders(availableLeaders);
        synchronized (lock) {
            Platform.runLater(() -> initialPhaseHandler.setScene(ScenesEnum.CHOOSE_LEADERS));
            initialPhaseHandler.chooseLeaders();
        }
    }

    @Override
    public void updateMarket() {

    }

    @Override
    public void updateAvailableProductionCards() {

    }

    @Override
    public void updateOtherPlayer(NubPlayer pp) {

    }

    @Override
    public void moveLorenzo(int currentPosition) {

    }

    @Override
    public void showCurrentTurn(String s) {

    }

    @Override
    public void startTurnPhase() {

    }

    @Override
    public void buyFromMarket() {

    }

    @Override
    public void activateLeader() {

    }

    @Override
    public void updatePositionAction(PlayersPositionMessage msg) {

    }

    @Override
    public void showBoard() {

    }

    @Override
    public void rearrangeWarehouse() {

    }

    @Override
    public void startLocalGame() {

    }
}
