package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.Warehouse;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;

public class GamePhaseHandler extends PhaseHandler {

    private Node target;

    @FXML
    private AnchorPane mainBoard;
    @FXML
    private ImageView leader1Show, leader2Show;
    @FXML
    private GridPane marketMarbles, marketMarblesPU, productionCards;
    @FXML
    private Circle extraMarble, extraMarblePU;
    @FXML
    private Region marketRegion;
    @FXML
    private Button marketBackBtn;
    @FXML
    private ImageView marketRow1Btn, marketRow2Btn, marketRow3Btn, marketCol1Btn, marketCol2Btn, marketCol3Btn, marketCol4Btn;
    @FXML
    private ImageView resource1ImgPU;
    @FXML
    private Region shelf1PU, shelf21PU, shelf22PU, shelf31PU, shelf32PU, shelf33PU;
    @FXML
    private ImageView shelf1MB, shelf21MB, shelf22MB, shelf31MB, shelf32MB, shelf33MB;

    //TODO: LORENZOROLLED UNDER PATH 8
    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();

    public GamePhaseHandler(GuiController controller, Stage stage) {
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(MAIN_BOARD, MARKET, STORAGE));
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

    public void initiateBoard(List<Integer> chosenLeadersId, List<ConcreteProductionCard> availableProductionCards) {
        leader1Show.setImage(new Image("img/leaderCards/" + chosenLeadersId.get(0) + ".png"));
        leader1Show.setEffect(new SepiaTone(0.6));
        leader2Show.setImage(new Image("img/leaderCards/" + chosenLeadersId.get(1) + ".png"));
        leader2Show.setEffect(new SepiaTone(0.6));

        extraMarble.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));
        extraMarblePU.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                setMarbleColor(x, y, marketMarbles, controller.getMarket().getMarketBoard()[x][y].getHexCode());
                setMarbleColor(x, y, marketMarblesPU, controller.getMarket().getMarketBoard()[x][y].getHexCode());
            }
        }

        int i = 0;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                setProductionCards(x, y, productionCards, availableProductionCards.get(i).getId());
                i++;
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

    private void setProductionCards(int row, int column, GridPane gridPane, int imgId) {
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if (gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                ((ImageView) node).setImage(new Image("img/productionCardsFront/" + imgId + ".png"));
                break;
            }
        }
    }

    private void updateBoard(List<ConcreteProductionCard> availableProductionCards) {
        int x, y, i;

        ObservableList<Node> children1 = marketMarbles.getChildren();
        ObservableList<Node> children2 = productionCards.getChildren();

        extraMarble.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));

        for (x = 0; x < 3; x++) {
            for (y = 0; y < 4; y++) {
                for (Node node : children1) {
                    if (marketMarbles.getRowIndex(node) == x && marketMarbles.getColumnIndex(node) == y) {
                        ((Circle) node).setFill(Color.web(controller.getMarket().getMarketBoard()[x][y].getHexCode()));
                        break;
                    }
                }
            }
        }

        i = 0;
        for (x = 0; x < 3; x++) {
            for (y = 0; y < 4; y++) {
                for (Node node : children2) {
                    if (productionCards.getRowIndex(node) == x && productionCards.getColumnIndex(node) == y) {
                        ((ImageView) node).setImage(new Image("img/productionCardsFront/" + availableProductionCards.get(i).getId() + ".png"));
                        i++;
                        break;
                    }
                }
            }
        }

    }

    public void observePlayerActions() {
        marketRegion.setOnMouseClicked(event -> {
            mainBoard.setEffect(new GaussianBlur());
            marketPopUp();
        });

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

    public void chooseStoragePopUp(List<Resource> selectedRes, int i) {

        resource1ImgPU.setImage(new Image("img/pawns/" + selectedRes.get(i).toString().toLowerCase() + ".png"));

        mainBoard.setEffect(new GaussianBlur());

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);
        popUpStage.centerOnScreen();
        popUpStage.setScene(getScene(STORAGE));
        popUpStage.show();

        resource1ImgPU.setOnDragDetected(event -> {
            sourceDragDetected(event, selectedRes.get(0));
        });
        resource1ImgPU.setOnDragDone(this::sourceDragDone);

        List<BiElement<Resource, Storage>> resourcePlacement = new ArrayList<>();

        shelf1PU.setOnDragOver(this::targetDragOver);
        shelf1PU.setOnDragDropped(event -> {
            resourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_SMALL));
            controller.sendPlaceResourcesMessage(resourcePlacement);
            popUpStage.close();
            mainBoard.setEffect(null);
            controller.ontoTheNextResource(selectedRes, i);
        });

        shelf21PU.setOnDragOver(this::targetDragOver);
        shelf21PU.setOnDragDropped(event -> {
            resourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_MID));
            controller.sendPlaceResourcesMessage(resourcePlacement);
            popUpStage.close();
            mainBoard.setEffect(null);
            controller.ontoTheNextResource(selectedRes, i);
        });
        shelf22PU.setOnDragOver(this::targetDragOver);
        shelf22PU.setOnDragDropped(event -> {
            resourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_MID));
            controller.sendPlaceResourcesMessage(resourcePlacement);
            popUpStage.close();
            mainBoard.setEffect(null);
            controller.ontoTheNextResource(selectedRes, i);
        });

        shelf31PU.setOnDragOver(this::targetDragOver);
        shelf31PU.setOnDragDropped(event -> {
            resourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
            controller.sendPlaceResourcesMessage(resourcePlacement);
            popUpStage.close();
            mainBoard.setEffect(null);
            controller.ontoTheNextResource(selectedRes, i);
        });
        shelf32PU.setOnDragOver(this::targetDragOver);
        shelf32PU.setOnDragDropped(event -> {
            resourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
            controller.sendPlaceResourcesMessage(resourcePlacement);
            popUpStage.close();
            mainBoard.setEffect(null);
            controller.ontoTheNextResource(selectedRes, i);
        });
        shelf33PU.setOnDragOver(this::targetDragOver);
        shelf33PU.setOnDragDropped(event -> {
            resourcePlacement.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
            controller.sendPlaceResourcesMessage(resourcePlacement);
            popUpStage.close();
            mainBoard.setEffect(null);
            controller.ontoTheNextResource(selectedRes, i);
        });
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

        event.setDropCompleted(true);
        event.consume();

        return Resource.valueOf(db.getString());
    }

}
