package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;


public class Gui extends Application implements ViewInterface {
    private static final String INDEX = "index.fxml";
    private GuiController controller;
    private Stage stage;
    private Scene scene;
    private String ip, port;

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + INDEX));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().addAll(this.getClass().getResource("/fxml/style.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Masters of Renaissance");
        stage.show();

    }

    public BiElement<String, Integer> askIpAndPort(){
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
    }




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
