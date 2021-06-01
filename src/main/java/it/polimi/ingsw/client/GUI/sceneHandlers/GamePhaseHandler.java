package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
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
    private GridPane marketMarbles;
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

    private void updateMarket() {
        ObservableList<Node> children = marketMarbles.getChildren();

        extraMarble.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                for (Node node : children) {
                    if (marketMarbles.getRowIndex(node) == x && marketMarbles.getColumnIndex(node) == y) {
                        ((Circle) node).setFill(Color.web(controller.getMarket().getMarketBoard()[x][y].getHexCode()));
                        break;
                    }
                }
            }
        }
    }

}
