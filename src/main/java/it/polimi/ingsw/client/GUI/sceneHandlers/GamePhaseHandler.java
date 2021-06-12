package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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

public class GamePhaseHandler extends PhaseHandler {
    /* MAIN ***********************************************************************************************************/
    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();
    private final BuyProdCardPhase buyProdCardPhase;
    @FXML
    private AnchorPane mainBoard;
    @FXML
    private Button endTurnBtn, productionCardsOpen;

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

    /* WAREHOUSE & CHOOSE STORAGE POPUP *****************************************************************************/
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
    private ImageView opLead1Img, opLead2Img, opShelf1Img, opShelf2Img, opShelf3Img;
    @FXML
    private Label opLbl, opQtyShelf1Lbl, opQtyShelf2Lbl, opQtyShelf3Lbl, opQtyStoneLbl, opQtyCoinLbl, opQtyServantLbl,
            opQtyShieldLbl;

    /* MESSAGE BOARD **************************************************************************************************/
    @FXML
    private TextArea msgBoard;

    /* FAITH TRACK ****************************************************************************************************/
    private List<BiElement<Double, Double>> faithCoords;
    @FXML
    private ImageView redCross;

    /* PRODUCTION CARDS POPUP *****************************************************************************************/
    @FXML
    private Button productionCardBackBtn;

    public GamePhaseHandler(GuiController controller, Stage stage) {
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(MAIN_BOARD, MARKET, STORAGE, OTHER_PLAYERS, PRODUCTION_CARDS));
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

        buyProdCardPhase = new BuyProdCardPhase(controller, stage);
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
    public void initiateBoard(List<Integer> chosenLeadersId, List<ConcreteProductionCard> availableProductionCards) {
        setLeaders(chosenLeadersId);
        setWarehouse();
        setMarket();
        setProductionCards(availableProductionCards);
        setEnemyBoard();
        initiateFaithTrack();
        setFaithTrack();

        if (controller.getPlayer().isMyTurn()) {
            sendToMsgBoard("It is now your turn, please either buy from market, buy production cards or" +
                    " activate production.");
        } else {
            sendToMsgBoard("It is not your turn yet, please wait for the other players.");
        }

    }

    public void updateBoard(List<ConcreteProductionCard> availableProductionCards) {
        setWarehouse();
        setMarket();
        setProductionCards(availableProductionCards);
        setEnemyBoard();
        setFaithTrack();
    }

    public void observePlayerActions() {
        if (controller.getPlayer().isMyTurn()) {
            marketRegion.setOnMouseClicked(event -> {
                mainBoard.setEffect(new GaussianBlur());
                marketPopUp();
            });

            endTurnBtn.setOnAction(actionEvent -> {
                tmpRes.clear();
                controller.passTurn();
            });
        }

        productionCardsOpen.setOnAction(actionEvent -> {
            productionCardsPopup();
        });

        player1Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(0)));
        player2Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(1)));
        player3Btn.setOnAction(event -> otherPlayersPopUp(controller.getOtherPlayers().get(2)));
    }

    private void setLeaders(List<Integer> chosenLeadersId) {
        leader1Show.setImage(new Image("img/leaderCards/" + chosenLeadersId.get(0) + ".png"));
        leader1Show.setEffect(new SepiaTone(0.6));
        leader2Show.setImage(new Image("img/leaderCards/" + chosenLeadersId.get(1) + ".png"));
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
                    }
                }
                case WAREHOUSE_MID -> {
                    if (y >= 1) {
                        shelf21MB.setImage(img);
                        shelf21PU.setDisable(true);
                        shelf21ImgPU.setLayoutX(shelf21PU.getLayoutX() + 5);
                        shelf21ImgPU.setLayoutY(shelf21PU.getLayoutY() + 5);
                        shelf21ImgPU.setImage(img);
                    }
                    if (y == 2) {
                        shelf22MB.setImage(img);
                        shelf22PU.setDisable(true);
                        shelf22ImgPU.setLayoutX(shelf22PU.getLayoutX() + 5);
                        shelf22ImgPU.setLayoutY(shelf22PU.getLayoutY() + 5);
                        shelf22ImgPU.setImage(img);
                    }
                }
                case WAREHOUSE_LARGE -> {
                    if (y >= 1) {
                        shelf31MB.setImage(img);
                        shelf31PU.setDisable(true);
                        shelf31ImgPU.setLayoutX(shelf31PU.getLayoutX() + 5);
                        shelf31ImgPU.setLayoutY(shelf31PU.getLayoutY() + 5);
                        shelf31ImgPU.setImage(img);
                    }
                    if (y >= 2) {
                        shelf32MB.setImage(img);
                        shelf32PU.setDisable(true);
                        shelf32ImgPU.setLayoutX(shelf32PU.getLayoutX() + 5);
                        shelf32ImgPU.setLayoutY(shelf32PU.getLayoutY() + 5);
                        shelf32ImgPU.setImage(img);
                    }
                    if (y == 3) {
                        shelf33MB.setImage(img);
                        shelf33PU.setDisable(true);
                        shelf33ImgPU.setLayoutX(shelf33PU.getLayoutX() + 5);
                        shelf33ImgPU.setLayoutY(shelf33PU.getLayoutY() + 5);
                        shelf33ImgPU.setImage(img);
                    }
                }
            }
        });
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


    /* PRODUCTION CARDS ***********************************************************************************************/
    private void productionCardsPopup() {
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        mainBoard.setEffect(new GaussianBlur());
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(PRODUCTION_CARDS));
        popUpStage.show();

        productionCards.setOnMouseClicked(this::clickGrid);

        productionCardBackBtn.setOnAction(actionEvent -> {
            mainBoard.setEffect(null);
            popUpStage.close();
        });
    }

    private void clickGrid(MouseEvent event) {
        Node clickedNode = event.getPickResult().getIntersectedNode();
        if (clickedNode != productionCards) {
            String url = ((ImageView) clickedNode).getImage().getUrl();
            String cardIdS = url.substring(url.lastIndexOf("/") + 1).split("\\.")[0];
            Integer cardId = Integer.parseInt(cardIdS);
            Integer stack = askStack();

            buyProdCardPhase.choosePayment(cardId, stack);
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

    /* ENEMY BOARD ****************************************************************************************************/
    private void setEnemyBoard() {
        List<Button> playersBtns = new ArrayList<>(Arrays.asList(player1Btn, player2Btn, player3Btn));
        List<Label> playersFaithLbls = new ArrayList<>(Arrays.asList(player1FaithLbl, player2FaithLbl,
                player3FaithLbl));
        List<ImageView> playersFaithImgs = new ArrayList<>(Arrays.asList(player1FaithImg, player2FaithImg,
                player3FaithImg));

        int ctr = 0;
        for (NubPlayer p : controller.getOtherPlayers()) {
            playersBtns.get(ctr).setText(p.getNickname() + "(#" + p.getTurnNumber() + ")");
            playersFaithLbls.get(ctr).setText(String.valueOf(p.getCurrPos()));
            ctr++;
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
        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        mainBoard.setEffect(new GaussianBlur());
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(OTHER_PLAYERS));
        popUpStage.show();

        opLbl.setText("You're viewing " + player.getNickname() + "'s status");
        System.out.println("leader size: " + player.getLeaders().size());
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

        opBackBtn.setOnAction(event -> {

        });
    }

    /* MESSAGE BOARD **************************************************************************************************/
    private void sendToMsgBoard(String message) {
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

}
