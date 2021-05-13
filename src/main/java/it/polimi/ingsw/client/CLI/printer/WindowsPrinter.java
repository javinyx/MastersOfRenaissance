package it.polimi.ingsw.client.CLI.printer;

import java.io.IOException;

public class WindowsPrinter extends Printer{
    @Override
    public void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
