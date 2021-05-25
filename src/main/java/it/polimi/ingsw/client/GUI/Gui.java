package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;


public class Gui extends Application implements ViewInterface {
    private static final String INDEX = "index.fxml";
    private static final String CONNECTION = "";
    /*private static final String REGISTRATION;
    private static final String WAITING_ROOM;
    private static final String CHOOSE_LEADERS;
    private static final String CHOOSE_RESOURCE;
    private static final String MAIN;*/
    private GuiController controller;
    private Stage stage;
    private Scene scene;
    private String ip, port;

    @FXML private Button playBtn, quitBtn;
    @FXML private TextField ipText, portText;
    @FXML private Button confirm;


    private Map<String, Scene> sceneMap = new HashMap<>();

    /*public Gui(GuiController controller){
        this.controller = controller;
    }*/


    public static void main(String[] args){
        Gui.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        controller = new GuiController(stage, this);

        initAllScenes();
        stage.setTitle("Masters of Renaissance");

        scene = sceneMap.get(INDEX);
        scene.getStylesheets().addAll(this.getClass().getResource("/fxml/style.css").toExternalForm());

        playBtn.setOnAction(actionEvent -> {
            try {
                controller.setup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        quitBtn.setOnAction(actionEvent -> System.exit(0));

        stage.setScene(scene);
        stage.show();

    }

    private void initAllScenes() throws IOException{
        //TODO: add all the paths in scenes
        List<String> scenes = new ArrayList<>(Arrays.asList(INDEX));
        for(String path : scenes){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + path));
            sceneMap.put(path, new Scene(loader.load()));
        }
    }


    public BiElement<String, Integer> connectionSetup(){
        scene = sceneMap.get(CONNECTION);

        ipText.setOnAction(actionEvent -> {
            ip = ipText.getText();
        });

        portText.setOnAction(actionEvent -> {
            port = portText.getText();
        });

        /*confirm.setOnAction(actionEvent -> {
            //controller.setIpAndPort
            }
        });
        */
        return new BiElement<String, Integer>(ip, Integer.parseInt(port));

    }

    public void registrationSetup(){

    }

    /*public BiElement<String, Integer> askIpAndPort(){
        GridPane rootNode = new GridPane();
        rootNode.setAlignment(Pos.CENTER);
        rootNode.getColumnConstraints().add(new ColumnConstraints(200));
        rootNode.getRowConstraints().add(new RowConstraints(200));
        scene.setRoot(rootNode);

        TextField ipField = new TextField();
        TextField portField = new TextField();
        ipField.setPromptText("Insert Server IP Address");
        portField.setPromptText("Insert Server Port");

        GridPane.setConstraints(ipField, 100, 75);
        GridPane.setConstraints(portField, 100, 115);

        Button confirmBtn = new Button("Confirm");
        GridPane.setConstraints(confirmBtn, 100, 170);


        rootNode.getChildren().addAll(ipField, portField, confirmBtn);
        stage.show();


        confirmBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ip = ipField.getText();
                port = portField.getText();
            }
        });

        return new BiElement<>(ip, Integer.parseInt(port));
    }*/




    @Override
    public void showMessage(String str) {

    }


    @Override
    public void updateMarket() {

    }

    @Override
    public void updateAvailableProductionCards() {

    }

    @Override
    public void updateThisPlayer() {

    }

    @Override
    public void updateOtherPlayer(NubPlayer player) {

    }


}
