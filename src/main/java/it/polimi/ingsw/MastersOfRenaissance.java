package it.polimi.ingsw;

import it.polimi.ingsw.client.CLI.CliController;
import it.polimi.ingsw.client.GUI.Gui;

import java.io.IOException;

public class MastersOfRenaissance {
    public static void main(String[] args) {
       boolean playAgain = true;

        while (playAgain) {
            CliController controller;
            //String graphicOption= args[0].toUpperCase();
            /*if (graphicOption.equals("CLI"))*/
            controller = new CliController();
            //else return;
            /*try {
                playAgain = controller.setup();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            controller.startLocalGame();
            break;

        }
    }
}
