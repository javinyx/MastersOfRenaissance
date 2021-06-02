package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.*;
import static it.polimi.ingsw.client.GUI.sceneHandlers.ScenesEnum.MAIN_BOARD;

public class GamePhaseHandler extends PhaseHandler{

    @FXML
    private ImageView leader1Show, leader2Show;
    @FXML
    private GridPane marketMarbles, productionCards;
    @FXML
    private Circle extraMarble;

    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();

    public GamePhaseHandler(GuiController controller, Stage stage) {
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(MAIN_BOARD));
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

}
