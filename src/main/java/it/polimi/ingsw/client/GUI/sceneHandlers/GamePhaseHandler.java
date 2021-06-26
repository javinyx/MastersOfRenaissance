package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;
import static it.polimi.ingsw.misc.Storage.*;
import static it.polimi.ingsw.model.market.Resource.*;

public class GamePhaseHandler extends PhaseHandler {
    /* MAIN ***********************************************************************************************************/
    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();
    @FXML
    private AnchorPane mainBoard;
    @FXML
    private Button endTurnBtn, productionCardsOpen, produceBtn, activateLeaderBtn, discardLeaderBtn;
    @FXML
    private ImageView productionStack11, productionStack12, productionStack13, productionStack21, productionStack22,
            productionStack23, productionStack31, productionStack32, productionStack33;
    @FXML
    private Label coinLblLC, servantLblLC, shieldLblLC, stoneLblLC;

    /* LEADER CARDS ***************************************************************************************************/
    @FXML
    private ImageView leader1Show, leader2Show;

    /* MARKET & MARKET POPUP ******************************************************************************************/
    @FXML
    private Button marketBackBtn;
    @FXML
    private Circle extraMarble, extraMarblePU;
    @FXML
    private GridPane marketMarbles, marketMarblesPU, productionCards;
    @FXML
    private ImageView marketRow1Btn, marketRow2Btn, marketRow3Btn, marketCol1Btn, marketCol2Btn, marketCol3Btn,
            marketCol4Btn;
    @FXML
    private Region marketRegion;

    /* WAREHOUSE & CHOOSE STORAGE POPUP *******************************************************************************/
    private Node target;
    private List<Resource> tmpRes = new ArrayList<>();
    @FXML
    private Button chooseStorageBtnPU;
    @FXML
    private ImageView resource1ImgPU, resource2ImgPU, resource3ImgPU, resource4ImgPU, bin;
    @FXML
    private Region shelf1PU, shelf21PU, shelf22PU, shelf31PU, shelf32PU, shelf33PU;
    @FXML
    private ImageView shelf1MB, shelf21MB, shelf22MB, shelf31MB, shelf32MB, shelf33MB,
            shelf1ImgPU, shelf21ImgPU, shelf22ImgPU, shelf31ImgPU, shelf32ImgPU, shelf33ImgPU;
    @FXML
    private AnchorPane chooseStoragePU;

    /* ENEMY BOARD ****************************************************************************************************/
    @FXML
    private Button player1Btn, player2Btn, player3Btn;
    @FXML
    private ImageView player1FaithImg, player2FaithImg, player3FaithImg;
    @FXML
    private Label player1FaithLbl, player2FaithLbl, player3FaithLbl;

    /* OTHER PLAYERS POPUP ********************************************************************************************/
    @FXML
    private Button opBackBtn;
    @FXML
    private ImageView opLead1Img, opLead2Img, opShelf1Img, opShelf2Img, opShelf3Img, opStack1Img, opStack2Img,
            opStack3Img;
    @FXML
    private Label opLbl, opQtyShelf1Lbl, opQtyShelf2Lbl, opQtyShelf3Lbl, opQtyStoneLbl, opQtyCoinLbl, opQtyServantLbl,
            opQtyShieldLbl, opTotalProdCardsLbl;

    /* MESSAGE BOARD **************************************************************************************************/
    @FXML
    private TextArea msgBoard;

    /* FAITH TRACK ****************************************************************************************************/
    private List<BiElement<Double, Double>> faithCoords;
    @FXML
    private ImageView redCross;

    /* PRODUCTION CARDS POPUP *****************************************************************************************/
    @FXML
    private Label listCostLblCP, qtyStoneLblCP, qtyShieldLblCP, qtyCoinLblCP, qtyServantLblCP, stoneLblCP, servantLblCP,
            coinLblCP, shieldLblCP;
    @FXML
    private Button productionCardBackBtn, stoneSubBtnCP, stoneAddBtnCP, servantSubBtnCP, servantAddBtnCP, coinSubBtnCP,
            coinAddBtnCP, shieldSubBtnCP, shieldAddBtnCP, backPaymentBtnCP, stack1Btn, stack2Btn,
            stack3Btn;
    @FXML
    private ImageView shelf1CP, shelf21CP, shelf22CP, shelf31CP, shelf32CP, shelf33CP;
    @FXML
    private ToggleButton shelf1ToggleCP, shelf21ToggleCP, shelf22ToggleCP, shelf31ToggleCP, shelf32ToggleCP,
            shelf33ToggleCP;

    /* PRODUCE ********************************************************************************************************/
    @FXML
    private Button produceBackBtn, produceConfirmBtn;
    @FXML
    private ImageView produce1Img, produce2Img, produce3Img, produce4Img;
    @FXML
    private ToggleButton produce1Toggle, produce2Toggle, produce3Toggle, produce4Toggle;

    /* DISCARD & ACTIVATE LEADER **************************************************************************************/
    @FXML
    private RadioButton DAL1Toggle, DAL2Toggle;
    @FXML
    private Button DALConfirmBtn, DALBackBtn;
    @FXML
    private ImageView DAL1Img, DAL2Img;
    @FXML
    private ToggleGroup chosenLeaderGrp;
    @FXML
    private Label DALLbl;


