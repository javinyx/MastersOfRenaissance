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
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
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

/**
 * The GUI Controller which handles all messages and errors.
 */
public class GuiController extends ClientController {
    /**
     * The actual GUI application.
     */
    protected final Gui gui;
    private final Stage stage;
    private final InitialPhaseHandler initialPhaseHandler;
    private final GamePhaseHandler gamePhaseHandler;
    private final Object lock = new Object();

    private String nickName;
    private Integer gameSize;

    private final Gson gson = new Gson();

    /**
     * Instantiates a new Gui controller with the given stage, also adds a font and the different phase handlers.
     *
     * @param stage the stage
     * @param gui   the gui
     */
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

    /**
     * Sets ip and port and starts the connection.
     *
     * @param ip   the ip
     * @param port the port
     */
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

    /**
     * Sets the nickname of the player.
     *
     * @param receivedName the received name
     */
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

    /**
     * Sets the requested game size to enter the correct lobby.
     *
     * @param size the game size
     */
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

    /**
     * Sets the selected leaders by the player, which will later show in the game.
     *
     * @param leadersChoice the chosen leaders
     */
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

    //TODO: riprendere Javadoc da qui, rimangono poi i 2 phase handler
    /**
     * Sets the initial
     *
     * @param placements the placements
     */
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

    /**
     * Send buy market message.
     *
     * @param dim   the dim
     * @param index the index
     */
    public void sendBuyMarketMessage(char dim, int index, List<BiElement<MarbleAbility, Integer>> leaders) {
        BuyMarketMessage msg = new BuyMarketMessage(dim, index, leaders);
        messageToServerHandler.generateEnvelope(MessageID.BUY_FROM_MARKET, gson.toJson(msg, BuyMarketMessage.class));
    }

    @Override
    public void updateMarket() {
        gamePhaseHandler.setMarket();
    }

    /**
     * Send place resources message.
     *
     * @param placements the placements
     */
    public void sendPlaceResourcesMessage(List<BiElement<Resource, Storage>> placements) {
        StoreResourcesMessage msg = new StoreResourcesMessage(placements, getPlayer().getTurnNumber());
        messageToServerHandler.generateEnvelope(MessageID.STORE_RESOURCES, gson.toJson(msg, StoreResourcesMessage
                .class));
    }

    /**
     * Buy production card.
     *
     * @param cardId    the card id
     * @param stack     the stack
     * @param leaderIds the leader ids
     * @param wallet    the wallet
     */
    /* PRODUCTION CARDS ***********************************************************************************************/
    public void buyProductionCard(int cardId, int stack, List<Integer> leaderIds, ResourcesWallet wallet) {
        BuyProductionMessage msg = new BuyProductionMessage(cardId, stack, leaderIds, wallet);
        messageToServerHandler.generateEnvelope(MessageID.BUY_PRODUCTION_CARD, gson.toJson(msg));
    }

    /**
     * Gets available production cards.
     *
     * @return the available production cards
     */
    public List<ConcreteProductionCard> getAvailableProductionCards() {
        return availableProductionCard;
    }

    /**
     * Send production message.
     *
     * @param prodCards     the prod cards
     * @param resWal        the res wal
     * @param leaderCards   the leader cards
     * @param leaderOutputs the leader outputs
     * @param basicProd     the basic prod
     * @param basicOutput   the basic output
     */
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
        if(!isRegistrationPhase())
            Platform.runLater(() -> gamePhaseHandler.updateBoard());
    }

    /**
     * Gets normal turn.
     *
     * @return the normal turn
     */
    public boolean getNormalTurn() {
        return normalTurn;
    }

    /**
     * Gets pope favours.
     *
     * @return the pope favours
     */
    public List<Integer> getPopeFavours() {
        return popeStatusGeneral;
    }

    /**
     * Send rearrange message.
     *
     * @param placements the placements
     */
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
        gamePhaseHandler.sendErrorToMsgBoard("The card you chosen is not available. Please, try again with another one.");
    }

    @Override
    public void badProductionRequest() {
        gamePhaseHandler.sendErrorToMsgBoard("The production could not be performed: your request was malformed. Please, try again.");
    }

    @Override
    public void badRearrangeRequest() {
        gamePhaseHandler.sendErrorToMsgBoard("The rearrange could not be performed: a bad request has been sent to the server." +
                "Please, try again.");
    }

    @Override
    public void badPaymentRequest() {
        gamePhaseHandler.sendErrorToMsgBoard("The purchase could not be performed: a bad payment request has been sent to the server.");
    }

    @Override
    public void badDimensionRequest() {
        gamePhaseHandler.sendErrorToMsgBoard("The action in the market could not be performed: check the dimension you chose and try again.");
    }

    @Override
    public void wrongStackRequest() {
        gamePhaseHandler.sendErrorToMsgBoard("The stack you have chosen is incorrect.");
    }

    @Override
    public void wrongLevelRequest() {
        gamePhaseHandler.sendErrorToMsgBoard("The level of the card you chose is not compatible with your status.");
    }

    @Override
    public void leaderNotActivable() {
        gamePhaseHandler.sendErrorToMsgBoard("This leader can't be activated yet: you don't have enough resources or development cards");
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
        //Platform.runLater(() -> initialPhaseHandler.);
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
        gamePhaseHandler.sendToMsgBoard("Now is " + s + "'s turn. Please wait until it's your turn.");
    }

    @Override
    public void startTurnPhase() {
        Platform.runLater(() -> gamePhaseHandler.updateBoard());
    }

    @Override
    public void activateLeader() {
        Platform.runLater(() -> gamePhaseHandler.updateBoard());
    }

    /**
     * Send activate leader.
     *
     * @param leaderCard the leader card
     */
    public void sendActivateLeader(String leaderCard) {
        messageToServerHandler.generateEnvelope(MessageID.ACTIVATE_LEADER, leaderCard);
    }

    /**
     * Send discard leader.
     *
     * @param leaderCard the leader card
     */
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
        Platform.runLater(() -> gamePhaseHandler.setActionToken(act));
        gamePhaseHandler.sendToMsgBoard("Lorenzo now is in position: " + lorenzoPos);
        gamePhaseHandler.sendToMsgBoard(act.print());
    }

    @Override
    public void startGameNotEndTurn() {
        gamePhaseHandler.sendToMsgBoard("You can't skip the turn. Please do a main action: buy a development card, " +
                "produce or buy resources from the market.");
    }
}
