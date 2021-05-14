package it.polimi.ingsw.client.CLI.printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Printer {
    protected final int SIDEBAR_SIZE = 51;

    public void clearScreen(){
        System.out.print("\n".repeat(60));
    }


    public void printAlignedCenter(String toPrint, int sizeToCenterComparedTo) {
        int blanks = (sizeToCenterComparedTo / 2 + (sizeToCenterComparedTo % 2 == 0 ? 0 : 1)) - (toPrint.length() / 2 + (toPrint.length() % 2 == 0 ? 0 : 1));
        String builder = " ".repeat(Math.max(0, blanks)) +
                toPrint;
        System.out.println(builder);
    }


}
