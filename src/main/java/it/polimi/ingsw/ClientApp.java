package it.polimi.ingsw;


import it.polimi.ingsw.client.CLI.CliController;
import it.polimi.ingsw.client.GUI.Gui;

import java.io.IOException;

public class ClientApp {
    /**
     * Starts a new client with the selected game type, chosen by using the argument.
     * @param args the game type, CLI or GUI
     */
    public static void main(String[] args) {
        boolean playAgain = true;

            if (args == null || args.length == 0 || args[0].equalsIgnoreCase("GUI")) {
                Gui.main(args);
                return;
            }
            while (playAgain) {
                CliController controller;
                String graphicOption= args[0].toUpperCase();
                if (graphicOption.equals("CLI"))
                    controller = new CliController();
                else
                    return;
                if (args.length > 1 && args[1].equals("local"))
                    controller.startLocalGame();
                else{
                    try {
                        playAgain = controller.setup();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

