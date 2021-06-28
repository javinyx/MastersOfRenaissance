package it.polimi.ingsw.client.GUI;

import com.google.gson.Gson;
import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.GUI.sceneHandlers.GamePhaseHandler;
import it.polimi.ingsw.client.GUI.sceneHandlers.InitialPhaseHandler;
import it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum;
import it.polimi.ingsw.client.LocalAdapter;
import it.polimi.ingsw.client.MessageReceiver;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.*;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GuiController extends ClientController {
    protected final Gui gui;
    private final Stage stage;
    private final InitialPhaseHandler initialPhaseHandler;
    private final GamePhaseHandler gamePhaseHandler;
    private final Object lock = new Object();

    private String nickName;
    private Integer gameSize;

    public final Gson gson = new Gson();

    public GuiController(Stage stage, Gui gui) {
        this.stage = stage;
        this.gui = gui;

        Font.loadFont(getClass().getResourceAsStream("/fonts/godofwar.ttf"), 14);
        initialPhaseHandler = new InitialPhaseHandler(this, stage);
        gamePhaseHandler = new GamePhaseHandler(this, stage);

        start();
    }

    /* INIT PHASE *****************************************************************************************************/
    private void start() {
        stage.setTitle("Masters of Renaissance");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/ui/inkwell.png")));
        stage.setResizable(false);
        stage.setOnCloseRequest(closeEvent -> {
            stage.close();
            System.exit(0);
        });
        stage.setScene(initialPhaseHandler.getScene(ScenesEnum.WELCOME));
        stage.show();
        initialPhaseHandler.start();
    }

    /* CONNECTION PHASE ***********************************************************************************************/
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

    /* REGISTRATION PHASE *********************************************************************************************/
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

    /* INITIAL CHOOSE PHASE *******************************************************************************************/
    @Override
    public void chooseLeadersAction() {
        List<LeaderCard> availableLeaders = getPlayer().getLeaders();
        initialPhaseHandler.displayLeaders(availableLeaders);
        synchronized (lock) {
            Platform.runLater(() -> initialPhaseHandler.setScene(ScenesEnum.CHOOSE_LEADERS));
            initialPhaseHandler.chooseLeaders();
        }
    }

    public void setSelectedLeaders(List<Boolean> leadersChoice) {
        Platform.runLater(initialPhaseHandler::waitStartGame);
        List<LeaderCard> availableLeaders = getPlayer().getLeaders();
        List<LeaderCard> chosenLeaders = new ArrayList<>();
        List<Integer> chosenLeadersId = new ArrayList<>();

        for (int i = 0; i < leadersChoice.size(); i++) {
            if (leadersChoice.get(i)) {
                chosenLeadersId.add(availableLeaders.get(i).getId());
                chosenLeaders.add(availableLeaders.get(i));
            }
        }

        synchronized (lock) {
            getPlayer().setLeaders(chosenLeaders);
            messageToServerHandler.generateEnvelope(MessageID.CHOOSE_LEADER_CARDS, chosenLeadersId.toString());
            lock.notifyAll();
        }
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

    public void setInitialResourcePlacements(List<BiElement<Resource, Storage>> placements) {
        Platform.runLater(initialPhaseHandler::waitStartGame);
        StoreResourcesMessage msg = new StoreResourcesMessage(placements, getPlayer().getTurnNumber());
        messageToServerHandler.generateEnvelope(MessageID.STORE_RESOURCES,
                gson.toJson(msg, StoreResourcesMessage.class));
    }

    /* START GAME PHASE ***********************************************************************************************/
    @Override
    public void startGame() {
        normalTurn = true;
        if (isRegistrationPhase() && gameSize == 1) {
            initialGameStart();
        } else if (isRegistrationPhase() && gameSize != 1) {
            System.out.println("Init multiplayer start");
            initialGameStart();
        } else {
            System.out.println("Middle game start");
            middleGameStart();
        }
    }

    private void middleGameStart() {
        initialGameStart();
    }

    private void initialGameStart() {
        setRegistrationPhase(false);

        Platform.runLater(() -> {
            gamePhaseHandler.setScene(ScenesEnum.MAIN_BOARD);
            gamePhaseHandler.initiateBoard();
        });
    }

    @Override
    public void startLocalGame() {
        localGame = true;
        messageToServerHandler = new LocalAdapter(this);
        askNickname();
    }

    /* MARKET ACTIONS *************************************************************************************************/
    @Override
    public void buyFromMarket() {

    }

    @Override
    public void chooseStorageAfterMarketAction(String s) {
        List<Resource> res = (new ArrayList<>(Arrays.asList(s.substring(1, s.length() - 1).split(", "))))
                .stream().map(this::convertStringToResource).collect(Collectors.toList());
        chooseStorageAction(res);
    }

    @Override
    public void chooseStorageAction(List<Resource> res) {
        List<Resource> tbdRes = new ArrayList<>();

        for (Resource r : res) {
            if (r != null && r != Resource.FAITH) {
                tbdRes.add(r);
            }
        }

        Platform.runLater(() -> gamePhaseHandler.chooseStoragePopUp(tbdRes, false, false));
    }

    public void sendBuyMarketMessage(char dim, int index) {
        //TODO: option for active leader
        BuyMarketMessage msg = new BuyMarketMessage(dim, index, null);
        messageToServerHandler.generateEnvelope(MessageID.BUY_FROM_MARKET, gson.toJson(msg, BuyMarketMessage.class));
    }

    @Override
    public void updateMarket() {
        gamePhaseHandler.setMarket();
    }

    public void sendPlaceResourcesMessage(List<BiElement<Resource, Storage>> placements) {
        StoreResourcesMessage msg = new StoreResourcesMessage(placements, getPlayer().getTurnNumber());
        messageToServerHandler.generateEnvelope(MessageID.STORE_RESOURCES, gson.toJson(msg, StoreResourcesMessage
                .class));
    }

    /* PRODUCTION CARDS ***********************************************************************************************/
    public void buyProductionCard(int cardId, int stack, List<Integer> leaderIds, ResourcesWallet wallet) {
        BuyProductionMessage msg = new BuyProductionMessage(cardId, stack, leaderIds, wallet);
        messageToServerHandler.generateEnvelope(MessageID.BUY_PRODUCTION_CARD, gson.toJson(msg));
    }

    public List<ConcreteProductionCard> getAvailableProductionCards() {
        return availableProductionCard;
    }

    /* PRODUCE PHASE **************************************************************************************************/
    public void sendProductionMessage(List<ConcreteProductionCard> prodCards, ResourcesWallet resWal,
                                      List<BoostAbility> leaderCards, List<Resource> leaderOutputs, boolean basicProd,
                                      Resource basicOutput) {
        ProduceMessage msg = new ProduceMessage(prodCards, resWal, leaderCards, leaderOutputs, basicProd,
                basicOutput);
        messageToServerHandler.generateEnvelope(MessageID.PRODUCE, gson.toJson(msg, ProduceMessage.class));
    }

    /* GENERAL ACTIONS ************************************************************************************************/
    @Override
    public void displayWaitMessage() {
        Platform.runLater(() -> gamePhaseHandler.updateBoard());
    }

    public boolean getNormalTurn() {
        return normalTurn;
    }

    public List<Integer> getPopeFavours() {
        return popeStatusGeneral;
    }

    public void sendRearrangeMessage(List<BiElement<Resource, Storage>> placements) {
        StoreResourcesMessage msg = new StoreResourcesMessage(placements, getPlayer().getTurnNumber());
        messageToServerHandler.generateEnvelope(MessageID.REARRANGE_WAREHOUSE, gson.toJson(msg, StoreResourcesMessage
                .class));
    }

    /* ERROR REQUESTS *************************************************************************************************/
    @Override
    public void badStorageRequest() {
        Platform.runLater(() -> gamePhaseHandler.chooseStoragePopUp(null, true, false));
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
        gamePhaseHandler.sendToMsgBoard("The stack you have chosen is incorrect.");
    }

    @Override
    public void wrongLevelRequest() {

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
    public void nickError() {

    }

    @Override
    public void setWaitingServerUpdate(boolean b) {

    }

    @Override
    public void updateAvailableProductionCards() {
        gamePhaseHandler.setProductionCards();
    }

    @Override
    public void updateOtherPlayer(NubPlayer pp) {

    }

    @Override
    protected void winner(String winner) {

    }

    @Override
    public void moveLorenzo(int currentPosition) {

    }

    @Override
    public void showCurrentTurn(String s) {

    }

    @Override
    public void startTurnPhase() {
        Platform.runLater(() -> gamePhaseHandler.updateBoard());
    }

    @Override
    public void activateLeader() {
        //UNUSED HERE
    }

    public void sendActivateLeader(String leaderCard) {
        messageToServerHandler.generateEnvelope(MessageID.ACTIVATE_LEADER, leaderCard);
    }

    public void sendDiscardLeader(String leaderCard) {
        messageToServerHandler.generateEnvelope(MessageID.DISCARD_LEADER, leaderCard);
    }

    @Override
    public void updatePositionAction(PlayersPositionMessage msg) {
        List<NubPlayer> allPlayers = getTotalPlayers();

        for(BiElement<Integer, Integer> pos : msg.getNewPlayersPosition()){
            if(pos.getFirstValue()==0){
            }else{
                for(NubPlayer p : allPlayers){
                    if(p.getTurnNumber()== pos.getFirstValue() && p.getCurrPos()!=pos.getSecondValue()){
                        p.setCurrPos(pos.getSecondValue());
                        break;
                    }else if(p.getTurnNumber()==pos.getFirstValue() && p.equals(getPlayer())){
                        player.setCurrPos(pos.getSecondValue());
                        break;
                    }
                }
            }
        }
        Platform.runLater(gamePhaseHandler::updateBoard);
    }

    @Override
    public void showBoard() {

    }

    @Override
    public void rearrangeWarehouse() {

    }

    @Override
    public void showLorenzoStatus(ActionToken act) {

    }

    @Override
    public void startGameNotEndTurn() {

    }
}
