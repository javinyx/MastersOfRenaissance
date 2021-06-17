package it.polimi.ingsw.client.CLI.printer;

import it.polimi.ingsw.client.CLI.Color;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.IOException;

/**
 * Class that specify the right command for Windows based consoles
 */

public class WindowsPrinter extends Printer{
    @Override
    public void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    /*@Override
    public String printRes (Resource res){
        String s = null;
        switch (res){
            case COIN -> s = "  COIN    ";
            case SERVANT -> s = "  SERVANT ";
            case STONE -> s = "  STONE   ";
            case SHIELD -> s = "  SHIELD  ";
            case BLANK -> s = "  BLANK   ";
            case FAITH -> s = "  FAITH   ";
        }
        return s;
    }
    @Override
    public String printColorprod (ConcreteProductionCard p){
        String s = null;
        switch (p.getColor()){
            case BLUE -> s = " BLUE   ";
            case GREEN -> s = " GREEN  ";
            case PURPLE -> s = " PURPLE ";
            case YELLOW -> s = " YELLOW ";
        }
        return s;
    }

    public void printFaithTrack(int currPos){

        for (int i = 0; i < 10; i++) {
            System.out.print("   "+i+"  ");
        }
        for (int i = 10; i < 25; i++) {
            System.out.print(Color.RESET+ "  "+i+"  ");
        }
        System.out.println();
        StringBuilder lines = new StringBuilder();
        lines.append("-".repeat(150));
        System.out.println(lines);

        for (int i = 0; i < 25; i++) {
            if (currPos == i) {
                if (i == 24) {
                    System.out.print("│  + │");
                } else {
                    System.out.print("│  +  ");
                }
            }
            else {
                if (i == 24) {
                    System.out.print("│    │");
                } else {
                    System.out.print("│     ");
                }
            }
        }
        System.out.println("\n"+lines);
        System.out.print(Color.RESET);

    }

    @Override
    public void printMarket(Market market, Boolean[] pope){
        StringBuilder s = new StringBuilder();
        s.append("   ").append("-".repeat(52)).append(" ".repeat(47)).append("Pope Favor Box");;
        System.out.println(s);

        for (int i = 0; i < 3; i++){
            System.out.print("   |");
            for (int j = 0; j < 4; j++)
                System.out.print(printRes(market.getMarketBoard()[i][j])+" | ");
            System.out.print("<- ");

            if(i == 1)
                System.out.print("\t Extra Marble: " + printRes(market.getExtra()));

            switch (i){
                case 0, 2 -> System.out.print(" ".repeat(37)+ "-----     -----     -----");
                case 1 -> {
                    int j = 0;
                    if(!pope[0])
                        j = -1;
                    else {
                        if (pope[1])
                            j = 1;
                        if (pope[2])
                            j = 2;
                    }
                    switch (j) {
                        case -1 -> System.out.print(" ".repeat(7) + "|   |     |   |     |   |");
                        case 0 ->  System.out.print(" ".repeat(7) + "| X |     |   |     |   |");
                        case 1 ->  System.out.print(" ".repeat(7) + "| X |     | X |     |   |");
                        case 2 ->  System.out.print(" ".repeat(7) + "| X |     | X |     | X |");
                    }
                }
            }

            System.out.println();
        }
        s = new StringBuilder();
        s.append("   ").append("-".repeat(52));
        System.out.println(s);
        System.out.println("         ^            ^            ^            ^");
        System.out.println("         |            |            |            |");
    }

    public void printFirstLineGridProdCard(){
        String spaces = " ".repeat(7);
        System.out.print(spaces + "GREEN " + spaces + "|");
        System.out.print(spaces + "BLUE  " + spaces + "|");
        System.out.print(spaces + "YELLOW" + spaces + "|");
        System.out.print(spaces + "PURPLE" + spaces + "|");
    }*/
}
