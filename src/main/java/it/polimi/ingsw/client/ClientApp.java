package it.polimi.ingsw.client;

import java.io.IOException;

public class ClientApp {
    /**
     * Starts a new client with the selected game type, chosen by using the argument.
     * @param args the game type, CLI or GUI
     */
    public static void main(String[] args) {
        boolean playAgain = true;

        /*
        try {
            if(args == null || args.length == 0 || args[0].toUpperCase().equals("GUI")){
                //Gui.main(args);
                return;
            }
            while(playAgain) {
                CliController controller;
                String graphicOption= args[0].toUpperCase();
                if (graphicOption.equals("CLI")) controller= new CliController();
                else return;
                playAgain=controller.startSetup();

            }
        } catch (IOException e) {
            System.err.println("Unable to initialize the client: " + e.getMessage());
        }
         */
    }
}
