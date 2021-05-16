package it.polimi.ingsw.client.GUI;

import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private GuiController controller;
    private Stage stage;

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

        //TODO: definire schermata iniziale
        stage.setTitle("Maestri del Rinascimento");
        GridPane rootNode = new GridPane();
        rootNode.setAlignment(Pos.BOTTOM_RIGHT);

        Image leo = new Image(MastersOfRenaissance.class.getResourceAsStream("/img/LeonardoDaVinci.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(leo, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, null);
        Background background = new Background(backgroundImage);
        rootNode.setBackground(background);

        Scene initScene = new Scene(rootNode);

        stage.setScene(initScene);
        Label memeLabel = new Label("It's a me... Leo.");
        memeLabel.setMinWidth(200.0);
        memeLabel.alignmentProperty();

        Button startGame = new Button("Play");
        Button exit = new Button("Quit");

        startGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    controller.setup();
                }catch(IOException e){}
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });

        rootNode.getChildren().addAll(startGame, exit, memeLabel);
        stage.show();
    }

    public BiElement<String, Integer> askIpAndPort(){
        TextField ipField = new TextField();
        TextField portField = new TextField();
        ipField.setPromptText("Insert Server IP Address");
        portField.setPromptText("Insert Server Port");



        String ip, port;
        ip = ipField.getText();
        port = portField.getText();

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
