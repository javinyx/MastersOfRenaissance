package it.polimi.ingsw.client.CLI.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnixPrinter extends Printer{
    @Override
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
