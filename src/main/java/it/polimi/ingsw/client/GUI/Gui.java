package it.polimi.ingsw.client.GUI;

import javafx.application.Application;
import javafx.stage.Stage;


public class Gui extends Application{
    private GuiController controller;

    public static void main(String[] args){
        Gui.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        controller = new GuiController(stage, this);
    }
}
