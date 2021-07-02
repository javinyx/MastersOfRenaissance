package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ColorEnum;
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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;
import static it.polimi.ingsw.misc.Storage.*;
import static it.polimi.ingsw.model.market.Resource.*;

/**
 * This class is the controller for all the JavaFX scenes used during the actual game phase.
 */
public class GamePhaseHandler extends PhaseHandler {
    /* MAIN ***********************************************************************************************************/
    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();
    @FXML
    private AnchorPane mainBoard;
    @FXML
    private Button endTurnBtn, productionCardsOpen, produceBtn, activateLeaderBtn, discardLeaderBtn, rearrangeBtn;
    @FXML
    private ImageView productionStack11, productionStack12, productionStack13, productionStack21, productionStack22,
            productionStack23, productionStack31, productionStack32, productionStack33, popeFav1, popeFav2, popeFav3,
            extraStorage11ImgMB, extraStorage12ImgMB, extraStorage21ImgMB, extraStorage22ImgMB;
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
    private GridPane marketMarbles, marketMarblesPU;
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
    private ImageView resource1ImgPU, resource2ImgPU, resource3ImgPU, resource4ImgPU, resource5ImgPU, resource6ImgPU,
            bin, extraStorage1Img, extraStorage2Img;
    @FXML
    private Region shelf1PU, shelf21PU, shelf22PU, shelf31PU, shelf32PU, shelf33PU, extraStorage11, extraStorage12,
            extraStorage21, extraStorage22;
    @FXML
    private ImageView shelf1MB, shelf21MB, shelf22MB, shelf31MB, shelf32MB, shelf33MB,
            shelf1ImgPU, shelf21ImgPU, shelf22ImgPU, shelf31ImgPU, shelf32ImgPU, shelf33ImgPU, extraStorage11Img,
            extraStorage12Img, extraStorage21Img, extraStorage22Img;
    @FXML
    private AnchorPane chooseStoragePU;
    @FXML
    private ImageView shelf1CP, shelf21CP, shelf22CP, shelf31CP, shelf32CP, shelf33CP,
            extraStorage11ImgCP, extraStorage12ImgCP, extraStorage21ImgCP, extraStorage22ImgCP;
    @FXML
    private ToggleButton shelf1ToggleCP, shelf21ToggleCP, shelf22ToggleCP, shelf31ToggleCP, shelf32ToggleCP,
            shelf33ToggleCP, extraStorage11CP, extraStorage12CP, extraStorage21CP, extraStorage22CP;
    @FXML
    private VBox extraStorageBox;

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
    @FXML
    private ImageView actionTokenImg;

    /* FAITH TRACK ****************************************************************************************************/
    private List<BiElement<Double, Double>> faithCoords = new ArrayList<>();
    @FXML
    private ImageView redCross;

    /* PRODUCTION CARDS POPUP *****************************************************************************************/
    private int coinQty, shieldQty, servantQty, stoneQty;
    @FXML
    private Label listCostLblCP, qtyStoneLblCP, qtyShieldLblCP, qtyCoinLblCP, qtyServantLblCP, stoneLblCP, servantLblCP,
            coinLblCP, shieldLblCP;
    @FXML
    private Button productionCardBackBtn, stoneSubBtnCP, stoneAddBtnCP, servantSubBtnCP, servantAddBtnCP, coinSubBtnCP,
            coinAddBtnCP, shieldSubBtnCP, shieldAddBtnCP, backPaymentBtnCP, stack1Btn, stack2Btn,
            stack3Btn;
    @FXML
    private GridPane productionCardsGrid;

    /* PRODUCE ********************************************************************************************************/
    private ResourcesWallet resWal;
    private Resource basicOut;
    private boolean isBasicProd;
    @FXML
    private Button produceBackBtn, produceConfirmBtn, BOConfirmBtn;
    @FXML
    private RadioButton stoneToggleBO, servantToggleBO, coinToggleBO, shieldToggleBO;
    @FXML
    private ImageView basicProduceImg, produce2Img, produce3Img, produce4Img;
    @FXML
    private ToggleButton basicProduceToggle, produce2Toggle, produce3Toggle, produce4Toggle;
    @FXML
    private ToggleGroup chosenOutputGrp;
    @FXML
    private Label chooseOutputLbl;

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

    /* USE LEADERS ****************************************************************************************************/
    @FXML
    private Button useLeaderBtn;
    @FXML
    private ImageView useLeader1Img, useLeader2Img;
    @FXML
    private ToggleButton useLeader1Toggle, useLeader2Toggle;
    @FXML
    private ChoiceBox useLeader1Choice, useLeader2Choice;
    @FXML
    private HBox useLeaderMarbleChoices;
    @FXML
    private Label useLeader1Lbl, useLeader2Lbl, useLeaderChooseLbl;

    /* MSG POPUP ******************************************************************************************************/
    @FXML
    private Label msg1Lbl, msg2Lbl;
    @FXML
    private Button msgOkBtn;

    /* END ************************************************************************************************************/
    @FXML
    private Label endStatusLbl, winnerLbl;


    public GamePhaseHandler(GuiController controller, Stage stage) {
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(MAIN_BOARD, MARKET, STORAGE, OTHER_PLAYERS,
                PRODUCTION_CARDS, CHOOSE_PAYMENT, CHOOSE_LEADERS, PRODUCE, DISCARD_ACTIVATE_LEADER, BASIC_OUTPUT,
                USE_LEADER, MSG, END));
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

    /**
     * @param sceneName the scene name
     * @return true if the scene has been set correctly, false if {@link GamePhaseHandler} is not a controller for that scene
     */
    public boolean setScene(ScenesEnum sceneName) {
        if (!sceneMap.containsKey(sceneName)) {
            return false;
        }
        stage.setScene(sceneMap.get(sceneName));
        return true;
    }

    /* MAIN BOARD *****************************************************************************************************/

    /**
     * Initiate the main board upon starting the game and then starts to listen for player's input.
     * <ul>
     * <li>Set the leaders held by the player;</li>
     * <li>Set the initial warehouse;</li>
     * <li>Set the market and the available Development cards;</li>
     * <li>Set the preview of the enemies;</li>
     * <li>Initiate the player's Faith Track</li>
     * </ul>
     */
    public void initiateBoard() {
        setLeaders();
        setWarehouse();
        setMarket();
        setProductionCards();
        setEnemyBoard();
        initiateFaithTrack();
        setFaithTrack();
        initiateGuiElements();
        observePlayerActions();
    }

    /**
     * Initiate the main board upon starting the game and then starts to listen for player's input.
     * <ul>
     * <li>Update the leaders held by the player;</li>
     * <li>Update warehouse;</li>
     * <li>Set the market and the available Development cards;</li>
     * <li>Update the preview of the enemies;</li>
     * <li>Update the player's Faith Track</li>
     * <li>Update the pope passes status</li>
     * <li>Update the Development cards status</li>
     * </ul>
     */
    public void updateBoard() {
        setLeaders();
        setWarehouse();
        setMarket();
        setLootChest();
        setProductionCards();
        setEnemyBoard();
        setFaithTrack();
        setPopeFavours();
        setStacks();
        observePlayerActions();
    }