    public GamePhaseHandler(GuiController controller, Stage stage) {
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(MAIN_BOARD, MARKET, STORAGE, OTHER_PLAYERS,
                PRODUCTION_CARDS, CHOOSE_PAYMENT, CHOOSE_LEADERS, PRODUCE, DISCARD_ACTIVATE_LEADER));
        for (ScenesEnum path : allPaths) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path.getPath()));
            loader.setController(this);

            try {
                Scene scene = new Scene(loader.load());
                scene.getStylesheets().addAll(this.getClass().getResource("/fxml/style.css").toExternalForm());
                scene.setCursor(new ImageCursor(new Image("/img/ui/cursor.png"), 5, 5));
                sceneMap.put(path, scene);
            } catch (IOException e) {
                System.err.println("Loader cannot load " + path);
            }
        }

        buildGeneralSceneMap(sceneMap);
    }

    public boolean setScene(ScenesEnum sceneName) {
        if (!sceneMap.containsKey(sceneName)) {
            return false;
        }
        stage.setScene(sceneMap.get(sceneName));
        return true;
    }

    /* MAIN BOARD *****************************************************************************************************/
    public void initiateBoard(List<Integer> chosenLeaderId) {
        setLeader(chosenLeaderId);
        setWarehouse();
        setMarket();
        setProductionCards(controller.getAvailableProductionCards());
        setEnemyBoard();
        initiateFaithTrack();
        setFaithTrack();
        observePlayerActions();

        if (controller.getPlayer().isMyTurn()) {
            sendToMsgBoard("It is now your turn, please either buy from market, buy production cards or" +
                    " activate production.");
        } else {
            sendToMsgBoard("It is not your turn yet, please wait for the other players.");
        }

    }

    public void updateBoard() {
        setWarehouse();
        setMarket();
        setLootChest();
        setProductionCards(controller.getAvailableProductionCards());
        setEnemyBoard();
        setFaithTrack();
        setStacks();
        observePlayerActions();

        if (controller.getPlayer().isMyTurn()) {
            sendToMsgBoard("It is now your turn, please either buy from market, buy development cards or" +
                    " activate production.");
        } else {
            sendToMsgBoard("It is not your turn yet, please wait for the other players.");
        }
    }

    public void observePlayerActions() {
        marketRegion.setOnMouseClicked(event -> {
            if (controller.getNormalTurn() && controller.getPlayer().isMyTurn()) {
                mainBoard.setEffect(new GaussianBlur());
                marketPopUp();
            } else {
                sendToMsgBoard("You have already completed a main action for this turn," +
                        "please end your turn when you're ready.");
            }
        });

        produceBtn.setOnAction(actionEvent -> {
            if (controller.getNormalTurn() && controller.getPlayer().isMyTurn()) {
                producePopup();
            } else {
                sendToMsgBoard("You have already completed a main action for this turn," +
                        "please end your turn when you're ready.");
            }
        });

        productionCardsOpen.setOnAction(actionEvent -> {
            setProductionCards(controller.getAvailableProductionCards());
            productionCardsPopup();
        });

        endTurnBtn.setOnAction(actionEvent -> {
            tmpRes.clear();
            controller.passTurn();
        });

        activateLeaderBtn.setOnAction(actionEvent -> activateLeader());

        discardLeaderBtn.setOnAction(actionEvent -> discardLeader());

        player1Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(0)));
        player2Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(1)));
        player3Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(2)));
    }

    private void setLeader(List<Integer> chosenLeaderId) {
        leader1Show.setImage(new Image("img/leaderCards/" + chosenLeaderId.get(0) + ".png"));
        leader1Show.setEffect(new SepiaTone(0.6));
        leader2Show.setImage(new Image("img/leaderCards/" + chosenLeaderId.get(1) + ".png"));
        leader2Show.setEffect(new SepiaTone(0.6));
    }

    private void initiateFaithTrack() {
        double x = 177;
        double y = 272;

        faithCoords = new ArrayList<>();
        faithCoords.add(new BiElement(x, y)); //Pos 0

        for (int i = 0; i <= 24; i++) {
            switch (i) {
                case 1, 2, 5, 6, 7, 8, 9, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24 -> {
                    // Move Right from previous cell
                    x = x + 40;
                    faithCoords.add(new BiElement(x, y));
                }
                case 3, 4, 17, 18 -> {
                    // Move Up from previous cell
                    y = y - 40;
                    faithCoords.add(new BiElement(x, y));
                }
                case 10, 11 -> {
                    // Move Down from previous cell
                    y = y + 40;
                    faithCoords.add(new BiElement(x, y));
                }
            }
        }
    }

    private void setFaithTrack() {
        int currPos = controller.getPlayer().getCurrPos();
        redCross.setLayoutX(faithCoords.get(currPos).getFirstValue());
        redCross.setLayoutY(faithCoords.get(currPos).getSecondValue());
        //TODO: Pope Favours
    }

    private void setWarehouse() {
        Map<BiElement<Resource, Storage>, Integer> wh = controller.getPlayer().getWarehouse();
        wh.forEach((x, y) -> {
            Image img = new Image("img/pawns/" + x.getFirstValue().toString().toLowerCase() + ".png");
            switch (x.getSecondValue()) {
                case WAREHOUSE_SMALL -> {
                    if (y == 1) {
                        shelf1MB.setImage(img);
                        shelf1PU.setDisable(true);
                        shelf1ImgPU.setLayoutX(shelf1PU.getLayoutX() + 5);
                        shelf1ImgPU.setLayoutY(shelf1PU.getLayoutY() + 5);
                        shelf1ImgPU.setImage(img);
                    } else {
                        shelf1MB.setImage(null);
                        shelf1PU.setDisable(false);
                        shelf1ImgPU.setImage(null);
                    }
                }
                case WAREHOUSE_MID -> {
                    if (y >= 1) {
                        shelf21MB.setImage(img);
                        shelf21PU.setDisable(true);
                        shelf21ImgPU.setLayoutX(shelf21PU.getLayoutX() + 5);
                        shelf21ImgPU.setLayoutY(shelf21PU.getLayoutY() + 5);
                        shelf21ImgPU.setImage(img);
                    } else {
                        shelf21MB.setImage(null);
                        shelf21PU.setDisable(false);
                        shelf21ImgPU.setImage(null);
                    }
                    if (y == 2) {
                        shelf22MB.setImage(img);
                        shelf22PU.setDisable(true);
                        shelf22ImgPU.setLayoutX(shelf22PU.getLayoutX() + 5);
                        shelf22ImgPU.setLayoutY(shelf22PU.getLayoutY() + 5);
                        shelf22ImgPU.setImage(img);
                    } else {
                        shelf22MB.setImage(null);
                        shelf22PU.setDisable(false);
                        shelf22ImgPU.setImage(null);
                    }
                }
                case WAREHOUSE_LARGE -> {
                    if (y >= 1) {
                        shelf31MB.setImage(img);
                        shelf31PU.setDisable(true);
                        shelf31ImgPU.setLayoutX(shelf31PU.getLayoutX() + 5);
                        shelf31ImgPU.setLayoutY(shelf31PU.getLayoutY() + 5);
                        shelf31ImgPU.setImage(img);
                    } else {
                        shelf31MB.setImage(null);
                        shelf31PU.setDisable(false);
                        shelf31ImgPU.setImage(null);
                    }
                    if (y >= 2) {
                        shelf32MB.setImage(img);
                        shelf32PU.setDisable(true);
                        shelf32ImgPU.setLayoutX(shelf32PU.getLayoutX() + 5);
                        shelf32ImgPU.setLayoutY(shelf32PU.getLayoutY() + 5);
                        shelf32ImgPU.setImage(img);
                    } else {
                        shelf32MB.setImage(null);
                        shelf32PU.setDisable(false);
                        shelf32ImgPU.setImage(null);
                    }
                    if (y == 3) {
                        shelf33MB.setImage(img);
                        shelf33PU.setDisable(true);
                        shelf33ImgPU.setLayoutX(shelf33PU.getLayoutX() + 5);
                        shelf33ImgPU.setLayoutY(shelf33PU.getLayoutY() + 5);
                        shelf33ImgPU.setImage(img);
                    } else {
                        shelf33MB.setImage(null);
                        shelf33PU.setDisable(false);
                        shelf33ImgPU.setImage(null);
                    }
                }
            }
        });
    }

    private void setLootChest() {
        Map<BiElement<Resource, Storage>, Integer> lc = controller.getPlayer().getLootchest();
        lc.forEach((x, y) -> {
            System.out.println("resource: " + x.getFirstValue() + "; storage: " + x.getSecondValue() + "; amount: " + y);
            switch (x.getFirstValue()) {
                case COIN: {
                    if (y != null) {
                        coinLblLC.setText("x" + y);
                    } else {
                        coinLblLC.setText("x0");
                    }
                }
                case SHIELD: {
                    if (y != null) {
                        shieldLblCP.setText("x" + y);
                    } else {
                        shieldLblCP.setText("x0");
                    }
                }
                case STONE: {
                    if (y != null) {
                        stoneLblLC.setText("x" + y);
                    } else {
                        stoneLblCP.setText("x0");
                    }
                }
                case SERVANT: {
                    if (y != null) {
                        servantLblCP.setText("x" + y);
                    } else {
                        servantLblCP.setText("x0");
                    }
                }
            }
        });
    }

    private void setStacks() {
        List<ConcreteProductionCard> tstack;

        for (int i = 0; i < 3; i++) {
            tstack = new ArrayList<>(controller.getPlayer().getProductionStacks().get(i));
            for (int j = 0, curr = (i * 3) + j; j < tstack.size(); j++, curr++) {
                switch (curr) {
                    case 0 -> productionStack11.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 1 -> productionStack12.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 2 -> productionStack13.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 3 -> productionStack21.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 4 -> productionStack22.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 5 -> productionStack23.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 6 -> productionStack31.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 7 -> productionStack32.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 8 -> productionStack33.setImage(new Image("img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                }
            }
        }
    }


    /* MARKET & MARKET POPUP ******************************************************************************************/
    //TODO: Select Leader
    public void setMarket() {
        extraMarble.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));
        extraMarblePU.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                setMarbleColor(x, y, marketMarbles, controller.getMarket().getMarketBoard()[x][y].getHexCode());
                setMarbleColor(x, y, marketMarblesPU, controller.getMarket().getMarketBoard()[x][y].getHexCode());
            }
        }
    }

    private void setMarbleColor(int row, int column, GridPane gridPane, String color) {
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if (gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                ((Circle) node).setFill(Color.web(color));
                break;
            }
        }
    }

    private void marketPopUp() {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        mainBoard.setEffect(new GaussianBlur());
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(MARKET));
        popUpStage.show();

        marketRow1Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('r', 1);
            mainBoard.setEffect(null);
            popUpStage.close();
        });
        marketRow2Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('r', 2);
            mainBoard.setEffect(null);
            popUpStage.close();
        });
        marketRow3Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('r', 3);
            mainBoard.setEffect(null);
            popUpStage.close();
        });
        marketCol1Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('c', 1);
            mainBoard.setEffect(null);
            popUpStage.close();
        });
        marketCol2Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('c', 2);
            mainBoard.setEffect(null);
            popUpStage.close();
        });
        marketCol3Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('c', 3);
            mainBoard.setEffect(null);
            popUpStage.close();
        });
        marketCol4Btn.setOnMouseClicked(mouseEvent -> {
            controller.sendBuyMarketMessage('c', 4);
            mainBoard.setEffect(null);
            popUpStage.close();
        });

        marketBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    /* ENEMY BOARD ****************************************************************************************************/
    private void setEnemyBoard() {
        List<Button> playersBtns = new ArrayList<>(Arrays.asList(player1Btn, player2Btn, player3Btn));
        List<Label> playersFaithLbls = new ArrayList<>(Arrays.asList(player1FaithLbl, player2FaithLbl,
                player3FaithLbl));
        List<ImageView> playersFaithImgs = new ArrayList<>(Arrays.asList(player1FaithImg, player2FaithImg,
                player3FaithImg));

        int ctr = 0;
        if (controller.getOtherPlayers().size() == 0) {
            playersBtns.get(0).setText("Lorenzo");
            playersBtns.get(0).getStyleClass().remove("btn");
            playersBtns.get(0).getStyleClass().add("lorenzo");
            playersFaithLbls.get(0).setText(String.valueOf(controller.getLorenzoPos()));
            playersFaithImgs.get(0).setImage(new Image("img/pawns/blackCrossNew.png"));
            ctr++;
        } else {
            for (NubPlayer p : controller.getOtherPlayers()) {
                playersBtns.get(ctr).setText(p.getNickname() + "(#" + p.getTurnNumber() + ")");
                playersFaithLbls.get(ctr).setText(String.valueOf(p.getCurrPos()));
                ctr++;
            }
        }
        //if the game is not of size 4, then hide other buttons
        while (ctr < 3) {
            playersBtns.get(ctr).setManaged(false);
            playersBtns.get(ctr).setVisible(false);
            playersFaithLbls.get(ctr).setVisible(false);
            playersFaithLbls.get(ctr).setManaged(false);
            playersFaithImgs.get(ctr).setVisible(false);
            playersFaithImgs.get(ctr).setManaged(false);
            ctr++;
        }
    }

    /* OTHER PLAYERS POPUP ********************************************************************************************/
    private void otherPlayersPopUp(NubPlayer player) {

        // This only happens if it's a single player game and the player clicks on Lorenzo
        if (controller.getOtherPlayers().size() == 0) {
            return;
        }

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        mainBoard.setEffect(new GaussianBlur());
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(OTHER_PLAYERS));
        popUpStage.show();

        opBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });

        opLbl.setText("You're viewing " + player.getNickname() + "'s status");
        if (player.getLeaders().size() > 0) {
            LeaderCard lead1 = player.getLeaders().get(0);
            opLead1Img.setImage(new Image("img/leaderCards/" + (lead1.isActive() ? String.valueOf(lead1.getId())
                    : "leaderBack") + ".png"));
        }
        if (player.getLeaders().size() > 1) {
            LeaderCard lead2 = player.getLeaders().get(1);
            opLead2Img.setImage(new Image("img/leaderCards/" + (lead2.isActive() ? String.valueOf(lead2.getId())
                    : "leaderBack") + ".png"));
        }

        Deque<ConcreteProductionCard> stack1 = player.getProductionStack(1);
        Deque<ConcreteProductionCard> stack2 = player.getProductionStack(2);
        Deque<ConcreteProductionCard> stack3 = player.getProductionStack(3);

        opTotalProdCardsLbl.setText("Total development cards: " + (stack1.size() + stack2.size() + stack3.size()));

        if (stack1.size() > 0) {
            opStack1Img.setImage(new Image("img/productionCardsFront/" + stack1.peekFirst().getId() + ".png"));
            opStack1Img.setVisible(true);
        } else {
            opStack1Img.setVisible(false);
        }

        if (stack2.size() > 0) {
            opStack2Img.setImage(new Image("img/productionCardsFront/" + stack2.peekFirst().getId() + ".png"));
            opStack2Img.setVisible(true);
        } else {
            opStack2Img.setVisible(false);
        }

        if (stack3.size() > 0) {
            opStack3Img.setImage(new Image("img/productionCardsFront/" + stack3.peekFirst().getId() + ".png"));
            opStack3Img.setVisible(true);
        } else {
            opStack3Img.setVisible(false);
        }

        Map<BiElement<Resource, Storage>, Integer> loot = player.getLootchest();
        loot.forEach((x, y) -> {
            switch (x.getFirstValue()) {
                case COIN -> opQtyCoinLbl.setText("x" + y);
                case SERVANT -> opQtyServantLbl.setText("x" + y);
                case SHIELD -> opQtyShieldLbl.setText("x" + y);
                case STONE -> opQtyStoneLbl.setText("x" + y);
            }
        });

        Map<BiElement<Resource, Storage>, Integer> war = player.getWarehouse();
        war.forEach((x, y) -> {
            Image img = new Image("img/pawns/" + x.getFirstValue().toString().toLowerCase() + ".png");
            switch (x.getSecondValue()) {
                case WAREHOUSE_SMALL -> {
                    opShelf1Img.setImage(img);
                    opQtyShelf1Lbl.setText("x" + y);
                }
                case WAREHOUSE_MID -> {
                    opShelf2Img.setImage(img);
                    opQtyShelf2Lbl.setText("x" + y);
                }
                case WAREHOUSE_LARGE -> {
                    opShelf3Img.setImage(img);
                    opQtyShelf3Lbl.setText("x" + y);
                }
            }
        });

    }

    /* MESSAGE BOARD **************************************************************************************************/
    public void sendToMsgBoard(String message) {
        msgBoard.appendText(message + '\n');
    }

    /* CHOOSE STORAGE POPUP *******************************************************************************************/
    public void chooseStoragePopUp(List<Resource> selectedRes, boolean isBadStorageRequest) {
        mainBoard.setEffect(new GaussianBlur());
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(STORAGE));
        popUpStage.show();

        List<BiElement<Resource, Storage>> tbdResourcePlacement = new ArrayList<>();

        if (!isBadStorageRequest) {
            tmpRes.addAll(selectedRes);

            if (selectedRes.size() >= 1)
                resource1ImgPU.setImage(new Image("img/pawns/" + selectedRes.get(0).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() >= 2)
                resource2ImgPU.setImage(new Image("img/pawns/" + selectedRes.get(1).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() >= 3)
                resource3ImgPU.setImage(new Image("img/pawns/" + selectedRes.get(2).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() == 4)
                resource4ImgPU.setImage(new Image("img/pawns/" + selectedRes.get(3).toString().toLowerCase() +
                        ".png"));

            resource1ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(0));
            });
            resource1ImgPU.setOnDragDone(this::sourceDragDone);

            resource2ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(1));
            });
            resource2ImgPU.setOnDragDone(this::sourceDragDone);

            resource3ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(2));
            });
            resource3ImgPU.setOnDragDone(this::sourceDragDone);

            resource4ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(3));
            });
            resource4ImgPU.setOnDragDone(this::sourceDragDone);
        } else {
            if (tmpRes.size() >= 1)
                resource1ImgPU.setImage(new Image("img/pawns/" + tmpRes.get(0).toString().toLowerCase() + ".png"));
            if (tmpRes.size() >= 2)
                resource2ImgPU.setImage(new Image("img/pawns/" + tmpRes.get(1).toString().toLowerCase() + ".png"));
            if (tmpRes.size() >= 3)
                resource3ImgPU.setImage(new Image("img/pawns/" + tmpRes.get(2).toString().toLowerCase() + ".png"));
            if (tmpRes.size() == 4)
                resource4ImgPU.setImage(new Image("img/pawns/" + tmpRes.get(3).toString().toLowerCase() + ".png"));

            resource1ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, tmpRes.get(0));
            });
            resource1ImgPU.setOnDragDone(this::sourceDragDone);

            resource2ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, tmpRes.get(1));
            });
            resource2ImgPU.setOnDragDone(this::sourceDragDone);

            resource3ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, tmpRes.get(2));
            });
            resource3ImgPU.setOnDragDone(this::sourceDragDone);

            resource4ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, tmpRes.get(3));
            });
            resource4ImgPU.setOnDragDone(this::sourceDragDone);
        }

        shelf1PU.setOnDragOver(this::targetDragOver);
        shelf1PU.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_SMALL));
        });

        shelf21PU.setOnDragOver(this::targetDragOver);
        shelf21PU.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_MID));
        });
        shelf22PU.setOnDragOver(this::targetDragOver);
        shelf22PU.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_MID));
        });

        shelf31PU.setOnDragOver(this::targetDragOver);
        shelf31PU.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
        });

        shelf32PU.setOnDragOver(this::targetDragOver);
        shelf32PU.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
        });

        shelf33PU.setOnDragOver(this::targetDragOver);
        shelf33PU.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
        });

        bin.setOnDragOver(this::targetDragOver);
        bin.setOnDragDropped(event -> {
            tbdResourcePlacement.add(new BiElement<>(discardDragDropped(event), Storage.DISCARD));
        });

        chooseStorageBtnPU.setOnAction(actionEvent -> {
            if (!isBadStorageRequest) {
                if (tbdResourcePlacement.size() == selectedRes.size()) {
                    mainBoard.setEffect(null);
                    popUpStage.close();
                    resetStoragePopUp();

                    // PRINT RESOURCE AND WAREHOUSE
                    for (int i = 0; i < tbdResourcePlacement.size(); i++) {
                        System.out.println(tbdResourcePlacement.get(i));
                    }
                    controller.sendPlaceResourcesMessage(tbdResourcePlacement);
                }
            } else {
                if (tbdResourcePlacement.size() == tmpRes.size()) {
                    mainBoard.setEffect(null);
                    popUpStage.close();
                    resetStoragePopUp();

                    // PRINT RESOURCE AND WAREHOUSE
                    for (int i = 0; i < tbdResourcePlacement.size(); i++) {
                        System.out.println(tbdResourcePlacement.get(i));
                    }
                    controller.sendPlaceResourcesMessage(tbdResourcePlacement);
                }
            }
        });
    }

    public void resetStoragePopUp() {
        resource1ImgPU.setLayoutX(109);
        resource1ImgPU.setLayoutY(117);
        resource1ImgPU.setDisable(false);
        resource1ImgPU.setImage(null);
        resource2ImgPU.setLayoutX(109);
        resource2ImgPU.setLayoutY(167);
        resource2ImgPU.setDisable(false);
        resource2ImgPU.setImage(null);
        resource3ImgPU.setLayoutX(109);
        resource3ImgPU.setLayoutY(217);
        resource3ImgPU.setDisable(false);
        resource3ImgPU.setImage(null);
        resource4ImgPU.setLayoutX(109);
        resource4ImgPU.setLayoutY(267);
        resource4ImgPU.setDisable(false);
        resource4ImgPU.setImage(null);

        shelf1PU.setDisable(false);
        shelf21PU.setDisable(false);
        shelf22PU.setDisable(false);
        shelf31PU.setDisable(false);
        shelf32PU.setDisable(false);
        shelf33PU.setDisable(false);

        shelf1ImgPU.setImage(null);
        shelf21ImgPU.setImage(null);
        shelf22ImgPU.setImage(null);
        shelf31ImgPU.setImage(null);
        shelf32ImgPU.setImage(null);
        shelf33ImgPU.setImage(null);

        setWarehouse();
    }

    private void sourceDragDetected(Event event, Resource resource) {
        Dragboard db = ((Node) event.getSource()).startDragAndDrop(TransferMode.ANY);
        ClipboardContent content = new ClipboardContent();
        content.putString(resource.toString());
        db.setContent(content);
        event.consume();
    }

    private void sourceDragDone(DragEvent event) {
        if (event.getTransferMode() == TransferMode.MOVE) {
            ((Node) event.getSource()).setLayoutX(target.getLayoutX() + 5);
            ((Node) event.getSource()).setLayoutY(target.getLayoutY() + 5);
            ((Node) event.getSource()).setDisable(true);
        }
        event.consume();
    }

    private void targetDragOver(DragEvent event) {
        if (event.getGestureSource() != event.getSource() &&
                event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.MOVE);
        }
        event.consume();
    }

    private Resource targetDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        target = (Node) event.getSource();
        ((Node) event.getSource()).setDisable(true);
        event.setDropCompleted(true);
        event.consume();

        return Resource.valueOf(db.getString());
    }

    private Resource discardDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        target = (Node) event.getSource();
        event.setDropCompleted(true);
        event.consume();

        return Resource.valueOf(db.getString());
    }

    /* PRODUCTION CARDS ***********************************************************************************************/
    private void productionCardsPopup() {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        mainBoard.setEffect(new GaussianBlur());
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(PRODUCTION_CARDS));
        popUpStage.show();

        productionCards.setOnMouseClicked(mouseEvent -> {
            if (controller.getNormalTurn() && controller.getPlayer().isMyTurn()) {
                clickGrid(mouseEvent, popUpStage);
            } else {
                sendToMsgBoard("You have already completed a main action for this turn," +
                        "please end your turn when you're ready.");
            }
        });

        productionCardBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    private void clickGrid(MouseEvent event, Stage popUpStage) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != productionCards) {
            String url = ((ImageView) clickedNode).getImage().getUrl();
            String cardIdS = url.substring(url.lastIndexOf("/") + 1).split("\\.")[0];
            Integer cardId = Integer.parseInt(cardIdS);
            Integer stack = askStack();

            popUpStage.close();
            choosePaymentPopUp(cardId, stack);
        }
    }

    private Integer askStack() {
        return 1;
    }

    public void setProductionCards(List<ConcreteProductionCard> availableProductionCards) {
        for (int x = 0, i = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++, i++) {
                setProductionCardImg(x, y, productionCards, availableProductionCards.get(i).getId());
            }
        }
    }

    private void setProductionCardImg(int row, int column, GridPane gridPane, int imgId) {
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if (gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                ((ImageView) node).setImage(new Image("img/productionCardsFront/" + imgId + ".png"));
                break;
            }
        }
    }

    public void choosePaymentPopUp(int cardId, int stack) {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(CHOOSE_PAYMENT));
        popUpStage.show();

        NubPlayer player = controller.getPlayer();
        Map<BiElement<Resource, Storage>, Integer> loot = player.getLootchest();
        ResourcesWallet wallet = new ResourcesWallet();
        List<Integer> leaderIds = new ArrayList<>();

        //showing loot status
        loot.forEach((x, y) -> {
            switch (x.getFirstValue()) {
                case COIN -> qtyCoinLblCP.setText("x" + y);
                case SERVANT -> qtyServantLblCP.setText("x" + y);
                case SHIELD -> qtyShieldLblCP.setText("x" + y);
                case STONE -> qtyStoneLblCP.setText("x" + y);
            }
        });

        //set Images in war
        Resource res = player.getResourceFromStorage(WAREHOUSE_SMALL);
        if (res != null) {
            shelf1CP.setImage(new Image("/img/pawns/" + res + ".png"));
        } else {
            shelf1ToggleCP.setDisable(true);
        }

        res = player.getResourceFromStorage(WAREHOUSE_MID);
        int qty = player.getQtyInStorage(res, WAREHOUSE_MID);

        if (res != null && qty == 2) {
            shelf21CP.setImage(new Image("/img/pawns/" + res + ".png"));
            shelf22CP.setImage(new Image("/img/pawns/" + res + ".png"));
        } else if (res != null && qty == 1) {
            shelf21CP.setImage(new Image("/img/pawns/" + res + ".png"));
            shelf22CP.setDisable(true);
        } else {
            shelf21CP.setDisable(true);
            shelf22CP.setDisable(true);
        }

        res = player.getResourceFromStorage(WAREHOUSE_LARGE);
        qty = player.getQtyInStorage(res, WAREHOUSE_LARGE);

        if (res != null) {
            if (qty == 3) {
                shelf31CP.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf32CP.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf33CP.setImage(new Image("/img/pawns/" + res + ".png"));
            } else if (qty == 2) {
                shelf31CP.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf32CP.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf33CP.setDisable(true);
            } else {
                shelf31CP.setImage(new Image("/img/pawns/" + res + ".png"));
                shelf32CP.setDisable(true);
                shelf33CP.setDisable(true);
            }
        } else {
            shelf31CP.setDisable(true);
            shelf32CP.setDisable(true);
            shelf33CP.setDisable(true);
        }

        //choose from warehouse
        List<Resource> fromWar = new ArrayList<>();

        shelf1ToggleCP.setOnAction(event -> {
            if (shelf1ToggleCP.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_SMALL));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_SMALL));
            }
        });
        shelf21ToggleCP.setOnAction(event -> {
            if (shelf21ToggleCP.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_MID));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_MID));
            }
        });
        shelf22ToggleCP.setOnAction(event -> {
            if (shelf22ToggleCP.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_MID));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_MID));
            }
        });
        shelf31ToggleCP.setOnAction(event -> {
            if (shelf31ToggleCP.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_LARGE));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_LARGE));
            }
        });
        shelf32ToggleCP.setOnAction(event -> {
            if (shelf32ToggleCP.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_LARGE));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_LARGE));
            }
        });
        shelf33ToggleCP.setOnAction(event -> {
            if (shelf33ToggleCP.isSelected()) {
                fromWar.add(player.getResourceFromStorage(WAREHOUSE_LARGE));
            } else {
                fromWar.remove(player.getResourceFromStorage(WAREHOUSE_LARGE));
            }
        });

        //choose from loot
        List<Resource> fromLoot = new ArrayList<>();
        stoneAddBtnCP.setOnAction(actionEvent -> {
            stoneLblCP.setText(String.valueOf((Integer.parseInt(stoneLblCP.getText()) + 1)));
            fromLoot.add(STONE);
        });
        stoneSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLblCP.getText()) != 0) {
                stoneLblCP.setText(String.valueOf((Integer.parseInt(stoneLblCP.getText()) - 1)));
                fromLoot.remove(STONE);
            }
        });
        servantAddBtnCP.setOnAction(actionEvent -> {
            servantLblCP.setText(String.valueOf((Integer.parseInt(servantLblCP.getText()) + 1)));
            fromLoot.add(SERVANT);
        });
        servantSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLblCP.getText()) != 0) {
                servantLblCP.setText(String.valueOf((Integer.parseInt(servantLblCP.getText()) - 1)));
                fromLoot.remove(SERVANT);
            }
        });
        coinAddBtnCP.setOnAction(actionEvent -> {
            coinLblCP.setText(String.valueOf((Integer.parseInt(coinLblCP.getText()) + 1)));
            fromLoot.add(COIN);
        });
        coinSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLblCP.getText()) != 0) {
                coinLblCP.setText(String.valueOf((Integer.parseInt(coinLblCP.getText()) - 1)));
                fromLoot.remove(COIN);
            }
        });
        shieldAddBtnCP.setOnAction(actionEvent -> {
            shieldLblCP.setText(String.valueOf((Integer.parseInt(shieldLblCP.getText()) + 1)));
            fromLoot.add(SHIELD);
        });
        shieldSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLblCP.getText()) != 0) {
                shieldLblCP.setText(String.valueOf((Integer.parseInt(shieldLblCP.getText()) - 1)));
                fromLoot.remove(SHIELD);
            }
        });

        //choose stack
        stack1Btn.setOnAction(actionEvent -> {
            wallet.setLootchestTray(fromLoot);
            wallet.setWarehouseTray(fromWar);

            controller.buyProductionCard(cardId, 1, leaderIds, wallet);

            mainBoard.setEffect(null);
            popUpStage.close();
        });

        stack2Btn.setOnAction(actionEvent -> {
            wallet.setLootchestTray(fromLoot);
            wallet.setWarehouseTray(fromWar);

            controller.buyProductionCard(cardId, 2, leaderIds, wallet);

            mainBoard.setEffect(null);
            popUpStage.close();
        });

        stack3Btn.setOnAction(actionEvent -> {
            wallet.setLootchestTray(fromLoot);
            wallet.setWarehouseTray(fromWar);

            controller.buyProductionCard(cardId, 3, leaderIds, wallet);

            mainBoard.setEffect(null);
            popUpStage.close();
        });

        backPaymentBtnCP.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    /* PRODUCE ********************************************************************************************************/
    private void producePopup() {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(PRODUCE));
        popUpStage.show();

        List<ConcreteProductionCard> prodCards = new ArrayList<>();
        ResourcesWallet resWal = new ResourcesWallet();
        AtomicBoolean basicProd = new AtomicBoolean(false);
        List<BoostAbility> leaderCards = new ArrayList<>();
        List<Resource> leaderOutputs = new ArrayList<>();

        if (controller.getPlayer().getProductionStacks().get(0).size() != 0)
            produce2Img.setImage(new Image("/img/productionCardsFront/" + controller.getPlayer().getProductionStacks()
                    .get(0).peekFirst().getId() + ".png"));

        if (controller.getPlayer().getProductionStacks().get(1).size() != 0)
            produce3Img.setImage(new Image("/img/productionCardsFront/" + controller.getPlayer().getProductionStacks()
                    .get(1).peekFirst().getId() + ".png"));

        if (controller.getPlayer().getProductionStacks().get(2).size() != 0)
            produce4Img.setImage(new Image("/img/productionCardsFront/" + controller.getPlayer().getProductionStacks()
                    .get(2).peekFirst().getId() + ".png"));

        produceConfirmBtn.setOnAction(actionEvent -> {
            /*if (produce1Toggle.isSelected()) {
                basicProd.set(true);
            }

            choosePaymentPopUp(0, 0); //da fare una versione customizzata che ritorna resWal e gli viene passato anche basic production da scegliere

            controller.sendProductionMessage(prodCards, resWal, leaderCards, leaderOutputs, basicProd.get(),
                    null, null);*/
        });

        produceBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    /* ACTIVATE & DISCARD LEADER **************************************************************************************/
    private void activateLeader() {
        List<LeaderCard> leaders = controller.getPlayer().getLeaders();
        List<LeaderCard> activable = new ArrayList<>();

        for (LeaderCard leader : leaders) {
            if (!leader.isActive()) {
                activable.add(leader);
            }
        }

        if (leaders.size() == 0) {
            sendToMsgBoard("You don't have any leader cards.");
            return;
        }

        if (activable.size() == 0) {
            sendToMsgBoard("All of your leader cards are already active.");
            return;
        }

        activateLeadPopUp();
    }

    private void activateLeadPopUp() {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(DISCARD_ACTIVATE_LEADER));
        popUpStage.show();

        DALLbl.setText("Select the leader you want to ACTIVATE");

        DAL1Img.setImage(new Image("/img/leaderCards/" + controller.getPlayer().getLeaders().get(0).getId() + ".png"));
        DAL1Toggle.setUserData(controller.getPlayer().getLeaders().get(0).getId());
        DAL2Img.setImage(new Image("/img/leaderCards/" + controller.getPlayer().getLeaders().get(1).getId() + ".png"));
        DAL2Toggle.setUserData(controller.getPlayer().getLeaders().get(0).getId());

        DALConfirmBtn.setOnAction(actionEvent -> {
            controller.sendActivateLeader(chosenLeaderGrp.getSelectedToggle().getUserData().toString());
            mainBoard.setEffect(null);
            popUpStage.close();
        });

        DALBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    private void discardLeader() {
        if (!controller.getPlayer().getLeaders().get(0).isActive() || !controller.getPlayer().getLeaders().get(1).isActive()) {
            List<LeaderCard> leaderCards = new ArrayList<>();
            for (LeaderCard led : controller.getPlayer().getLeaders())
                if(!led.isActive())
                    leaderCards.add(led);

            //discardLeadPopUp(leaderCards);

        } else {
            sendToMsgBoard("You don't have any leader cards to discard!");
        }
    }

    private void discardLeadPopUp() {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(DISCARD_ACTIVATE_LEADER));
        popUpStage.show();

        DALLbl.setText("Select the leader you want to DISCARD");

        DAL1Img.setImage(new Image("/img/leaderCards/" + controller.getPlayer().getLeaders().get(0).getId() + ".png"));
        DAL1Toggle.setUserData(controller.getPlayer().getLeaders().get(0).getId());
        DAL2Img.setImage(new Image("/img/leaderCards/" + controller.getPlayer().getLeaders().get(1).getId() + ".png"));
        DAL2Toggle.setUserData(controller.getPlayer().getLeaders().get(0).getId());

        DALConfirmBtn.setOnAction(actionEvent -> {
            int c = Integer.parseInt(chosenLeaderGrp.getSelectedToggle().getUserData().toString());
            controller.getPlayer().getLeaders().removeIf(led -> led.getId() == c);
            controller.sendDiscardLeader(chosenLeaderGrp.getSelectedToggle().getUserData().toString());
            sendToMsgBoard("You have earned a Faith Point!");
            mainBoard.setEffect(null);
            popUpStage.close();
        });

        DALBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }
}
