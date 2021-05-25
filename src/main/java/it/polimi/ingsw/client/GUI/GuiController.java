package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI.sceneHandlers.InitialPhaseHandler;
import it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum;
import it.polimi.ingsw.client.MessageReceiver;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.concreteMessages.PlayersPositionMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.market.Resource;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;

public class GuiController extends ClientController {
    protected final Gui gui;
    private Stage stage;
    private InitialPhaseHandler initialPhaseHandler;

    private String ip, port;



    public GuiController(Stage stage, Gui gui){
        this.stage = stage;
        this.gui = gui;
        initialPhaseHandler = new InitialPhaseHandler(this, stage);
        start();
    }

    private void start(){
        stage.setTitle("Masters of Renaissance");
        stage.setScene(initialPhaseHandler.getScene(ScenesEnum.WELCOME));
        stage.show();
        initialPhaseHandler.start();
    }

    @Override
    @FXML
    public boolean setup() throws IOException {
        stage.setScene(initialPhaseHandler.getScene(ScenesEnum.CONNECTION));
        stage.show();

        initialPhaseHandler.retrieveIpAndPort();

        return true;
    }

    public void setIpAndPort(String ip, String port){
        this.ip = ip;
        this.port = port;

        if(ip.equals("0") && port.equals("0")){
            startLocalGame();
            return;
        }

        try {
            Socket socket = new Socket(ip, Integer.parseInt(port));
            socket.setKeepAlive(true);

            PrintWriter toServer = new PrintWriter(socket.getOutputStream());

            messageToServerHandler = new MessageToServerHandler(toServer, this);

            System.out.println("Connection OK");

            try (socket; ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream()); toServer) {
                Thread t0 = new Thread(new MessageReceiver(socketIn, this));
                t0.start();
                t0.join();
                synchronized (this){
                    this.wait(3000);
                }

            } catch (InterruptedException | NoSuchElementException e) {
                setClosedConnection("Connection closed from the client side");
                Thread.currentThread().interrupt();
            }
        }catch(IOException e){
            System.err.println(e.getClass() + "Socket error");
        }

    }

    @Override
    public void setWaitingServerUpdate(boolean b) {
    }

    @Override
    @FXML
    public void askNickname() {
        stage.setScene(initialPhaseHandler.getScene(ScenesEnum.REGISTRATION));
        stage.show();

        BiElement<String, Integer> nickAndSize = initialPhaseHandler.getNickNameAndGameSize();

        messageToServerHandler.sendMessageToServer(nickAndSize.getFirstValue());
        player = new NubPlayer(nickAndSize.getFirstValue());
    }

    public void setNickname(){

    }

    @Override
    public void askNumberOfPlayers() {
        //messageToServerHandler.sendMessageToServer();
    }

    @Override
    public void startGame() {

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

    }

    @Override
    public void chooseStorageAction(List<Resource> msg) {

    }

    @Override
    public void chooseLeadersAction() {

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
    public void startLocalGame(){

    }
}
