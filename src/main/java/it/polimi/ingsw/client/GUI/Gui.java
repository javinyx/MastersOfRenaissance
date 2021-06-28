package it.polimi.ingsw.client.GUI;

import javafx.application.Application;
import javafx.stage.Stage;


/**
 * The GUI Version of the client, which extends Application.
 */
public class Gui extends Application{
    private GuiController controller;

    /**
     * The main method, its only purpose is to launch the application.
     *
     * @param args the args
     */
    public static void main(String[] args){
        Gui.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        controller = new GuiController(stage, this);
    }
}
