package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.printer.Printer;
import it.polimi.ingsw.client.CLI.printer.UnixPrinter;
import it.polimi.ingsw.client.CLI.printer.WindowsPrinter;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.Warehouse;

import java.awt.*;
import java.awt.geom.RectangularShape;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
            for (int i = 0; i < 34; i++)
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
     * Sets a new {@link Thread} to constantly wait for input until controller goes inactive
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


    // SETUP PHASE -----------------------------------------------------------------------------------------------------

    public List<Resource> chooseResources(int quantity){
        //List<BiElement<Resource, Storage>> resources = new ArrayList<>();
        List<Resource> resources = new ArrayList<>();
        String res;
        int pos;
        Storage stor = null;

        for (int i = 0; i < quantity; i++){
            do {
                System.out.println("Choose a Resources by typing: Stone, Shield, Servant, Coin: ");
                res = scanner.next().toLowerCase();
            }while (!res.equals("stone") && !res.equals("shield") && !res.equals("servant") && !res.equals("coin"));

            /*do{
                System.out.println("Where do you want to store it in the warehouse? Small(1), Medium(2), Large(3)");
                pos = scanner.nextInt();
            } while (pos != 1 && pos != 2 && pos != 3);

            switch (pos){
                case 1 -> stor = Storage.WAREHOUSE_SMALL;
                case 2 -> stor = Storage.WAREHOUSE_MID;
                case 3 -> stor = Storage.WAREHOUSE_LARGE;
            }*/

            switch(res){
                case "stone" -> resources.add(Resource.STONE);
                case "servant" -> resources.add(Resource.SERVANT);
                case "coin" -> resources.add(Resource.COIN);
                case "shield" -> resources.add(Resource.SHIELD);
            }
        }

        return resources;
    }

    public List<Integer> chooseLeader(List<LeaderCard> leaders){

        List<Integer> lID = new ArrayList<>();

        for (int i = 0; i < leaders.size(); i++) {
            OSPrinter.printLeaders(leaders.get(i));
        }

        System.out.println("Choose the ID of the two card you want:");

        for (int i = 1; i < 3; i++) {
            boolean lock = true;
            while (lock) {
                System.out.print("Leader " + i + ": ");
                int t = scanner.nextInt();

                if (t == leaders.get(0).getId() || t == leaders.get(1).getId() || t == leaders.get(2).getId() || t == leaders.get(3).getId()) {
                    if (i == 1) {
                        lock = false;
                        lID.add(t);
                    }
                    else {
                        if (t != lID.get(0)){
                            lock = false;
                            lID.add(t);
                        }
                    }
                }
            }
        }

        return lID;
    }


    // GAME PHASE --------------------------------------------------------------------------------------------

    public void displayTurnOption(){

        int choice;

        OSPrinter.printTable();

        OSPrinter.printTurnOptions();

        do{
            System.out.println("Select a number:");
            choice = scanner.nextInt();
        } while (choice < 1 || choice > 7);

        switch(choice){
            case 1 -> controller.buyFromMarket();
            case 2 -> controller.buyProductionCard();
            case 3 -> controller.startProduction();
            case 4 -> controller.viewOpponents();
            case 5 -> controller.activateLeader();
            case 6 -> controller.discardLeader();
            case 7 -> controller.setWantPlay(false);
        }

    }

    public void displayLightTurnOption(){

        int choice;

        OSPrinter.printLightTurnOptions();

        do{
            System.out.println("Select a number:");
            choice = scanner.nextInt();
        } while (choice < 1 || choice > 4);

        switch(choice){
            case 1 -> controller.viewOpponents();
            case 2 -> controller.activateLeader();
            case 3 -> controller.discardLeader();
            case 4 -> controller.setWantPlay(false);
        }

    }

    public int discardLeader(List<LeaderCard> led){

        int c;

        System.out.println("Select the leader you want to discard by typing its ID");

        if (led.size() != 0){
            for (LeaderCard leaderCard : led)
                OSPrinter.printLeaders(leaderCard);
            do{
                System.out.println("Select leader:");
                c = scanner.nextInt();
            } while (c != led.get(0).getId() && c != led.get(1).getId());
            return c;
        }
        else
            System.out.println("You don't have any leader to discard");

        return -1;
    }

    public String marketDimChoose() {
        String dim;
        do {
            System.out.println("Choose a row or a column from the market by typing 'r' or 'c'");
            dim = scanner.next().toLowerCase();
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
    
    public List<BiElement<Resource, Storage>> storeResources(List<Resource> res){
        int c;
        Storage s = null;

        List<BiElement<Resource, Storage>> pos = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            if(res.get(i) != null){
                int var;
                do {
                    var = 0;
                    System.out.println("Where do you want to store " + res.get(i) + "? Small(1), Medium(2), Large(3). Press 0 if you want to discard it");
                    c = scanner.nextInt();

                    if (controller.isRegistrationPhase() && c == 0) {
                        System.out.println("There's a time and place for everything, but not now!");
                        var = 1;
                    }

                } while (c != 1 && c != 2 && c != 3 && c != 0 || var != 0);

                switch(c){
                    case 1 -> s = Storage.WAREHOUSE_SMALL;
                    case 2 -> s = Storage.WAREHOUSE_MID;
                    case 3 -> s = Storage.WAREHOUSE_LARGE;
                    case 0 -> s = Storage.DISCARD;
                }

                pos.add(new BiElement<>(res.get(i), s));
            }
        }
        return pos;
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
