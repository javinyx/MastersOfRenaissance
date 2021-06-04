package it.polimi.ingsw.client.GUI.sceneHandlers;

import it.polimi.ingsw.client.GUI.GuiController;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
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

/**
 * This class is the handler for the welcome, connection, registration, waiting room, leaders choice and resource choice
 * action during the initial phase.
 */
public class InitialPhaseHandler extends PhaseHandler {
    private String ip, port;
    private int ctr;
    private Node target;

    private List<BiElement<Resource, Storage>> initialResourcePlacements = new ArrayList<>();

    private Map<ScenesEnum, Scene> sceneMap = new HashMap<>();

    @FXML
    private Button playBtn, quitBtn, connectBtn, localModeBtn;
    @FXML
    private TextField ipField, portField, nickNameField;
    @FXML
    private Button singlePlayerBtn, twoPlayerBtn, threePlayerBtn, fourPlayerBtn;
    @FXML
    private Label waitingRoomLbl, badNameLbl;
    @FXML
    private Button chooseLeadersBtn, chooseResourcesBtn;
    @FXML
    private ToggleButton leader1Toggle, leader2Toggle, leader3Toggle, leader4Toggle;
    @FXML
    private ImageView leader1Img, leader2Img, leader3Img, leader4Img;
    @FXML
    private Label chooseResLbl, chooseFaithLbl;
    @FXML
    private Button stoneSubBtn, stoneAddBtn, servantSubBtn, servantAddBtn, coinSubBtn, coinAddBtn, shieldSubBtn, shieldAddBtn;
    @FXML
    private Label stoneLbl, servantLbl, coinLbl, shieldLbl, resPluralLbl;
    @FXML
    private Pane chooseResPane, chooseStrPane, welcomePane;
    @FXML
    private ImageView resource1Img, resource2Img, wareHouseImg;
    @FXML
    private Region shelf1, shelf21, shelf22, shelf31, shelf32, shelf33;
    @FXML
    private Button chooseStorageBtn;
    @FXML
    private ImageView leader1Show, leader2Show;
    @FXML
    private Pane mainBoard;
    @FXML
    private GridPane marketMarbles, productionCards;
    @FXML
    private Circle extraMarble;
    @FXML
    private Button player1Btn, player2Btn, player3Btn;
    @FXML
    private Label player1FaithLbl, player2FaithLbl, player3FaithLbl;