    /**
     * Set all the behaviors in response to a player input from the main board.
     * <p>
     * Furthermore, it informs the players about their turn through the message box.
     * </p>
     */
    public void observePlayerActions() {
        msgBoard.clear();
        if (controller.getPlayer().isMyTurn()) {
            if (controller.getNormalTurn()) {
                sendToMsgBoard("It is now your turn, please either buy from market, buy development cards or" +
                        " activate production.");
            } else {
                sendToMsgBoard("You can't do a main action anymore but you can browse. When you're ready, please end" +
                        "your turn.");
            }
        } else {
            sendToMsgBoard("It is not your turn yet, please wait for the other players.");
        }

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
                producePopUp();
            } else {
                sendToMsgBoard("You have already completed a main action for this turn," +
                        "please end your turn when you're ready.");
            }
        });

        productionCardsOpen.setOnAction(actionEvent -> {
            setProductionCards();
            productionCardsPopup();
        });

        endTurnBtn.setOnAction(actionEvent -> {
            tmpRes.clear();
            controller.passTurn();
        });

        rearrangeBtn.setOnAction(actionEvent -> rearrangeWarehouse());

        activateLeaderBtn.setOnAction(actionEvent -> activateLeader());

        discardLeaderBtn.setOnAction(actionEvent -> discardLeader());

        player1Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(0)));
        player2Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(1)));
        player3Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(2)));
    }

    private void setLeaders() {
        leader1Show.setImage(null);
        leader2Show.setImage(null);
        leader1Show.setEffect(null);
        leader2Show.setEffect(null);

        List<LeaderCard> currLeads = controller.getPlayer().getLeaders();

        //Set leader images
        if (currLeads.size() >= 1) {
            leader1Show.setImage(new Image("/img/leaderCards/" + currLeads.get(0).getId() + ".png"));
            if (!currLeads.get(0).isActive()) {
                leader1Show.setEffect(new SepiaTone(0.6));
            }
        }
        if (currLeads.size() == 2) {
            leader2Show.setImage(new Image("/img/leaderCards/" + currLeads.get(1).getId() + ".png"));
            if (!currLeads.get(1).isActive()) {
                leader2Show.setEffect(new SepiaTone(0.6));
            }
        }

        int storageCtr = 0;

        //Count how many Storage leaders there are
        for (LeaderCard l : currLeads) {
            if (l instanceof StorageAbility) {
                storageCtr++;
            }
        }

        //Set the resources to anchor to their corresponding leader image
        if (storageCtr == 1 && currLeads.get(0) instanceof StorageAbility) {
            extraStorage11ImgMB.setLayoutX(leader1Show.getLayoutX() + 25.0);
            extraStorage11ImgMB.setLayoutY(leader1Show.getLayoutY() + 140.0);
            extraStorage12ImgMB.setLayoutX(leader1Show.getLayoutX() + 65.0);
            extraStorage12ImgMB.setLayoutY(leader1Show.getLayoutY() + 140.0);
        } else if (storageCtr == 1 && currLeads.get(1) instanceof StorageAbility) {
            extraStorage11ImgMB.setLayoutX(leader2Show.getLayoutX() + 25.0);
            extraStorage11ImgMB.setLayoutY(leader2Show.getLayoutY() + 140.0);
            extraStorage12ImgMB.setLayoutX(leader2Show.getLayoutX() + 25.0);
            extraStorage12ImgMB.setLayoutY(leader2Show.getLayoutY() + 140.0);
        } else if (storageCtr == 2) {
            extraStorage11ImgMB.setLayoutX(leader1Show.getLayoutX() + 25.0);
            extraStorage11ImgMB.setLayoutY(leader1Show.getLayoutY() + 140.0);
            extraStorage12ImgMB.setLayoutX(leader1Show.getLayoutX() + 65.0);
            extraStorage12ImgMB.setLayoutY(leader1Show.getLayoutY() + 140.0);

            extraStorage21ImgMB.setLayoutX(leader2Show.getLayoutX() + 25.0);
            extraStorage21ImgMB.setLayoutY(leader2Show.getLayoutY() + 140.0);
            extraStorage22ImgMB.setLayoutX(leader2Show.getLayoutX() + 25.0);
            extraStorage22ImgMB.setLayoutY(leader2Show.getLayoutY() + 140.0);
        }
    }

    private void initiateFaithTrack() {
        double x = 177;
        double y = 272;

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

    private void initiateGuiElements() {
        useLeader1Choice.getItems().addAll(1, 2, 3, 4);
        useLeader2Choice.getItems().addAll(1, 2, 3, 4);

        stoneToggleBO.setUserData(STONE);
        servantToggleBO.setUserData(SERVANT);
        coinToggleBO.setUserData(COIN);
        shieldToggleBO.setUserData(SHIELD);

        useLeaderChooseLbl.setWrapText(true);
    }

    private void setFaithTrack() {
        int currPos = controller.getPlayer().getCurrPos();
        redCross.setLayoutX(faithCoords.get(currPos).getFirstValue());
        redCross.setLayoutY(faithCoords.get(currPos).getSecondValue());
    }

    private void setPopeFavours() {
        List<ImageView> popeFavList = new ArrayList<>(Arrays.asList(popeFav1, popeFav2, popeFav3));
        List<Integer> passes = controller.getPopeFavours();

        for (int i = 0; i < passes.size(); i++) {
            switch (passes.get(i)) {
                case 0 -> popeFavList.get(i).setImage(new Image("/img/ui/pope" + (i + 2) + ".png"));
                case 1 -> popeFavList.get(i).setImage(null);
            }
        }
    }

    private void resetWarehouse() {
        shelf1MB.setDisable(false);
        shelf21MB.setDisable(false);
        shelf22MB.setDisable(false);
        shelf31MB.setDisable(false);
        shelf32MB.setDisable(false);
        shelf33MB.setDisable(false);

        shelf1MB.setImage(null);
        shelf21MB.setImage(null);
        shelf22MB.setImage(null);
        shelf31MB.setImage(null);
        shelf32MB.setImage(null);
        shelf33MB.setImage(null);

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

        extraStorage11.setDisable(false);
        extraStorage12.setDisable(false);
        extraStorage21.setDisable(false);
        extraStorage22.setDisable(false);

        extraStorage11Img.setImage(null);
        extraStorage12Img.setImage(null);
        extraStorage21Img.setImage(null);
        extraStorage22Img.setImage(null);

        extraStorage11ImgMB.setImage(null);
        extraStorage12ImgMB.setImage(null);
        extraStorage21ImgMB.setImage(null);
        extraStorage22ImgMB.setImage(null);
    }

    private void setWarehouse() {
        resetWarehouse();

        Resource r = controller.getPlayer().getResourceFromStorage(EXTRA1);
        if (r != null) {
            int extra1 = controller.getPlayer().getQtyInStorage(r, EXTRA1);
            if (extra1 >= 1) {
                extraStorage11Img.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage11Img.setLayoutX(extraStorage11.getLayoutX() + 5);
                extraStorage11Img.setLayoutY(extraStorage11.getLayoutY() + 5);
                extraStorage11ImgMB.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage11.setDisable(true);
            } else {
                extraStorage11Img.setImage(null);
                extraStorage11ImgMB.setImage(null);
                extraStorage11.setDisable(false);
            }
            if (extra1 == 2) {
                extraStorage12Img.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage12Img.setLayoutX(extraStorage12.getLayoutX() + 5);
                extraStorage12Img.setLayoutY(extraStorage12.getLayoutY() + 5);
                extraStorage12ImgMB.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage12.setDisable(true);
            } else {
                extraStorage22Img.setImage(null);
                extraStorage22ImgMB.setImage(null);
                extraStorage22.setDisable(false);
            }
        }

        r = controller.getPlayer().getResourceFromStorage(EXTRA2);
        if (r != null) {
            int extra2 = controller.getPlayer().getQtyInStorage(r, EXTRA2);
            if (extra2 >= 1) {
                extraStorage21Img.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage21Img.setLayoutX(extraStorage21.getLayoutX() + 5);
                extraStorage21Img.setLayoutY(extraStorage21.getLayoutY() + 5);
                extraStorage21ImgMB.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage21.setDisable(true);
            } else {
                extraStorage21Img.setImage(null);
                extraStorage21ImgMB.setImage(null);
                extraStorage21.setDisable(false);
            }
            if (extra2 == 2) {
                extraStorage22Img.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage22Img.setLayoutX(extraStorage22.getLayoutX() + 5);
                extraStorage22Img.setLayoutY(extraStorage22.getLayoutY() + 5);
                extraStorage22ImgMB.setImage(new Image("/img/pawns/" + r.toString().toLowerCase() + ".png"));
                extraStorage22.setDisable(true);
            } else {
                extraStorage22Img.setImage(null);
                extraStorage22ImgMB.setImage(null);
                extraStorage22.setDisable(false);
            }
        }

        Map<BiElement<Resource, Storage>, Integer> wh = controller.getPlayer().getWarehouse();
        wh.forEach((x, y) -> {
            Image img = new Image("/img/pawns/" + x.getFirstValue().toString().toLowerCase() + ".png");
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

    private void rearrangeWarehouse() {
        resetWarehouse();
        List<Resource> elem = new ArrayList<>();

        for (BiElement<Resource, Storage> res : controller.getPlayer().getAllResources().keySet()) {
            if (res.getSecondValue().equals(Storage.WAREHOUSE_SMALL) ||
                    res.getSecondValue().equals(Storage.WAREHOUSE_MID) ||
                    res.getSecondValue().equals(Storage.WAREHOUSE_LARGE)) {
                for (int i = 0; i < controller.getPlayer().getAllResources().get(res); i++) {
                    elem.add(res.getFirstValue());
                }
            }
        }

        chooseStoragePopUp(elem, false, true);
    }

    private void resetLootChest() {
        coinLblLC.setText("x0");
        shieldLblLC.setText("x0");
        stoneLblLC.setText("x0");
        servantLblLC.setText("x0");
    }

    private void setLootChest() {
        resetLootChest();

        Map<BiElement<Resource, Storage>, Integer> lc = controller.getPlayer().getLootchest();
        lc.forEach((x, y) -> {
            switch (x.getFirstValue()) {
                case COIN -> {
                    coinLblLC.setText("x" + y);
                }
                case SHIELD -> {
                    shieldLblLC.setText("x" + y);
                }
                case STONE -> {
                    stoneLblLC.setText("x" + y);
                }
                case SERVANT -> {
                    servantLblLC.setText("x" + y);
                }
            }
        });
    }

    private void setStacks() {
        List<ConcreteProductionCard> tstack;

        for (int i = 0; i < 3; i++) {
            tstack = new ArrayList<>(controller.getPlayer().getProductionStacks().get(i));
            Collections.reverse(tstack);
            for (int j = 0, curr = (i * 3) + j; j < tstack.size(); j++, curr++) {
                switch (curr) {
                    case 0 -> productionStack11.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 1 -> productionStack12.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 2 -> productionStack13.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 3 -> productionStack21.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 4 -> productionStack22.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 5 -> productionStack23.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 6 -> productionStack31.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 7 -> productionStack32.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                    case 8 -> productionStack33.setImage(new Image("/img/productionCardsFront/" +
                            tstack.get(j).getId() + ".png"));
                }
            }
        }
    }

    /* MARKET & MARKET POPUP ******************************************************************************************/

    /**
     * Fill the market region with the current resources as set in {@link it.polimi.ingsw.client.ClientController}
     */
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

        List<LeaderCard> active = controller.getPlayer().getLeaders().stream().filter(LeaderCard::isActive)
                .collect(Collectors.toList());
        List<LeaderCard> toPick = new ArrayList<>();
        for (LeaderCard lead : active) {
            if (lead instanceof MarbleAbility) {
                toPick.add(lead);
            }
        }

        marketRow1Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('r', 1, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('r', 1, toPick);
            }
        });
        marketRow2Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('r', 2, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('r', 2, toPick);
            }
        });
        marketRow3Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('r', 3, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('r', 3, toPick);
            }
        });
        marketCol1Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('c', 1, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('c', 1, toPick);
            }
        });
        marketCol2Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('c', 2, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('c', 2, toPick);
            }
        });
        marketCol3Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('c', 3, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('c', 3, toPick);
            }
        });
        marketCol4Btn.setOnMouseClicked(mouseEvent -> {
            if (toPick.isEmpty()) {
                controller.sendBuyMarketMessage('c', 4, null);
                mainBoard.setEffect(null);
                popUpStage.close();
            } else {
                popUpStage.close();
                useMarbleLeader('c', 4, toPick);
            }
        });

        marketBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    private void useMarbleLeader(char dim, int index, List<LeaderCard> toPick) {
        resetUseLeader();
        useLeaderMarbleChoices.setVisible(true);
        useLeader1Choice.setVisible(false);
        useLeader1Lbl.setVisible(false);
        useLeader2Choice.setVisible(false);
        useLeader2Lbl.setVisible(false);

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(USE_LEADER));
        popUpStage.show();

        if (toPick.size() >= 1) {
            useLeader1Img.setImage(new Image("/img/leaderCards/" + toPick.get(0).getId() + ".png"));
            useLeader1Toggle.setUserData(toPick.get(0));
            useLeader1Choice.setVisible(true);
            useLeader1Lbl.setVisible(true);
        }
        if (toPick.size() == 2) {
            useLeader2Img.setImage(new Image("/img/leaderCards/" + toPick.get(1).getId() + ".png"));
            useLeader2Toggle.setUserData(toPick.get(1));
            useLeader2Choice.setVisible(true);
            useLeader2Lbl.setVisible(true);
        }

        useLeaderBtn.setOnAction(actionEvent -> {
            if (useLeader1Toggle.isSelected() || useLeader2Toggle.isSelected()) {
                List<BiElement<MarbleAbility, Integer>> marbleChoice = new ArrayList<>();
                if (useLeader1Toggle.isSelected()) {
                    BiElement<MarbleAbility, Integer> leader1Info = new BiElement<>
                            ((MarbleAbility) useLeader1Toggle.getUserData(), (Integer) useLeader1Choice.getValue());
                    marbleChoice.add(leader1Info);
                }
                if (useLeader2Toggle.isSelected()) {
                    BiElement<MarbleAbility, Integer> leader2Info = new BiElement<>
                            ((MarbleAbility) useLeader2Toggle.getUserData(), (Integer) useLeader2Choice.getValue());
                    marbleChoice.add(leader2Info);
                }
                controller.sendBuyMarketMessage(dim, index, marbleChoice);
            } else {
                controller.sendBuyMarketMessage(dim, index, null);
            }

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
            playersFaithImgs.get(0).setImage(new Image("/img/pawns/blackCrossNew.png"));
            ctr++;
        } else {
            for (NubPlayer p : controller.getOtherPlayers()) {
                playersBtns.get(ctr).setText(p.getNickname() + "(#" + p.getTurnNumber() + ")");
                playersFaithLbls.get(ctr).setText(String.valueOf(p.getCurrPos()));
                ctr++;
            }
        }

        //if the game is not of size 4, then hide the other buttons
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
            opLead1Img.setImage(new Image("/img/leaderCards/" + (lead1.isActive() ? String.valueOf(lead1.getId())
                    : "leaderBack") + ".png"));
        }
        if (player.getLeaders().size() > 1) {
            LeaderCard lead2 = player.getLeaders().get(1);
            opLead2Img.setImage(new Image("/img/leaderCards/" + (lead2.isActive() ? String.valueOf(lead2.getId())
                    : "leaderBack") + ".png"));
        }

        int cardCount = 0;
        List<Deque<ConcreteProductionCard>> prodStacks = player.getProductionStacks();
        if (prodStacks != null && prodStacks.size() >= 1) {
            Deque<ConcreteProductionCard> stack1 = player.getProductionStack(0);
            if (stack1.size() > 0) {
                opStack1Img.setVisible(true);
                opStack1Img.setImage(new Image("/img/productionCardsFront/" + stack1.peekFirst().getId() + ".png"));
                cardCount += stack1.size();
            } else {
                opStack1Img.setVisible(false);
            }
        }
        if (prodStacks.size() >= 2) {
            Deque<ConcreteProductionCard> stack2 = player.getProductionStack(1);
            if (stack2.size() > 0) {
                opStack2Img.setVisible(true);
                opStack2Img.setImage(new Image("/img/productionCardsFront/" + stack2.peekFirst().getId() + ".png"));
                cardCount += stack2.size();
            } else {
                opStack2Img.setVisible(false);
            }
        }
        if (prodStacks.size() == 3) {
            Deque<ConcreteProductionCard> stack3 = player.getProductionStack(2);
            if (stack3.size() > 0) {
                opStack3Img.setVisible(true);
                opStack3Img.setImage(new Image("/img/productionCardsFront/" + stack3.peekFirst().getId() + ".png"));
                cardCount += stack3.size();
            } else {
                opStack3Img.setVisible(false);
            }
        }
        opTotalProdCardsLbl.setText("Total development cards: " + cardCount);

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

    /**
     * @param message info message that has to be displayed in the Message Box
     */
    public void sendToMsgBoard(String message) {
        msgBoard.setStyle("");
        msgBoard.appendText(message + '\n');
    }

    /**
     * It will show the error message to the player through the Message Box. The text will turn red.
     *
     * @param message error message that has to be displayed in the Message Box
     */
    public void sendErrorToMsgBoard(String message) {
        msgBoard.setStyle("-fx-text-fill: red");
        msgBoard.appendText(message + '\n');
    }

    /**
     * It display the image of the token {@code act} on the right side of the player's board.
     *
     * @param act the Action Token drawn during the last Lorenzo's turn
     */
    public void setActionToken(ActionToken act) {
        switch (act.getId()) {
            case 1 -> actionTokenImg.setImage(new Image("/img/actionTokens/actTokenGreen.png"));
            case 2 -> actionTokenImg.setImage(new Image("/img/actionTokens/actTokenPurple.png"));
            case 3 -> actionTokenImg.setImage(new Image("/img/actionTokens/actTokenBlue.png"));
            case 4 -> actionTokenImg.setImage(new Image("/img/actionTokens/actTokenYellow.png"));
            case 5, 6 -> actionTokenImg.setImage(new Image("/img/actionTokens/actTokenDoubleMove.png"));
            case 7 -> actionTokenImg.setImage(new Image("/img/actionTokens/actTokenShuffle.png"));
        }
    }

    /* CHOOSE STORAGE POPUP *******************************************************************************************/

    /**
     * It will open the pop-up that allows the player to place the resources in {@code selectedRes} into the Warehouse's shelves.
     *
     * @param selectedRes         the list of resources that needs to be stored by the player
     * @param isBadStorageRequest true if a {@link it.polimi.ingsw.exception.BadStorageException} has been thrown by the server
     *                            sending a {@code BAD_STORAGE_REQUEST} and the resources need to be rearranged again
     * @param isRearrangeRequest  true if the player wants to rearrange the resources he/she already has in the Warehouse
     */
    public void chooseStoragePopUp(List<Resource> selectedRes, boolean isBadStorageRequest, boolean isRearrangeRequest) {
        mainBoard.setEffect(new GaussianBlur());
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(STORAGE));
        popUpStage.show();

        List<LeaderCard> active = controller.getPlayer().getLeaders().stream().filter(LeaderCard::isActive)
                .collect(Collectors.toList());
        List<StorageAbility> toPick = new ArrayList<>();
        for (LeaderCard lead : active) {
            if (lead instanceof StorageAbility) {
                toPick.add((StorageAbility) lead);
            }
        }

        List<BiElement<Resource, Storage>> tbdResourcePlacement = new ArrayList<>();

        if (!isBadStorageRequest) {
            tmpRes.addAll(selectedRes);

            if (selectedRes.size() >= 1)
                resource1ImgPU.setImage(new Image("/img/pawns/" + selectedRes.get(0).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() >= 2)
                resource2ImgPU.setImage(new Image("/img/pawns/" + selectedRes.get(1).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() >= 3)
                resource3ImgPU.setImage(new Image("/img/pawns/" + selectedRes.get(2).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() >= 4)
                resource4ImgPU.setImage(new Image("/img/pawns/" + selectedRes.get(3).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() >= 5)
                resource5ImgPU.setImage(new Image("/img/pawns/" + selectedRes.get(4).toString().toLowerCase() +
                        ".png"));
            if (selectedRes.size() == 6)
                resource6ImgPU.setImage(new Image("/img/pawns/" + selectedRes.get(5).toString().toLowerCase() +
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

            resource5ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(4));
            });
            resource5ImgPU.setOnDragDone(this::sourceDragDone);

            resource6ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(5));
            });
            resource6ImgPU.setOnDragDone(this::sourceDragDone);

        } else {
            if (tmpRes.size() >= 1)
                resource1ImgPU.setImage(new Image("/img/pawns/" + tmpRes.get(0).toString().toLowerCase() + ".png"));
            if (tmpRes.size() >= 2)
                resource2ImgPU.setImage(new Image("/img/pawns/" + tmpRes.get(1).toString().toLowerCase() + ".png"));
            if (tmpRes.size() >= 3)
                resource3ImgPU.setImage(new Image("/img/pawns/" + tmpRes.get(2).toString().toLowerCase() + ".png"));
            if (tmpRes.size() >= 4)
                resource4ImgPU.setImage(new Image("/img/pawns/" + tmpRes.get(3).toString().toLowerCase() + ".png"));
            if (tmpRes.size() >= 5)
                resource5ImgPU.setImage(new Image("/img/pawns/" + tmpRes.get(4).toString().toLowerCase() + ".png"));
            if (tmpRes.size() == 6)
                resource6ImgPU.setImage(new Image("/img/pawns/" + tmpRes.get(5).toString().toLowerCase() + ".png"));

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

            resource5ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, tmpRes.get(4));
            });
            resource5ImgPU.setOnDragDone(this::sourceDragDone);

            resource6ImgPU.setOnDragDetected(event -> {
                sourceDragDetected(event, tmpRes.get(5));
            });
            resource6ImgPU.setOnDragDone(this::sourceDragDone);
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

        if (toPick.size() >= 1) {
            extraStorage1Img.setImage(new Image("/img/ui/" + toPick.get(0).getStorageType().toString().toLowerCase()
                    + "Storage.png"));
            extraStorage11.setOnDragOver(this::targetDragOver);
            extraStorage11.setOnDragDropped(event -> {
                tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.EXTRA1));
            });
            extraStorage12.setOnDragOver(this::targetDragOver);
            extraStorage12.setOnDragDropped(event -> {
                tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.EXTRA1));
            });
        }
        if (toPick.size() == 2) {
            extraStorage2Img.setImage(new Image("/img/ui/" + toPick.get(1).getStorageType().toString().toLowerCase()
                    + "Storage.png"));
            extraStorage21.setOnDragOver(this::targetDragOver);
            extraStorage21.setOnDragDropped(event -> {
                tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.EXTRA2));
            });
            extraStorage22.setOnDragOver(this::targetDragOver);
            extraStorage22.setOnDragDropped(event -> {
                tbdResourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.EXTRA2));
            });
        }

        if (isRearrangeRequest) {
            bin.setDisable(true);
            bin.setVisible(false);
        } else {
            bin.setDisable(false);
            bin.setVisible(true);
            bin.setOnDragOver(this::targetDragOver);
            bin.setOnDragDropped(event -> {
                tbdResourcePlacement.add(new BiElement<>(discardDragDropped(event), Storage.DISCARD));
            });
        }

        chooseStorageBtnPU.setOnAction(actionEvent -> {
            if (!isBadStorageRequest) {
                if (tbdResourcePlacement.size() == selectedRes.size()) {
                    mainBoard.setEffect(null);
                    popUpStage.close();
                    resetStoragePopUp();
                    setWarehouse();

                    if (isRearrangeRequest) {
                        controller.sendRearrangeMessage(tbdResourcePlacement);
                    } else {
                        controller.sendPlaceResourcesMessage(tbdResourcePlacement);
                    }
                }
            } else {
                if (tbdResourcePlacement.size() == tmpRes.size()) {
                    mainBoard.setEffect(null);
                    popUpStage.close();
                    resetStoragePopUp();
                    setWarehouse();

                    if (isRearrangeRequest) {
                        controller.sendRearrangeMessage(tbdResourcePlacement);
                    } else {
                        controller.sendPlaceResourcesMessage(tbdResourcePlacement);
                    }
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

        resource5ImgPU.setLayoutX(59);
        resource5ImgPU.setLayoutY(117);
        resource5ImgPU.setDisable(false);
        resource5ImgPU.setImage(null);

        resource6ImgPU.setLayoutX(59);
        resource6ImgPU.setLayoutY(167);
        resource6ImgPU.setDisable(false);
        resource6ImgPU.setImage(null);

        shelf1PU.setDisable(false);
        shelf21PU.setDisable(false);
        shelf22PU.setDisable(false);
        shelf31PU.setDisable(false);
        shelf32PU.setDisable(false);
        shelf33PU.setDisable(false);

        extraStorage11.setDisable(false);
        extraStorage12.setDisable(false);
        extraStorage21.setDisable(false);
        extraStorage22.setDisable(false);

        shelf1ImgPU.setImage(null);
        shelf21ImgPU.setImage(null);
        shelf22ImgPU.setImage(null);
        shelf31ImgPU.setImage(null);
        shelf32ImgPU.setImage(null);
        shelf33ImgPU.setImage(null);

        extraStorage11Img.setImage(null);
        extraStorage12Img.setImage(null);
        extraStorage21Img.setImage(null);
        extraStorage22Img.setImage(null);
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

        productionCardsGrid.setOnMouseClicked(mouseEvent -> {
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
        if (clickedNode != productionCardsGrid) {
            String url = ((ImageView) clickedNode).getImage().getUrl();
            String cardIdS = url.substring(url.lastIndexOf("/") + 1).split("\\.")[0];
            Integer cardId = Integer.parseInt(cardIdS);

            popUpStage.close();

            // Check if player has active Discount Ability Leader Cards
            List<LeaderCard> active = controller.getPlayer().getLeaders().stream().filter(LeaderCard::isActive)
                    .collect(Collectors.toList());
            List<LeaderCard> toPick = new ArrayList<>();
            for (LeaderCard lead : active) {
                if (lead instanceof DiscountAbility) {
                    toPick.add(lead);
                }
            }
            if (toPick.size() > 0) {
                useDiscountLeader(cardId, toPick);
            } else {
                choosePaymentPopUp(cardId, new ArrayList<>());
            }
        }
    }

    private void resetUseLeader() {
        useLeader1Toggle.setSelected(false);
        useLeader2Toggle.setSelected(false);
        useLeader1Toggle.setUserData(null);
        useLeader2Toggle.setUserData(null);

        useLeader1Img.setImage(null);
        useLeader2Img.setImage(null);

        useLeaderMarbleChoices.setVisible(false);
    }

    private void useDiscountLeader(int prodCardId, List<LeaderCard> toPick) {
        resetUseLeader();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(USE_LEADER));
        popUpStage.show();

        if (toPick.size() >= 1) {
            useLeader1Img.setImage(new Image("/img/leaderCards/" + toPick.get(0).getId() + ".png"));
            useLeader1Toggle.setUserData(toPick.get(0).getId());
        }
        if (toPick.size() == 2) {
            useLeader2Img.setImage(new Image("/img/leaderCards/" + toPick.get(1).getId() + ".png"));
            useLeader2Toggle.setUserData(toPick.get(1).getId());
        }

        useLeaderBtn.setOnAction(actionEvent -> {
            if (useLeader1Toggle.isSelected() || useLeader2Toggle.isSelected()) {
                List<Integer> leaderIds = new ArrayList<>();

                if (useLeader1Toggle.isSelected()) {
                    leaderIds.add((Integer) useLeader1Toggle.getUserData());
                }
                if (useLeader2Toggle.isSelected()) {
                    leaderIds.add((Integer) useLeader2Toggle.getUserData());
                }
                popUpStage.close();
                choosePaymentPopUp(prodCardId, leaderIds);
            } else {
                popUpStage.close();
                choosePaymentPopUp(prodCardId, new ArrayList<>());
            }
        });
    }

    /**
     * Place all the still available Development cards to purchase in the grid. The cards are chosen as the
     * version represented in the {@code ClientController}.
     * <p>The grid is divided into 12 decks, each of 4 cards. Once a deck is empty, it replaces the image with a back card image.</p>
     */
    public void setProductionCards() {
        ConcreteProductionCard[] availableProductionCards = {null, null, null, null, null, null, null, null, null,
                null, null, null};
        for (int i = 0, j = 0; i < 12; i++, j++) {
            switch (i) {
                case 0 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 1 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.GREEN))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 1 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 1 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.PURPLE))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 2 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 1 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.BLUE))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 3 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 1 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.YELLOW))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 4 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 2 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.GREEN))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 5 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 2 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.PURPLE))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 6 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 2 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.BLUE))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 7 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 2 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.YELLOW))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 8 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 3 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.GREEN))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 9 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 3 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.PURPLE))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 10 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 3 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.BLUE))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
                case 11 -> {
                    if (controller.getAvailableProductionCards().get(j).getLevel() == 3 && controller
                            .getAvailableProductionCards().get(j).getColor().equals(ColorEnum.YELLOW))
                        availableProductionCards[i] = controller.getAvailableProductionCards().get(j);
                    else {
                        availableProductionCards[i] = null;
                        j--;
                    }
                }
            }
        }
        for (int x = 0, i = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++, i++) {
                setProductionCardImg(x, y, productionCardsGrid, availableProductionCards[i]);
            }
        }
    }

    private void setProductionCardImg(int row, int column, GridPane productionCardsGrid,
                                      ConcreteProductionCard prodCard) {
        ObservableList<Node> children = productionCardsGrid.getChildren();

        for (Node node : children) {
            if (productionCardsGrid.getRowIndex(node) == row && productionCardsGrid.getColumnIndex(node) == column) {
                if (prodCard != null) {
                    ((ImageView) node).setImage(new Image("/img/productionCardsFront/" + prodCard.getId() + ".png"));
                } else {
                    switch (column) {
                        case 0 -> {
                            switch (row) {
                                case 0 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/green1.png"));
                                case 1 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/green2.png"));
                                case 2 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/green3.png"));
                            }
                        }
                        case 1 -> {
                            switch (column) {
                                case 0 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/purple1.png"));
                                case 1 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/purple2.png"));
                                case 2 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/purple3.png"));
                            }
                        }
                        case 2 -> {
                            switch (column) {
                                case 0 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/blue1.png"));
                                case 1 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/blue2.png"));
                                case 2 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/blue3.png"));
                            }
                        }
                        case 3 -> {
                            switch (column) {
                                case 0 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/yellow1.png"));
                                case 1 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/yellow2.png"));
                                case 2 -> ((ImageView) node).setImage(new Image("/img/productionCardsBack/yellow3.png"));
                            }
                        }
                    }
                    node.setDisable(true);
                }
                break;
            }
        }
    }

    private void resetChoosePayment() {
        shelf1CP.setImage(null);
        shelf21CP.setImage(null);
        shelf22CP.setImage(null);
        shelf31CP.setImage(null);
        shelf32CP.setImage(null);
        shelf33CP.setImage(null);

        extraStorage11ImgCP.setImage(null);
        extraStorage12ImgCP.setImage(null);
        extraStorage21ImgCP.setImage(null);
        extraStorage22ImgCP.setImage(null);

        shelf1ToggleCP.setSelected(false);
        shelf21ToggleCP.setSelected(false);
        shelf22ToggleCP.setSelected(false);
        shelf31ToggleCP.setSelected(false);
        shelf32ToggleCP.setSelected(false);
        shelf33ToggleCP.setSelected(false);

        extraStorage11CP.setSelected(false);
        extraStorage12CP.setSelected(false);
        extraStorage21CP.setSelected(false);
        extraStorage22CP.setSelected(false);

        qtyServantLblCP.setText("x0");
        qtyCoinLblCP.setText("x0");
        qtyShieldLblCP.setText("x0");
        qtyStoneLblCP.setText("x0");

        stoneLblCP.setText("0");
        coinLblCP.setText("0");
        servantLblCP.setText("0");
        shieldLblCP.setText("0");

        coinQty = 0;
        shieldQty = 0;
        servantQty = 0;
        stoneQty = 0;

        resWal = new ResourcesWallet();
        stack1Btn.setVisible(true);
        stack1Btn.setDisable(false);
        stack3Btn.setVisible(true);
        stack3Btn.setDisable(false);
        stack2Btn.setText("Stack 2");

        extraStorageBox.setVisible(true);
        extraStorageBox.setManaged(true);
    }

    private void choosePaymentPopUp(int cardId, List<Integer> leaderIds) {
        resetChoosePayment();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(CHOOSE_PAYMENT));
        popUpStage.show();

        NubPlayer player = controller.getPlayer();
        Map<BiElement<Resource, Storage>, Integer> loot = player.getLootchest();
        ResourcesWallet wallet = new ResourcesWallet();

        //set resources needed label
        String cost = new String();
        for (ConcreteProductionCard c : controller.getAvailableProductionCards()) {
            if (c.getId() == cardId) {
                cost = c.getCost().toString();
                break;
            }
        }
        listCostLblCP.setWrapText(true);
        listCostLblCP.setText(cost);

        //showing loot status
        loot.forEach((x, y) -> {
            switch (x.getFirstValue()) {
                case COIN -> {
                    qtyCoinLblCP.setText("x" + y);
                    coinQty = y;
                }
                case SERVANT -> {
                    qtyServantLblCP.setText("x" + y);
                    servantQty = y;
                }
                case SHIELD -> {
                    qtyShieldLblCP.setText("x" + y);
                    shieldQty = y;
                }
                case STONE -> {
                    qtyStoneLblCP.setText("x" + y);
                    stoneQty = y;
                }
            }
        });

        //set images in warehouse
        Resource res = player.getResourceFromStorage(WAREHOUSE_SMALL);
        if (res != null) {
            shelf1CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
        }

        res = player.getResourceFromStorage(WAREHOUSE_MID);
        int qty = player.getQtyInStorage(res, WAREHOUSE_MID);

        if (res != null && qty == 2) {
            shelf21CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            shelf22CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
        } else if (res != null && qty == 1) {
            shelf21CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
        }

        res = player.getResourceFromStorage(WAREHOUSE_LARGE);
        qty = player.getQtyInStorage(res, WAREHOUSE_LARGE);

        if (res != null) {
            if (qty == 3) {
                shelf31CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                shelf32CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                shelf33CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            } else if (qty == 2) {
                shelf31CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                shelf32CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            } else {
                shelf31CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            }
        }

        //set images in extra storage
        Resource extraRes1 = player.getResourceFromStorage(EXTRA1);
        Resource extraRes2 = player.getResourceFromStorage(EXTRA2);

        if (extraRes1 != null || extraRes2 != null) {
            qty = player.getQtyInStorage(res, EXTRA1);
            if (extraRes1 != null) {
                if (qty >= 1) {
                    extraStorage11ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
                if (qty == 2) {
                    extraStorage12ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
            }
            qty = player.getQtyInStorage(res, EXTRA2);
            if (extraRes2 != null) {
                if (qty >= 1) {
                    extraStorage21ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
                if (qty == 2) {
                    extraStorage22ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
            }
        } else {
            extraStorageBox.setVisible(false);
            extraStorageBox.setManaged(false);
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

        //choose from extra storage
        List<Resource> fromExtra1 = new ArrayList<>();
        List<Resource> fromExtra2 = new ArrayList<>();

        extraStorage11CP.setOnAction(event -> {
            if (extraStorage11CP.isSelected()) {
                fromExtra1.add(player.getResourceFromStorage(EXTRA1));
            } else {
                fromExtra1.remove(player.getResourceFromStorage(EXTRA1));
            }
        });
        extraStorage12CP.setOnAction(event -> {
            if (extraStorage12CP.isSelected()) {
                fromExtra1.add(player.getResourceFromStorage(EXTRA1));
            } else {
                fromExtra1.remove(player.getResourceFromStorage(EXTRA1));
            }
        });
        extraStorage21CP.setOnAction(event -> {
            if (extraStorage21CP.isSelected()) {
                fromExtra2.add(player.getResourceFromStorage(EXTRA2));
            } else {
                fromExtra2.remove(player.getResourceFromStorage(EXTRA2));
            }
        });
        extraStorage22CP.setOnAction(event -> {
            if (extraStorage22CP.isSelected()) {
                fromExtra2.add(player.getResourceFromStorage(EXTRA2));
            } else {
                fromExtra2.remove(player.getResourceFromStorage(EXTRA2));
            }
        });

        //choose from loot
        List<Resource> fromLoot = new ArrayList<>();
        stoneAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLblCP.getText()) < stoneQty) {
                stoneLblCP.setText(String.valueOf((Integer.parseInt(stoneLblCP.getText()) + 1)));
                fromLoot.add(STONE);
            }
        });
        stoneSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLblCP.getText()) != 0) {
                stoneLblCP.setText(String.valueOf((Integer.parseInt(stoneLblCP.getText()) - 1)));
                fromLoot.remove(STONE);
            }
        });
        servantAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLblCP.getText()) < servantQty) {
                servantLblCP.setText(String.valueOf((Integer.parseInt(servantLblCP.getText()) + 1)));
                fromLoot.add(SERVANT);
            }
        });
        servantSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLblCP.getText()) != 0) {
                servantLblCP.setText(String.valueOf((Integer.parseInt(servantLblCP.getText()) - 1)));
                fromLoot.remove(SERVANT);
            }
        });
        coinAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLblCP.getText()) < coinQty) {
                coinLblCP.setText(String.valueOf((Integer.parseInt(coinLblCP.getText()) + 1)));
                fromLoot.add(COIN);
            }
        });
        coinSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLblCP.getText()) != 0) {
                coinLblCP.setText(String.valueOf((Integer.parseInt(coinLblCP.getText()) - 1)));
                fromLoot.remove(COIN);
            }
        });
        shieldAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLblCP.getText()) < shieldQty) {
                shieldLblCP.setText(String.valueOf((Integer.parseInt(shieldLblCP.getText()) + 1)));
                fromLoot.add(SHIELD);
            }
        });
        shieldSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLblCP.getText()) != 0) {
                shieldLblCP.setText(String.valueOf((Integer.parseInt(shieldLblCP.getText()) - 1)));
                fromLoot.remove(SHIELD);
            }
        });

        //choose stack
        stack1Btn.setOnAction(actionEvent -> {
            if (!(fromLoot.isEmpty() && fromWar.isEmpty())) {
                wallet.setLootchestTray(fromLoot);
                wallet.setWarehouseTray(fromWar);
                wallet.setExtraStorage(fromExtra1, 0);
                wallet.setExtraStorage(fromExtra2, 1);

                controller.sendBuyProductionCard(cardId, 1, leaderIds, wallet);

                mainBoard.setEffect(null);
                popUpStage.close();
            }
        });

        stack2Btn.setOnAction(actionEvent -> {
            if (!(fromLoot.isEmpty() && fromWar.isEmpty())) {
                wallet.setLootchestTray(fromLoot);
                wallet.setWarehouseTray(fromWar);
                wallet.setExtraStorage(fromExtra1, 0);
                wallet.setExtraStorage(fromExtra2, 1);

                controller.sendBuyProductionCard(cardId, 2, leaderIds, wallet);

                mainBoard.setEffect(null);
                popUpStage.close();
            }
        });

        stack3Btn.setOnAction(actionEvent -> {
            if (!(fromLoot.isEmpty() && fromWar.isEmpty())) {
                wallet.setLootchestTray(fromLoot);
                wallet.setWarehouseTray(fromWar);
                wallet.setExtraStorage(fromExtra1, 0);
                wallet.setExtraStorage(fromExtra2, 1);

                controller.sendBuyProductionCard(cardId, 3, leaderIds, wallet);

                mainBoard.setEffect(null);
                popUpStage.close();
            }
        });

        backPaymentBtnCP.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    /* PRODUCE ********************************************************************************************************/
    private void resetProducePopUp() {
        produce2Img.setImage(null);
        produce3Img.setImage(null);
        produce4Img.setImage(null);

        produce2Toggle.setUserData(null);
        produce3Toggle.setUserData(null);
        produce4Toggle.setUserData(null);

        basicProduceToggle.setSelected(false);
        produce2Toggle.setSelected(false);
        produce3Toggle.setSelected(false);
        produce4Toggle.setSelected(false);
    }

    private void producePopUp() {
        resetProducePopUp();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        mainBoard.setEffect(new GaussianBlur());
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(PRODUCE));
        popUpStage.show();

        isBasicProd = false;
        List<ConcreteProductionCard> prodCards = new ArrayList<>();
        List<BoostAbility> leaderCards = new ArrayList<>();
        List<Resource> leaderOutputs = new ArrayList<>();

        if (controller.getPlayer().getProductionStacks().get(0).size() != 0) {
            produce2Img.setImage(new Image("/img/productionCardsFront/" + controller.getPlayer()
                    .getProductionStacks().get(0).peekFirst().getId() + ".png"));
            produce2Toggle.setUserData(controller.getPlayer().getProductionStacks().get(0).peekFirst());
        }

        if (controller.getPlayer().getProductionStacks().get(1).size() != 0) {
            produce3Img.setImage(new Image("/img/productionCardsFront/" + controller.getPlayer()
                    .getProductionStacks().get(1).peekFirst().getId() + ".png"));
            produce3Toggle.setUserData(controller.getPlayer().getProductionStacks().get(1).peekFirst());
        }

        if (controller.getPlayer().getProductionStacks().get(2).size() != 0) {
            produce4Img.setImage(new Image("/img/productionCardsFront/" + controller.getPlayer()
                    .getProductionStacks().get(2).peekFirst().getId() + ".png"));
            produce4Toggle.setUserData(controller.getPlayer().getProductionStacks().get(2).peekFirst());
        }

        // Check if player has active Boost Ability Leader Cards
        List<LeaderCard> active = controller.getPlayer().getLeaders().stream().filter(LeaderCard::isActive)
                .collect(Collectors.toList());
        List<LeaderCard> toPick = new ArrayList<>();
        for (LeaderCard lead : active) {
            if (lead instanceof BoostAbility) {
                toPick.add(lead);
            }
        }

        produceConfirmBtn.setOnAction(actionEvent -> {
            if (basicProduceToggle.isSelected() || produce2Toggle.isSelected() || produce3Toggle.isSelected() ||
                    produce4Toggle.isSelected()) {

                if (basicProduceToggle.isSelected()) {
                    isBasicProd = true;
                }

                if (produce2Toggle.isSelected()) {
                    prodCards.add((ConcreteProductionCard) produce2Toggle.getUserData());
                }
                if (produce3Toggle.isSelected()) {
                    prodCards.add((ConcreteProductionCard) produce3Toggle.getUserData());
                }
                if (produce4Toggle.isSelected()) {
                    prodCards.add((ConcreteProductionCard) produce4Toggle.getUserData());
                }

                popUpStage.close();
                if (toPick.size() > 0) {
                    useBoostLeader(toPick, prodCards);
                } else {
                    if (isBasicProd) {
                        chooseBasicOutput(prodCards, leaderCards, leaderOutputs);
                    } else {
                        choosePaymentPopUp(prodCards, leaderCards, leaderOutputs);
                    }
                }
            }
        });

        produceBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    private void useBoostLeader(List<LeaderCard> toPick, List<ConcreteProductionCard> prodCards) {
        resetUseLeader();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(USE_LEADER));
        popUpStage.show();

        if (toPick.size() >= 1) {
            useLeader1Img.setImage(new Image("/img/leaderCards/" + toPick.get(0).getId() + ".png"));
            useLeader1Toggle.setUserData(toPick.get(0));
        }
        if (toPick.size() == 2) {
            useLeader2Img.setImage(new Image("/img/leaderCards/" + toPick.get(1).getId() + ".png"));
            useLeader2Toggle.setUserData(toPick.get(1));
        }

        List<Resource> leaderOutputs = new ArrayList<>();
        List<BoostAbility> leaderCards = new ArrayList<>();

        useLeaderBtn.setOnAction(actionEvent -> {
            if (useLeader1Toggle.isSelected() || useLeader2Toggle.isSelected()) {
                if (useLeader1Toggle.isSelected()) {
                    leaderCards.add((BoostAbility) useLeader1Toggle.getUserData());
                }
                if (useLeader2Toggle.isSelected()) {
                    leaderCards.add((BoostAbility) useLeader2Toggle.getUserData());
                }
                popUpStage.close();
                chooseLeaderOutput(prodCards, leaderCards, leaderOutputs, leaderCards.size());
            } else {
                popUpStage.close();
                if (isBasicProd) {
                    chooseBasicOutput(prodCards, leaderCards, leaderOutputs);
                } else {
                    choosePaymentPopUp(prodCards, leaderCards, leaderOutputs);
                }
            }

        });
    }

    private void chooseLeaderOutput(List<ConcreteProductionCard> prodCards, List<BoostAbility> leaderCards,
                                    List<Resource> leaderOutputs, int cycle) {
        chooseOutputLbl.setText("Choose leader output");

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(BASIC_OUTPUT));
        popUpStage.show();

        int tempCycle = cycle - 1;

        BOConfirmBtn.setOnAction(actionEvent -> {
            leaderOutputs.add((Resource) chosenOutputGrp.getSelectedToggle().getUserData());

            popUpStage.close();
            if (tempCycle > 0) {
                chooseLeaderOutput(prodCards, leaderCards, leaderOutputs, tempCycle);
            } else if (isBasicProd) {
                chooseBasicOutput(prodCards, leaderCards, leaderOutputs);
            } else {
                choosePaymentPopUp(prodCards, leaderCards, leaderOutputs);
            }
        });
    }

    private void chooseBasicOutput(List<ConcreteProductionCard> prodCards,
                                   List<BoostAbility> leaderCards, List<Resource> leaderOutputs) {
        chooseOutputLbl.setText("Choose basic output");

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(BASIC_OUTPUT));
        popUpStage.show();

        BOConfirmBtn.setOnAction(actionEvent -> {
            basicOut = (Resource) chosenOutputGrp.getSelectedToggle().getUserData();
            popUpStage.close();
            choosePaymentPopUp(prodCards, leaderCards, leaderOutputs);
        });
    }

    private void choosePaymentPopUp(List<ConcreteProductionCard> prodCards, List<BoostAbility> leaderCards,
                                    List<Resource> leaderOutputs) {
        resetChoosePayment();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(CHOOSE_PAYMENT));
        popUpStage.show();

        NubPlayer player = controller.getPlayer();
        Map<BiElement<Resource, Storage>, Integer> loot = player.getLootchest();
        ResourcesWallet wallet = new ResourcesWallet();

        //set resources needed label
        String cost = new String();

        //get cost of production cards
        if (!prodCards.isEmpty()) {
            for (ConcreteProductionCard c : prodCards) {
                cost = cost + " " + c.getRequiredResources();
            }
        }
        //get cost of leaders
        if (!leaderCards.isEmpty()) {
            for (BoostAbility c : leaderCards) {
                cost = cost + " [" + c.getResource() + "] ";
            }
        }
        //get cost of basic production
        if (isBasicProd) {
            cost = cost + "[ANY 2 RESOURCES]";
        }

        listCostLblCP.setWrapText(true);
        listCostLblCP.setText(cost);

        //showing loot status
        loot.forEach((x, y) -> {
            switch (x.getFirstValue()) {
                case COIN -> {
                    qtyCoinLblCP.setText("x" + y);
                    coinQty = y;
                }
                case SERVANT -> {
                    qtyServantLblCP.setText("x" + y);
                    servantQty = y;
                }
                case SHIELD -> {
                    qtyShieldLblCP.setText("x" + y);
                    shieldQty = y;
                }
                case STONE -> {
                    qtyStoneLblCP.setText("x" + y);
                    stoneQty = y;
                }
            }
        });

        //set images in warehouse
        Resource res = player.getResourceFromStorage(WAREHOUSE_SMALL);
        if (res != null) {
            shelf1CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
        }

        res = player.getResourceFromStorage(WAREHOUSE_MID);
        int qty = player.getQtyInStorage(res, WAREHOUSE_MID);

        if (res != null && qty == 2) {
            shelf21CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            shelf22CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
        } else if (res != null && qty == 1) {
            shelf21CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
        }

        res = player.getResourceFromStorage(WAREHOUSE_LARGE);
        qty = player.getQtyInStorage(res, WAREHOUSE_LARGE);

        if (res != null) {
            if (qty == 3) {
                shelf31CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                shelf32CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                shelf33CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            } else if (qty == 2) {
                shelf31CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                shelf32CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            } else {
                shelf31CP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
            }
        }

        //set images in extra storage
        Resource extraRes1 = player.getResourceFromStorage(EXTRA1);
        Resource extraRes2 = player.getResourceFromStorage(EXTRA2);

        if (extraRes1 != null || extraRes2 != null) {
            qty = player.getQtyInStorage(res, EXTRA1);
            if (extraRes1 != null) {
                if (qty >= 1) {
                    extraStorage11ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
                if (qty == 2) {
                    extraStorage12ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
            }
            qty = player.getQtyInStorage(res, EXTRA2);
            if (extraRes2 != null) {
                if (qty >= 1) {
                    extraStorage21ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
                if (qty == 2) {
                    extraStorage22ImgCP.setImage(new Image("/img/pawns/" + res.toString().toLowerCase() + ".png"));
                }
            }
        } else {
            extraStorageBox.setVisible(false);
            extraStorageBox.setManaged(false);
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

        //choose from extra storage
        List<Resource> fromExtra1 = new ArrayList<>();
        List<Resource> fromExtra2 = new ArrayList<>();

        extraStorage11CP.setOnAction(event -> {
            if (extraStorage11CP.isSelected()) {
                fromExtra1.add(player.getResourceFromStorage(EXTRA1));
            } else {
                fromExtra1.remove(player.getResourceFromStorage(EXTRA1));
            }
        });
        extraStorage12CP.setOnAction(event -> {
            if (extraStorage12CP.isSelected()) {
                fromExtra1.add(player.getResourceFromStorage(EXTRA1));
            } else {
                fromExtra1.remove(player.getResourceFromStorage(EXTRA1));
            }
        });
        extraStorage21CP.setOnAction(event -> {
            if (extraStorage21CP.isSelected()) {
                fromExtra2.add(player.getResourceFromStorage(EXTRA2));
            } else {
                fromExtra2.remove(player.getResourceFromStorage(EXTRA2));
            }
        });
        extraStorage22CP.setOnAction(event -> {
            if (extraStorage22CP.isSelected()) {
                fromExtra2.add(player.getResourceFromStorage(EXTRA2));
            } else {
                fromExtra2.remove(player.getResourceFromStorage(EXTRA2));
            }
        });

        //choose from loot
        List<Resource> fromLoot = new ArrayList<>();
        stoneAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLblCP.getText()) < stoneQty) {
                stoneLblCP.setText(String.valueOf((Integer.parseInt(stoneLblCP.getText()) + 1)));
                fromLoot.add(STONE);
            }
        });
        stoneSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLblCP.getText()) != 0) {
                stoneLblCP.setText(String.valueOf((Integer.parseInt(stoneLblCP.getText()) - 1)));
                fromLoot.remove(STONE);
            }
        });
        servantAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLblCP.getText()) < servantQty) {
                servantLblCP.setText(String.valueOf((Integer.parseInt(servantLblCP.getText()) + 1)));
                fromLoot.add(SERVANT);
            }
        });
        servantSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLblCP.getText()) != 0) {
                servantLblCP.setText(String.valueOf((Integer.parseInt(servantLblCP.getText()) - 1)));
                fromLoot.remove(SERVANT);
            }
        });
        coinAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLblCP.getText()) < coinQty) {
                coinLblCP.setText(String.valueOf((Integer.parseInt(coinLblCP.getText()) + 1)));
                fromLoot.add(COIN);
            }
        });
        coinSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLblCP.getText()) != 0) {
                coinLblCP.setText(String.valueOf((Integer.parseInt(coinLblCP.getText()) - 1)));
                fromLoot.remove(COIN);
            }
        });
        shieldAddBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLblCP.getText()) < shieldQty) {
                shieldLblCP.setText(String.valueOf((Integer.parseInt(shieldLblCP.getText()) + 1)));
                fromLoot.add(SHIELD);
            }
        });
        shieldSubBtnCP.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLblCP.getText()) != 0) {
                shieldLblCP.setText(String.valueOf((Integer.parseInt(shieldLblCP.getText()) - 1)));
                fromLoot.remove(SHIELD);
            }
        });

        //recycle stack button for confirm
        stack1Btn.setVisible(false);
        stack1Btn.setDisable(true);
        stack3Btn.setVisible(false);
        stack3Btn.setDisable(true);
        stack2Btn.setText("Confirm");
        stack2Btn.setOnAction(actionEvent -> {
            if (!(fromLoot.isEmpty() && fromWar.isEmpty())) {
                wallet.setLootchestTray(fromLoot);
                wallet.setWarehouseTray(fromWar);
                wallet.setExtraStorage(fromExtra1, 0);
                wallet.setExtraStorage(fromExtra2, 1);

                resWal = wallet;

                controller.sendProductionMessage(prodCards, resWal, leaderCards, leaderOutputs, isBasicProd, basicOut);
                mainBoard.setEffect(null);
                popUpStage.close();
            }
        });

        backPaymentBtnCP.setOnAction(actionEvent -> {
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

        activateLeadPopUp(activable);
    }

    private void activateLeadPopUp(List<LeaderCard> activable) {
        resetDALPopUp();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(DISCARD_ACTIVATE_LEADER));
        popUpStage.show();

        DALLbl.setText("Select the leader you want to ACTIVATE");

        if (activable.size() >= 1) {
            DAL1Toggle.setVisible(true);
            DAL1Img.setImage(new Image("/img/leaderCards/" + activable.get(0).getId() + ".png"));
            DAL1Toggle.setUserData(activable.get(0).getId());
        }
        if (activable.size() == 2) {
            DAL2Toggle.setVisible(true);
            DAL2Img.setImage(new Image("/img/leaderCards/" + activable.get(1).getId() + ".png"));
            DAL2Toggle.setUserData(activable.get(1).getId());
        }

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
        if (controller.getPlayer().getLeaders().size() != 0) {
            List<LeaderCard> leaderCards = new ArrayList<>();
            for (LeaderCard led : controller.getPlayer().getLeaders())
                if (!led.isActive())
                    leaderCards.add(led);

            if (leaderCards.size() >= 1) {
                discardLeadPopUp(leaderCards);
            } else {
                sendToMsgBoard("You don't have any leader cards to discard!");
            }
        } else {
            sendToMsgBoard("You don't have any leader cards to discard!");
        }
    }

    private void discardLeadPopUp(List<LeaderCard> lead) {
        resetDALPopUp();

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(DISCARD_ACTIVATE_LEADER));
        popUpStage.show();

        DALLbl.setText("Select the leader you want to DISCARD");

        if (lead.size() >= 1) {
            DAL1Toggle.setVisible(true);
            DAL1Img.setImage(new Image("/img/leaderCards/" + lead.get(0).getId() + ".png"));
            DAL1Toggle.setUserData(lead.get(0).getId());
        }
        if (lead.size() == 2) {
            DAL2Toggle.setVisible(true);
            DAL2Img.setImage(new Image("/img/leaderCards/" + lead.get(1).getId() + ".png"));
            DAL2Toggle.setUserData(lead.get(1).getId());
        }

        DALConfirmBtn.setOnAction(actionEvent -> {
            int c = Integer.parseInt(chosenLeaderGrp.getSelectedToggle().getUserData().toString());
            controller.sendDiscardLeader(chosenLeaderGrp.getSelectedToggle().getUserData().toString());
            controller.getPlayer().getLeaders().removeIf(led -> led.getId() == c);

            mainBoard.setEffect(null);
            popUpStage.close();

            sendMsgPopUp("You have earned a Faith Point!");
        });

        DALBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    private void resetDALPopUp() {
        DAL1Img.setImage(null);
        DAL1Toggle.setUserData(null);
        DAL2Img.setImage(null);
        DAL2Toggle.setUserData(null);

        DAL1Toggle.setVisible(false);
        DAL2Toggle.setVisible(false);
    }

    /* MESSAGE POP UP *************************************************************************************************/
    private void resetMsgPopUp() {
        msg1Lbl.setText("");
        msg2Lbl.setText("");
    }

    /**
     * Display the message in a pop-up.
     *
     * @param msg the message to be displayed
     */
    public void sendMsgPopUp(String msg) {
        resetMsgPopUp();

        mainBoard.setEffect(new GaussianBlur());
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(MSG));
        popUpStage.show();

        msg1Lbl.setText(msg);

        msgOkBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    /**
     * Display the messages for Lorenzo's actions in a pop-up.
     *
     * @param msg1 the main message
     * @param msg2 the additional information to place under {@code msg1}
     */
    public void sendMsgPopUp(String msg1, String msg2) {
        resetMsgPopUp();

        mainBoard.setEffect(new GaussianBlur());
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(MSG));
        popUpStage.show();

        msg1Lbl.setText(msg1);

        msg2Lbl.setWrapText(true);
        msg2Lbl.setText(msg2);

        msgOkBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    /* END ************************************************************************************************************/

    /**
     * Change the scene displayed on the stage showing if the player has won or the "defeat" scene informing who has won the match.
     *
     * @param winner the name of the winner of the game
     */
    public void displayWinner(String winner) {
        if (controller.getPlayer().getNickname().equals(winner)) {
            endStatusLbl.setText("VICTORY!");
        } else {
            endStatusLbl.setText("DEFEAT...");
            winnerLbl.setText(winner + " won");
        }

        double x, y;
        x = stage.getWidth();
        y = stage.getHeight();
        setScene(END);
        stage.setWidth(x);
        stage.setHeight(y);
    }
}
