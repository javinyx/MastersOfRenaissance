package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.printer.Printer;
import it.polimi.ingsw.client.CLI.printer.UnixPrinter;
import it.polimi.ingsw.client.CLI.printer.WindowsPrinter;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Cli /*extends ViewInterface*/ {
    private final Scanner scanner = new Scanner(System.in);
    private CliController controller;
    private final Printer OSPrinter;
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private final int imageLength = 160;
    private final Scanner inputScanner = new Scanner(System.in);


    /**
     * Instantiates the correct version of {@link Printer} based on the current OS
     */
    public Cli(CliController controller){

        this.controller = controller;

        if (OS.contains("windows"))
            OSPrinter = new WindowsPrinter();
        else if (OS.contains("linux"))
            OSPrinter = new UnixPrinter();
        else OSPrinter = new Printer(); //mac-os
    }

    /**
     * Prints the initial screen
     * @return {@code true} if player pressed the play button
     */
    public boolean initialScreen() {
        OSPrinter.clearScreen();
        try {
            // Print Logo
            InputStream in = getClass().getResourceAsStream("/startLogo.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            for (int i = 0; i < 30; i++)
                System.out.println(reader.readLine());

            OSPrinter.printAlignedCenter("1: Start Game", imageLength);
            System.out.println("\n");
            OSPrinter.printAlignedCenter("2: Exit Game", imageLength);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> {
                        return true;
                    }
                    case "2" -> {
                        return false;
                    }
                    default -> System.out.println("That is not a valid option! Enter a valid one");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Asks for server port input
     * @return the input given port
     */
    public int askForPort() {
        System.out.println("Insert the port");
        Scanner scanner = new Scanner(System.in);
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * Asks for server ip input
     * @return the input given ip
     */
    public String askForIp() {
        OSPrinter.clearScreen();
        System.out.println("Insert the Server's IP address");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    /**
     * Sets a new {@link Thread} to constantly wait for input until his {@link InputChecker} goes inactive
     * @return the reading created {@link Thread}
     */
    public Thread startContinuousRead() {
        Thread t = new Thread(() -> {
            try {
                while (controller.isActive()) {
                    if (System.in.available()>0) {
                        String input = inputScanner.nextLine();
                        controller.checkInput(input);
                    }
                }
            } catch (Exception e) {
                controller.setActive(false);
                e.printStackTrace();
            }
        });
        t.start();
        return t;
    }

    /*public void printBoard() {
        OSPrinter.printBoard();
    }*/



    public String marketDimChoose() {
        String dim;
        do {
            System.out.println("Choose a row or a column from the market by typing 'r' or 'c'");
            dim = scanner.nextLine();
            dim = dim.toLowerCase();
        }while(!dim.equals("c") && !dim.equals("r"));

        return dim;

    }
    public int marketIntChoose(int upperBound, int lowerBound) {
        int index;
        do{
            System.out.println("Choose the index - must be between " + lowerBound + " and " + upperBound);
            index = scanner.nextInt();
        }while(index<lowerBound || index>upperBound);
        return index;
    }

    public int marketLeaderChoose(MarbleAbility card){
        System.out.println("How many for this card?\n" + card);
        return scanner.nextInt();
    }

    public int activeLeaderFromId(int activable){
        int id;
        do{
            System.out.println("You can activate " + activable + " leaders. Which one do you want to activate?" +
                    "Insert its id.\n" + activable);
            id = scanner.nextInt();
        }while (id > 0 && id < activable);

        return id;
    }






    //--------------------------------------------------------------------------------------------------------------------

    public void showMessage(String str){
        System.out.println(str);
    }




}