    public InitialPhaseHandler(GuiController controller, Stage stage) {
        super(controller, stage);

        List<ScenesEnum> allPaths = new ArrayList<>(Arrays.asList(WELCOME, CONNECTION, REGISTRATION, WAITING_ROOM, CHOOSE_LEADERS, CHOOSE_RESOURCES, CHOOSE_STORAGE, MAIN_BOARD));
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

    @FXML
    public void start() {
        playBtn.setOnAction(actionEvent -> {
            try {
                controller.setup();
            } catch (IOException e) {
                System.err.println("IOException");
            }
        });

        quitBtn.setOnAction(actionEvent -> {
            System.exit(0);
        });
    }

    /**
     * @return a {@link BiElement} containing the IP as first value and the Port as second.
     */
    @FXML
    public void retrieveIpAndPort() {
        connectBtn.setOnAction(actionEvent -> {
            if (ipField.getText().length() > 0 && ipField.getText().length() > 0) {
                ip = ipField.getText();
                port = portField.getText();
            } else {
                ip = "localhost";
                port = "27001";
            }
            controller.setIpAndPort(ip, port);
        });

        localModeBtn.setOnAction(actionEvent -> {
            ip = "0";
            port = "0";
            controller.setIpAndPort(ip, port);
        });
    }

    @FXML
    public void getNickNameAndGameSize() {
        singlePlayerBtn.setOnAction(this::setNickNameAndGameSize);
        twoPlayerBtn.setOnAction(this::setNickNameAndGameSize);
        threePlayerBtn.setOnAction(this::setNickNameAndGameSize);
        fourPlayerBtn.setOnAction(this::setNickNameAndGameSize);
    }

    @FXML
    private void setNickNameAndGameSize(ActionEvent event) {
        if (nickNameField.getText().length() > 0 && nickNameField.getText().length() <= 12) {
            String nickname = nickNameField.getText();
            String gameSize = ((Button) event.getSource()).getText();
            controller.setNickname(nickname);
            controller.setGameSize(gameSize);
        } else {
            badNameLbl.setText("Your name must be between 1 and 12 characters long");
        }
    }

    @FXML
    public void setWaitingRoomName(String nickName) {
        waitingRoomLbl.setText("Hello, " + nickName);
    }

    @FXML
    public void waitStartGame(){
        waitingRoomLbl.setText("The game is about to start!");
        setScene(WAITING_ROOM);
    }

    @FXML
    public void displayLeaders(List<LeaderCard> availableLeaders) {
        leader1Img.setImage(new Image("img/leaderCards/" + availableLeaders.get(0).getId() + ".png"));
        leader2Img.setImage(new Image("img/leaderCards/" + availableLeaders.get(1).getId() + ".png"));
        leader3Img.setImage(new Image("img/leaderCards/" + availableLeaders.get(2).getId() + ".png"));
        leader4Img.setImage(new Image("img/leaderCards/" + availableLeaders.get(3).getId() + ".png"));
    }

    @FXML
    public void chooseLeaders() {
        ctr = 0;

        List<ToggleButton> toggleButtons = new ArrayList<>();
        toggleButtons.add(leader1Toggle);
        toggleButtons.add(leader2Toggle);
        toggleButtons.add(leader3Toggle);
        toggleButtons.add(leader4Toggle);

        leader1Toggle.setOnAction(actionEvent -> leaderToggle(actionEvent, toggleButtons));
        leader2Toggle.setOnAction(actionEvent -> leaderToggle(actionEvent, toggleButtons));
        leader3Toggle.setOnAction(actionEvent -> leaderToggle(actionEvent, toggleButtons));
        leader4Toggle.setOnAction(actionEvent -> leaderToggle(actionEvent, toggleButtons));

        chooseLeadersBtn.setOnAction(actionEvent -> {
            if (ctr == 2) {
                List<Boolean> selectedLeaders = new ArrayList<>();
                selectedLeaders.add(leader1Toggle.isSelected());
                selectedLeaders.add(leader2Toggle.isSelected());
                selectedLeaders.add(leader3Toggle.isSelected());
                selectedLeaders.add(leader4Toggle.isSelected());
                controller.setSelectedLeaders(selectedLeaders);
            }
        });
    }

    public void leaderToggle(ActionEvent event, List<ToggleButton> toggleButtons) {
        if (((ToggleButton) event.getSource()).isSelected()) {
            if (ctr == 0) {
                ctr++;
            } else if (ctr == 1) {
                ctr++;
                for (ToggleButton currBtn : toggleButtons) {
                    if (currBtn != event.getSource()) {
                        if (!currBtn.isSelected()) {
                            currBtn.setDisable(true);
                        }
                    }
                }
            }
        } else {
            ctr--;
            for (ToggleButton currBtn : toggleButtons) {
                if (currBtn != event.getSource()) {
                    if (!currBtn.isSelected()) {
                        currBtn.setDisable(false);
                    }
                }
            }
        }
    }

    @FXML
    public void chooseResources(Integer resources) {

        List<Resource> selectedRes = new ArrayList<>();

        chooseResLbl.setText(resources.toString());
        if (resources == 2) {
            resPluralLbl.setText("resources");
        }

        if (controller.getPlayer().getTurnNumber() == 3 || controller.getPlayer().getTurnNumber() == 4) {
            chooseFaithLbl.setText("You have also received 1 bonus faith point");
        } else {
            chooseFaithLbl.setDisable(true);
        }

        ctr = 0;

        stoneAddBtn.setOnAction(actionEvent -> {
            if (ctr < resources) {
                stoneLbl.setText(String.valueOf((Integer.parseInt(stoneLbl.getText()) + 1)));
                ctr++;
            }
        });
        stoneSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLbl.getText()) != 0) {
                stoneLbl.setText(String.valueOf((Integer.parseInt(stoneLbl.getText()) - 1)));
                ctr--;
            }
        });
        servantAddBtn.setOnAction(actionEvent -> {
            if (ctr < resources) {
                servantLbl.setText(String.valueOf((Integer.parseInt(servantLbl.getText()) + 1)));
                ctr++;
            }
        });
        servantSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(servantLbl.getText()) != 0) {
                servantLbl.setText(String.valueOf((Integer.parseInt(servantLbl.getText()) - 1)));
                ctr--;
            }
        });
        coinAddBtn.setOnAction(actionEvent -> {
            if (ctr < resources) {
                coinLbl.setText(String.valueOf((Integer.parseInt(coinLbl.getText()) + 1)));
                ctr++;
            }
        });
        coinSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(coinLbl.getText()) != 0) {
                coinLbl.setText(String.valueOf((Integer.parseInt(coinLbl.getText()) - 1)));
                ctr--;
            }
        });
        shieldAddBtn.setOnAction(actionEvent -> {
            if (ctr < resources) {
                shieldLbl.setText(String.valueOf((Integer.parseInt(shieldLbl.getText()) + 1)));
                ctr++;
            }
        });
        shieldSubBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(shieldLbl.getText()) != 0) {
                shieldLbl.setText(String.valueOf((Integer.parseInt(shieldLbl.getText()) - 1)));
                ctr--;
            }
        });

        chooseResourcesBtn.setOnAction(actionEvent -> {
            if (Integer.parseInt(stoneLbl.getText()) + Integer.parseInt(servantLbl.getText()) + Integer.parseInt(coinLbl.getText()) + Integer.parseInt(shieldLbl.getText()) == resources) {

                if (Integer.parseInt(stoneLbl.getText()) == 1) {
                    selectedRes.add(Resource.STONE);
                } else if (Integer.parseInt(stoneLbl.getText()) == 2) {
                    selectedRes.add(Resource.STONE);
                    selectedRes.add(Resource.STONE);
                }

                if (Integer.parseInt(servantLbl.getText()) == 1) {
                    selectedRes.add(Resource.SERVANT);
                } else if (Integer.parseInt(servantLbl.getText()) == 2) {
                    selectedRes.add(Resource.SERVANT);
                    selectedRes.add(Resource.SERVANT);
                }

                if (Integer.parseInt(coinLbl.getText()) == 1) {
                    selectedRes.add(Resource.COIN);
                } else if (Integer.parseInt(coinLbl.getText()) == 2) {
                    selectedRes.add(Resource.COIN);
                    selectedRes.add(Resource.COIN);
                }

                if (Integer.parseInt(shieldLbl.getText()) == 1) {
                    selectedRes.add(Resource.SHIELD);
                } else if (Integer.parseInt(shieldLbl.getText()) == 2) {
                    selectedRes.add(Resource.SHIELD);
                    selectedRes.add(Resource.SHIELD);
                }

                chooseStorage(chooseResPane, getScene(CHOOSE_STORAGE), selectedRes);
            }
        });
    }

    private void chooseStorage(Pane motherPane, Scene popUpScene, List<Resource> selectedRes) {
        resource1Img.setImage(new Image("img/pawns/" + selectedRes.get(0).toString().toLowerCase() + ".png"));
        if (selectedRes.size() == 1) {
            resource2Img.setManaged(false);
        } else {
            resource2Img.setImage(new Image("img/pawns/" + selectedRes.get(1).toString().toLowerCase() + ".png"));
        }

        motherPane.setEffect(new GaussianBlur());

        Stage popUpStage = new Stage(StageStyle.TRANSPARENT);
        popUpStage.initOwner(stage);
        popUpStage.initModality(Modality.APPLICATION_MODAL);

        popUpStage.setX(stage.getX() + (stage.getWidth() / 4));
        popUpStage.setY(stage.getY() + (stage.getHeight() / 4));

        popUpStage.setScene(popUpScene);
        popUpStage.show();

        resource1Img.setOnDragDetected(event -> {
            sourceDragDetected(event, selectedRes.get(0));
        });
        resource1Img.setOnDragDone(this::sourceDragDone);

        if (resource2Img.isManaged()) {
            resource2Img.setOnDragDetected(event -> {
                sourceDragDetected(event, selectedRes.get(1));
            });
            resource2Img.setOnDragDone(this::sourceDragDone);
        }

        shelf1.setOnDragOver(this::targetDragOver);
        shelf1.setOnDragDropped(event -> {
            initialResourcePlacements.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_SMALL));
        });

        shelf21.setOnDragOver(this::targetDragOver);
        shelf21.setOnDragDropped(event -> {
            initialResourcePlacements.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_MID));
        });
        shelf22.setOnDragOver(this::targetDragOver);
        shelf22.setOnDragDropped(event -> {
            initialResourcePlacements.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_MID));
        });

        shelf31.setOnDragOver(this::targetDragOver);
        shelf31.setOnDragDropped(event -> {
            initialResourcePlacements.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
        });
        shelf32.setOnDragOver(this::targetDragOver);
        shelf32.setOnDragDropped(event -> {
            initialResourcePlacements.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
        });
        shelf33.setOnDragOver(this::targetDragOver);
        shelf33.setOnDragDropped(event -> {
            initialResourcePlacements.add(new BiElement<>(targetDragDropped(event), Storage.WAREHOUSE_LARGE));
        });

        chooseStorageBtn.setOnAction(actionEvent -> {
            if (initialResourcePlacements.size() == selectedRes.size()) {
                motherPane.setEffect(null);
                popUpStage.close();
                controller.setInitialResourcePlacements(initialResourcePlacements);
            }
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

    public void initiateBoard(List<Integer> chosenLeadersId, List<ConcreteProductionCard> availableProductionCards) {
        leader1Show.setImage(new Image("img/leaderCards/" + chosenLeadersId.get(0) + ".png"));
        leader1Show.setEffect(new SepiaTone(0.6));
        leader2Show.setImage(new Image("img/leaderCards/" + chosenLeadersId.get(1) + ".png"));
        leader2Show.setEffect(new SepiaTone(0.6));

        extraMarble.setFill(Color.web(controller.getMarket().getExtra().getHexCode()));
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 4; y++) {
                setMarbleColor(x, y, marketMarbles, controller.getMarket().getMarketBoard()[x][y].getHexCode());
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
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                ((Circle)node).setFill(Color.web(color));
                break;
            }
        }
    }

    private void setProductionCards(int row, int column, GridPane gridPane, int imgId) {
        ObservableList<Node> children = gridPane.getChildren();

        for (Node node : children) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                ((ImageView)node).setImage(new Image("img/productionCardsFront/" + imgId + ".png"));
                break;
            }
        }
    }

}