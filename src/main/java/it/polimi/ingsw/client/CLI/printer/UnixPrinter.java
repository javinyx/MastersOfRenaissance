package it.polimi.ingsw.client.CLI.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class that specify the right command for Unix based consoles
 */

public class UnixPrinter extends Printer{
    @Override
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }


}
