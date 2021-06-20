package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.CLI.printer.Printer;
import it.polimi.ingsw.client.CLI.printer.UnixPrinter;
import it.polimi.ingsw.client.CLI.printer.WindowsPrinter;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.cards.leader.StorageAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class that handle all the input that a user has to insert in the teminal
 */

public class Cli /*extends MessageDispatcher*/ {
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

    /**
     * Allow user to choose a resource
     * @param quantity number of resources to choose
     */
    public List<Resource> chooseResources(int quantity){
        List<Resource> resources = new ArrayList<>();
        String res;

        for (int i = 0; i < quantity; i++){
            do {
                System.out.println("Choose a Resources by typing: Stone, Shield, Servant, Coin: ");
                res = scanner.next().toLowerCase();
            }while (!res.equals("stone") && !res.equals("shield") && !res.equals("servant") && !res.equals("coin"));

            resources.add(convertStringToResource(res));
        }

        return resources;
    }

    /**
     * Let the user choose the two initial leader of the game
     * @param leaders the list of the 4 selectable leaders
     */
    public List<Integer> chooseInitialLeader(List<LeaderCard> leaders){

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

    /**
     * Let the user choose a leader by type its ID
     * @param leaders the list of the selectable leaders
     */
    public List<Integer> chooseLeader(List<LeaderCard> leaders){

        int c = -1;
        List<Integer> lID = new ArrayList<>();
        List<LeaderCard> leadBack = new ArrayList<>(leaders);

        for (int i = 0; i < leaders.size(); i++) {
            OSPrinter.printLeaders(leaders.get(i));
        }

        System.out.println("Choose the ID of the two card you want:");

        for (int i = 0; i < 2; i++) {
            boolean lock;
            do {
                lock = true;
                System.out.println("Select the Leader Card you want to use, press 0 to skip selection");
                c = scanner.nextInt();
                if (c != 0) {
                    for (LeaderCard lead : leaders)
                        if (lead.getId() == c) {
                            lID.add(lead.getId());
                            lock = false;
                            break;
                        }
                }
                else
                    break;
            } while (!lock);
        }
        return lID;
    }


    // GAME PHASE --------------------------------------------------------------------------------------------

    public void displayTurnOption(){

        int choice = 0;

        OSPrinter.clearScreen();
        OSPrinter.printBoard(controller.getPlayer(), controller.getMarket());

        OSPrinter.printTurnOptions();

        do {
            System.out.println("Select a number:");
            choice = scanner.nextInt();
        } while (choice < 1 || (choice > 9 && choice != 42));

        switch (choice) {
            case 1 -> controller.buyFromMarket();
            case 2 -> controller.buyProductionCard();
            case 3 -> controller.startProduction();
            case 4 -> controller.activateLeader();
            case 5 -> controller.discardLeader();
            case 6 -> controller.viewOpponents();
            case 7 -> controller.viewYourInfo();
            case 8 -> controller.viewProductionCard();
            case 9 -> controller.rearrangeWarehouse();
            //case 10 -> controller.passTurn();
            case 42 -> controller.normalProcedure();
        }

    }

    public void displayLightTurnOption(){
        int choice = 0;

        OSPrinter.clearScreen();
        OSPrinter.printBoard(controller.getPlayer(), controller.getMarket());

        OSPrinter.printLightTurnOptions();
        do {
            System.out.println("Select a number:");
            choice = scanner.nextInt();
        } while (choice < 1 || (choice > 7 && choice != 42));

        switch (choice) {
            case 1 -> controller.activateLeader();
            case 2 -> controller.discardLeader();
            case 3 -> controller.viewOpponents();
            case 4 -> controller.viewYourInfo();
            case 5 -> controller.viewProductionCard();
            case 6 -> controller.rearrangeWarehouse();
            case 7 -> controller.passTurn();
            case 42 -> controller.normalProcedure();
        }


    }

    /**
     * Like a C System("Pause") statement
     */
    public void pressEnter(){
        System.out.print("Press enter to continue.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearScreen(){
        OSPrinter.clearScreen();
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

    /**
     * Let the user select a development card
     */
    public List<ConcreteProductionCard> selectProdCard(){

        List<ConcreteProductionCard> card = new ArrayList<>();
        List<ConcreteProductionCard> retCard = new ArrayList<>();
        int c = -1;

        for (int i = 0; i < 3; i++) {
            if (controller.getPlayer().getProductionStacks().get(i).size() != 0) {
                card.add(controller.getPlayer().getProductionStacks().get(i).peekFirst());
                OSPrinter.printProductionCard(card.get(i));
            }
        }

        while (c != 0) {
            boolean lock;
            do {
                lock = true;
                System.out.println("Select the Development Card you want to use, press 0 to exit selection");
                c = scanner.nextInt();
                for(ConcreteProductionCard card1 : card)
                    if (card1.getId() == c) {
                        lock = false;
                        retCard.add(card1);
                        card.remove(card1);
                        break;
                    }
            } while (!lock);
        }
        return retCard;
    }

    /**
     * Let the user select the resources tht he want to use for a generic action
     * the parameters are the pointer of the lists that arrive from the CliController
     */
    public void selectResWalletProd(List<ConcreteProductionCard> prodCard, List<Resource> fromWare, List<Resource> fromLoot, List<Resource> fromLeader1, List<Resource> fromLeader2){
        int c, numLeader = -1, lock;
        List<StorageAbility> storeLead = new ArrayList<>();

        for (LeaderCard led : controller.getPlayer().getLeaders()) {
            if (led instanceof StorageAbility && led.isActive())
                storeLead.add((StorageAbility) led);
        }

        for (ConcreteProductionCard pc : prodCard) {
            int count = 1;
            for(Resource res : pc.getCost()) {
                System.out.print("From where do you want to take the resource number "+ count + " ("+res+") for the Development Card with ID number " + pc.getId() + "?\n (1) Warehouse\n (2) StrongBox\n (3) Leader Extra storage\n");
                do {
                    lock = 1;
                    System.out.print("Choice: ");
                    c = scanner.nextInt();
                    if (c == 3){
                        if (storeLead.size() == 0)
                            System.out.println("You haven't any eligible leader");
                        lock =  0;
                    }
                } while (c != 1 && c != 2 && c != 3 || lock != 1);

                switch (c) {
                    case 1 -> fromWare.add(selectProdRes());
                    case 2 -> fromLoot.add(selectProdRes());
                    case 3 -> {
                        BiElement<Resource, Integer> bi = selectProdResLeader(storeLead);
                        numLeader = bi.getSecondValue();
                        if (numLeader == 0)
                            fromLeader1.add(bi.getFirstValue());
                        else
                            fromLeader2.add(bi.getFirstValue());
                    }
                }

                count++;
            }
        }
    }

    /**
     * Let the user select the resources tht he want to use for a Development card purchase action
     * the parameters are the pointer of the lists that arrive from the CliController
     */
    public void selectWalletForProduction(List<ConcreteProductionCard> prodCard, List<Resource> fromWare, List<Resource> fromLoot, List<Resource> fromLeader1, List<Resource> fromLeader2){
        int c, numLeader = -1, lock;
        List<StorageAbility> storeLead = new ArrayList<>();

        for (LeaderCard led : controller.getPlayer().getLeaders()) {
            if (led instanceof StorageAbility && led.isActive())
                storeLead.add((StorageAbility) led);
        }

        for (ConcreteProductionCard pc : prodCard) {
            int count = 1;
            for(Resource res : pc.getRequiredResources()) {
                System.out.print("From where do you want to take the resource number "+ count + " ("+res+") for the Development Card with ID number " + pc.getId() + "?\n (1) Warehouse\n (2) StrongBox\n (3) Leader Extra storage\n");
                do {
                    lock = 1;
                    System.out.print("Choice: ");
                    c = scanner.nextInt();
                    if (c == 3){
                        if (storeLead.size() == 0)
                            System.out.println("You haven't any eligible leader");
                        lock =  0;
                    }
                } while (c != 1 && c != 2 && c != 3 || lock != 1);

                switch (c) {
                    case 1 -> fromWare.add(selectProdRes());
                    case 2 -> fromLoot.add(selectProdRes());
                    case 3 -> {
                        BiElement<Resource, Integer> bi = selectProdResLeader(storeLead);
                        numLeader = bi.getSecondValue();
                        if (numLeader == 0)
                            fromLeader1.add(bi.getFirstValue());
                        else
                            fromLeader2.add(bi.getFirstValue());
                    }
                }

                count++;
            }
        }
    }

    private Resource selectProdRes(){
        String res;
            do {
                System.out.print("Resource: ");
                res = scanner.next().toLowerCase();
            } while (!res.equals("stone") && !res.equals("servant") && !res.equals("shield") && !res.equals("coin") && !res.equals("0"));

        return convertStringToResource(res);
    }

    private BiElement<Resource, Integer> selectProdResLeader(List<StorageAbility> leadCard){
        String res;
        int c;

        System.out.println("Which leader do you want to select");
        for (int i = 0; i < 2; i++) {
           OSPrinter.printLeaders((LeaderCard)leadCard);
           System.out.println("Resource contained: " + leadCard.get(i).get0());
           System.out.println("Resource contained: " + leadCard.get(i).get1());
        }
        do {
            System.out.print("ID: ");
            c = scanner.nextInt();
        } while (leadCard.get(0).getId() != c && leadCard.get(1).getId() != c);
        do {
            System.out.print("Resource: ");
            res = scanner.next().toLowerCase();
        } while (!res.equals("stone") && !res.equals("servant") && !res.equals("shield") && !res.equals("coin") && !res.equals("0"));

        if(c == leadCard.get(0).getId())
            c = 0;
        else
            c = 1;

        return new BiElement<>(convertStringToResource(res), c);
    }

    /**
     * Let the user select the resources tht he want to use for a Leader action
     * the parameters are the pointer of the lists that arrive from the CliController
     */
    public void selectLeadWalletProd(BoostAbility card, List<Resource> fromWare, List<Resource> fromLoot, List<Resource> fromLeader1, List<Resource> fromLeader2){
        int c, numLeader = -1, lock;
        List<StorageAbility> storeLead = new ArrayList<>();

        System.out.print("From where do you want to take the resource for the Leader Card with ID number " + card.getId() + "?\n (1) Warehouse\n (2) StrongBox\n (3) Leader Extra storage");
        do {
            lock = 1;
            System.out.print("Choice: ");
            c = scanner.nextInt();
            if (c == 3){
                for (LeaderCard led : controller.getPlayer().getLeaders()) {
                    if (led instanceof StorageAbility && led.isActive())
                        storeLead.add((StorageAbility) led);
                }
                if (storeLead.size() == 0)
                    System.out.println("You haven't any eligible leader");
                lock =  0;
            }
        } while (c != 1 && c != 2 && c != 3 || lock != 1);

        switch(c){
            case 1 -> fromWare.add(selectProdRes());
            case 2 -> fromLoot.add(selectProdRes());
            case 3 -> {
                BiElement<Resource, Integer> bi = selectProdResLeader(storeLead);
                numLeader = bi.getSecondValue();
                if (numLeader == 0)
                    fromLeader1.add(bi.getFirstValue());
                else
                    fromLeader2.add(bi.getFirstValue());
            }
        }
    }


    /**
     * Part 1 of the basic Production process
     * @return the resources you want to spend
     */
    public List<Resource> doBasicProd1(List<Resource> fromWare, List<Resource> fromLoot, List<Resource> fromLeader1, List<Resource> fromLeader2) {

        String c, res;
        int c1, numLeader = -1, lock;
        List<Resource> in = new ArrayList<>();
        List<StorageAbility> storeLead = new ArrayList<>();

        System.out.println("Do you want to use basic production?");
        do {
            System.out.println("(y)es or (n)o");
            c = scanner.next().toLowerCase();
        } while (!c.equals("y") && !c.equals("n"));

        if (c.equals("y") && controller.getPlayer().getAllResources().size() > 1) {
            System.out.println("Select the 2 resources you want to use");
            for (int i = 1; i < 3; i++) {

                System.out.print("From where do you want to take the resource number "+i+" for the Basic Production?\n (1) Warehouse\n (2) StrongBox\n (3) Leader Extra storage");

                do {
                    lock = 1;
                    System.out.print("Choice: ");
                    c1 = scanner.nextInt();
                    if (c1 == 3){
                        for (LeaderCard led : controller.getPlayer().getLeaders()) {
                            if (led instanceof StorageAbility && led.isActive())
                                storeLead.add((StorageAbility) led);
                        }
                        if (storeLead.size() == 0)
                            System.out.println("You haven't any eligible leader");
                        lock =  0;
                    }
                } while (c1 != 1 && c1 != 2 && c1 != 3 || lock != 1);

                switch(c1){
                    case 1 -> fromWare.add(selectProdRes());
                    case 2 -> fromLoot.add(selectProdRes());
                    case 3 -> {

                        BiElement<Resource, Integer> bi = selectProdResLeader(storeLead);
                        numLeader = bi.getSecondValue();
                        if (numLeader == 0)
                            fromLeader1.add(bi.getFirstValue());
                        else
                            fromLeader2.add(bi.getFirstValue());
                    }
                }
            }

            return in;
        }

        else
            return null;
    }

    /**
     * Part 1 of the basic Production process
     * @return the resources you want to produce
     */
    public Resource doBasicProd2(){

        String res;

        System.out.println("Select the resources you want to produce");
        do {
            System.out.print("Resource:");
            res = scanner.next().toLowerCase();
        } while (!res.equals("stone") && !res.equals("servant") && !res.equals("shield") && !res.equals("coin"));

        return convertStringToResource(res);

    }

    /**
     * @return the resources you want to produce from a leader card
     */
    public List<Resource> chooseLeaderOut(List<LeaderCard> leaderCards){
        List<Resource> resRet = new ArrayList<>();
        String res;

        System.out.println("Witch type of resource do you want to produce with your leader? 'Stone', 'Servant', 'Shield' or 'Coin'.");
        for (int i = 0; i < leaderCards.size(); i++) {
            do {
                System.out.print("Resource from leader number " + i + " :");
                res = scanner.next().toLowerCase();
            } while (!res.equals("stone") && !res.equals("servant") && !res.equals("shield") && !res.equals("coin"));

            resRet.add(convertStringToResource(res));
        }

        return resRet;
    }

    /**
     * @return true if the player want to play a leader during an action
     */
    public boolean wantPlayLeader(){
        String c;

        System.out.println("Do you want to play some leader?");
        do {
            System.out.println("(y)es or (n)o");
            c = scanner.next().toLowerCase();
        } while (!c.equals("y") && !c.equals("n"));

        return c.equals("y");

    }

    /**
     * Let the player choose row or column
     */
    public String marketDimChoose() {
        String dim;
        do {
            System.out.println("Choose a row or a column from the market by typing 'r' or 'c'");
            dim = scanner.next().toLowerCase();
        }while(!dim.equals("c") && !dim.equals("r"));

        return dim;

    }
    /**
     * Let the player choose the index
     */
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

    /**
     * Let the player choose where he want to put the resources that has arrived from
     * @param res
     */
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

    /**
     * Let the player activate a leader
     */
    public int activeLeaderFromId(List<LeaderCard> activable){
        int id, lock;
        for (int i = 0; i < activable.size(); i++) {
            OSPrinter.printLeaders(activable.get(i));
        }
        System.out.println("You can activate " + activable.size() + " leaders. Which one do you want to activate?");
        do{
            lock = 1;
            System.out.print("ID: ");
            id = scanner.nextInt();
            for (LeaderCard lead : activable)
                if (id != lead.getId())
                    lock = 0;
        }while (lock != 0);

        return id;
    }


    public void showProductionCardInTheStore(List<ConcreteProductionCard> prodCard){
        System.out.println("You can buy one of these card");
        for (ConcreteProductionCard p :prodCard)
            OSPrinter.printProductionCard(p);
        OSPrinter.printProductionStoreGrid(prodCard);
    }

    public int chooseProdCard(List<ConcreteProductionCard> prodCard){

        int c = 0;
        boolean lock = true;

        System.out.println("Choose the card you want to buy by typing its ID");
        while(lock) {
            System.out.print("ID: ");
            c = scanner.nextInt();

            for (ConcreteProductionCard concreteProductionCard : prodCard) {
                if (concreteProductionCard.getId() == c) {
                    lock = false;
                    break;
                }
            }
        }
        return c;
    }

    public int chooseProdStack(){
        int c = 0;
        do {
            System.out.print("Choose the stack where you want to put the Development Card: 1, 2 or 3: ");
            c = scanner.nextInt();
        } while (c != 1 && c != 2 && c != 3);
        return c;
    }

    public void showPlayerProdCard(List<Deque<ConcreteProductionCard>> prodStack){
        System.out.println("Development cards:");
        if (prodStack.get(0).size() == 0 && prodStack.get(1).size() == 0 && prodStack.get(2).size() == 0)
            System.out.println("There are no Development card");
        else {
            int i = 1;
            for (Deque d : prodStack) {
                if (d.size() != 0) {
                    System.out.println("In the stuck number " + i + " there is: ");
                    OSPrinter.printProductionCard(prodStack.get(i - 1).peekFirst());
                    i++;
                }
            }
        }
    }

    public void showPlayerLeader(List<LeaderCard> leadCard){
        System.out.println("Leader cards:");
        if(leadCard.size() != 0)
            for (LeaderCard ld : leadCard){
                if (ld != null && ld.isActive())
                    OSPrinter.printLeaders(ld);
                 else
                    System.out.println("None");
            }
        else
            System.out.println("There are no Leader card");

    }

    public void showYourLeader(List<LeaderCard> leadCard){
        System.out.println("Leader cards:");
        if(leadCard.size() != 0)
            for (LeaderCard ld : leadCard){
                if (ld != null) {
                    OSPrinter.printLeaders(ld);
                    System.out.print("This leader is: ");
                    System.out.println(ld.isActive()? "ACTIVATE" : "INACTIVE");
                }
                else
                    System.out.println("None");
            }
        else
            System.out.println("There are no Leader card");

    }

    public void showPlayerResources(Map<BiElement<Resource, Storage>, Integer> res){
        List<Resource> extra = new ArrayList<>();
        Resource small = null;
        List<Resource> mid = new ArrayList<>();
        List<Resource> large = new ArrayList<>();

        int shield = 0, stone = 0, ser = 0, coin = 0;

        System.out.println("Resources: ");

        if (res.size() != 0) {
            for (BiElement<Resource, Storage> bi : res.keySet()) {
                switch (bi.getSecondValue()) {
                    case WAREHOUSE_SMALL -> small = bi.getFirstValue();
                    case WAREHOUSE_MID -> {
                        for (int i = 0; i < res.get(bi); i++) {
                            mid.add(bi.getFirstValue());
                        }
                    }
                    case WAREHOUSE_LARGE -> {
                        for (int i = 0; i < res.get(bi); i++) {
                            large.add(bi.getFirstValue());
                        }
                    }
                    case LOOTCHEST -> {
                        switch (bi.getFirstValue()){
                            case SERVANT -> ser++;
                            case SHIELD -> shield++;
                            case STONE -> stone++;
                            case COIN -> coin++;
                        }
                    }
                    case EXTRA1, EXTRA2 -> extra.add(bi.getFirstValue());
                }
            }
            System.out.println("The warehouse is:");
            System.out.println("Small shelf: " + small);
            System.out.println("Middle shelf: " + mid);
            System.out.println("Large shelf: " + large);

            System.out.println("In the Strong Box there are:");
            System.out.println("Servant: " + ser);
            System.out.println("Stone: " + stone);
            System.out.println("Shield: " + shield);
            System.out.println("Coin: " + coin);

        }
        else
            System.out.println("There are no Resources");
    }

    public void printResource(Resource res){
        System.out.print(OSPrinter.printRes(res));
    }

    public void showPlayerBoard(){ OSPrinter.printBoard(controller.getPlayer(), controller.getMarket()); }

    public String askNick(){
        String nick;
        do {
            nick = scanner.next();
        } while (nick.length() > 12);

        return nick;
    }

    //--------------------------------------------------------------------------------------------------------------------

    /**
     * Print a message in the terminal
     */
    public void showMessage(String str){
        System.out.println(str);
    }

    private Resource convertStringToResource(String res){
        return switch (res) {
            case "stone" -> Resource.STONE;
            case "servant" -> Resource.SERVANT;
            case "coin" -> Resource.COIN;
            case "shield" -> Resource.SHIELD;
            default -> null;
        };
    }



}
