package it.polimi.ingsw.client.CLI.printer;

import it.polimi.ingsw.client.CLI.Color;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;


import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles the screen printing on the cli intreface
 */
public class Printer {

    public void clearScreen() {
        System.out.print("\n".repeat(60));
    }


    public void printAlignedCenter(String toPrint, int sizeToCenterComparedTo) {
        int blanks = (sizeToCenterComparedTo / 2 + (sizeToCenterComparedTo % 2 == 0 ? 0 : 1)) - (toPrint.length() / 2 + (toPrint.length() % 2 == 0 ? 0 : 1));
        String builder = " ".repeat(Math.max(0, blanks)) +
                toPrint;
        System.out.println(builder);
    }

    /**
     * Print the leader cards
     * @param card the leader card you want to print
     */
    public void printLeaders(LeaderCard card) {

        List<Buyable> cost = card.getCost();
        Resource res;
        String lines = "-".repeat(150);

        System.out.println(lines);
        System.out.println("Leader Card with ID: " + card.getId());
        System.out.println("Victory Points: " + card.getVictoryPoints());

        switch (card.getNameNew()) {

            case "StorageAbility" -> {
                int count;
                res = (Resource) cost.get(0);
                for (count = 0; count < cost.size(); count++) ;

                System.out.println("Cost: " + count + " " + res.toString());
                System.out.println("Power: add 2 extra space for the " + res + " resource");

            }

            case "MarbleAbility" -> {
                doStuffProdCard(card);
                System.out.println("Power: convert a blank marble in the market into a " + ((MarbleAbility) card).getReplacingResource() + " resource");
            }

            case "DiscountAbility" -> {
                doStuffProdCard(card);
                System.out.println("Power: discount a Development Card with one less " + ((DiscountAbility) card).getDiscountType() + " in the cost");
            }

            case "BoostAbility" -> {
                doStuffProdCard(card);
                System.out.println("Power: spend a " + ((BoostAbility) card).getResource() + " and receive a resource you choose and an extra Faith Point");
            }
        }

        System.out.println(lines);
    }

    private void doStuffProdCard(LeaderCard card) {
        List<ConcreteProductionCard> lp = new ArrayList<>();

        for (int i = 0; i < card.getCost().size(); i++) {
            lp.add((ConcreteProductionCard) card.getCost().get(i));
        }

        System.out.println("Cost: " + lp.size() + " Development Card:");
        for (int i = 0; i < lp.size(); i++) {
            System.out.println("                         " + "*Development Card number " + i + ": Level: " + lp.get(i).getLevel() + "; Color: " + lp.get(i).getColor());
        }
    }

    public void printTurnOptions() {
        System.out.println("Now it's your turn, you can choose between these by typing the number:\n" +
                "1)  Buy from market: you can buy Resources from the market\n" +
                "2)  Buy a Development Card\n" +
                "3)  Start production: you can produce Resources\n" +
                "4)  Activate a Leader Card\n" +
                "5)  Discard Leader Card\n" +
                "6)  View your opponents status\n" +
                "7)  View your status\n" +
                "8)  View Development Card that you can buy\n" +
                "9)  Rearrange the resource in the warehouse");
    }

    public void printLightTurnOptions() {
        System.out.println("Now it's your turn, you can choose between these by typing the number:\n" +
                "1) Activate a Leader Card\n" +
                "2) Discard Leader Card\n" +
                "3) View your opponents status\n" +
                "4) View your status\n" +
                "5) View Development Card that you can buy\n" +
                "6) Rearrange the resource in the warehouse\n" +
                "7) End turn");
    }

    public void printFaithTrack(int currPos){
        //Max 158 caratteri in powershell misura max ideale = 144 --> 144/24 = 6

        for (int i = 0; i < 10; i++) {
            switch (i){
                case 5,6,7 -> System.out.print(Color.YELLOW.escape()+ "   "+i+"  ");
                case 8 -> System.out.print(Color.RED.escape()+ "   "+i+"  ");
                default -> System.out.print(Color.RESET+ "   "+i+"  ");
            }

        }
        for (int i = 10; i < 25; i++) {
            switch (i){
                case 12,13,14,15,19,20,21,22,23 -> System.out.print(Color.YELLOW.escape()+ "  "+i+"  ");
                case 16,24 -> System.out.print(Color.RED.escape()+ "  "+i+"  ");
                default -> System.out.print(Color.RESET+ "  "+i+"  ");
            }
        }
        System.out.println();
        StringBuilder lines = new StringBuilder();
        lines.append(Color.RESET + "-".repeat(30)).append(Color.YELLOW.escape() + "-".repeat(18)).append(Color.RED.escape() + "-".repeat(7)).append(Color.RESET + "-".repeat(17)).append(Color.YELLOW.escape() + "-".repeat(24)).append(Color.RED.escape() + "-".repeat(7)).append(Color.RESET + "-".repeat(11)).append(Color.YELLOW.escape() + "-".repeat(30)).append(Color.RED.escape() + "-".repeat(6));
        System.out.println(lines);

        for (int i = 0; i < 25; i++) {
            if (currPos == i)
                switch (i){
                    case 5,6,7,12,13,14,15,20,21,22,23 -> System.out.print(Color.YELLOW.escape()+ "│  †  ");
                    case 8,9,16,17 -> System.out.print(Color.RED.escape()+ "│  †  ");
                    case 24 -> System.out.print(Color.RED.escape()+ "│  † │");
                    default -> System.out.print(Color.RESET+ "│  †  ");
                }
            else
                switch (i){
                    case 5,6,7,12,13,14,15,20,21,22,23 -> System.out.print(Color.YELLOW.escape()+ "│     ");
                    case 8,9,16,17 -> System.out.print(Color.RED.escape()+ "│     ");
                    case 24 -> System.out.print(Color.RED.escape()+ "│    │");
                    default -> System.out.print(Color.RESET+ "│     ");
                }
        }
        System.out.println("\n"+lines);
        System.out.print(Color.RESET);

    }

    public void printMarket(Market market, Boolean[] pope){
        StringBuilder s = new StringBuilder();
        s.append("   ").append("-".repeat(52)).append(" ".repeat(47)).append("Pope Favor Box");
        System.out.println(s);

        for (int i = 0; i < 3; i++){
            System.out.print("   |");
            for (int j = 0; j < 4; j++)
                System.out.print(printRes(market.getMarketBoard()[i][j])+" | ");
            System.out.print("←— ");

            if(i == 1)
                System.out.print("\t Extra Marble: " + printRes(market.getExtra()));
            switch (i){
                case 0, 2 -> System.out.print(" ".repeat(37)+ Color.RED.escape() + "-----     -----     -----" + Color.RESET);
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
                        case -1 -> System.out.print(" ".repeat(7) + Color.RED.escape() + "|   |     |   |     |   |" + Color.RESET);
                        case 0 ->  System.out.print(" ".repeat(7) + Color.RED.escape() + "| X |     |   |     |   |" + Color.RESET);
                        case 1 ->  System.out.print(" ".repeat(7) + Color.RED.escape() + "| X |     | X |     |   |" + Color.RESET);
                        case 2 ->  System.out.print(" ".repeat(7) + Color.RED.escape() + "| X |     | X |     | X |" + Color.RESET);
                    }
                }
            }
            System.out.println();
        }
        s = new StringBuilder();
        s.append("   ").append("-".repeat(52));
        System.out.println(s);
        System.out.println("         ↑            ↑            ↑            ↑");
    }

    public void printBoard(NubPlayer player, Market market) {

        printFaithTrack(player.getCurrPos());

        printMarket(market, player.getPopePasses());

        Resource wareSmall = null;
        List<Resource> wareMid = new ArrayList<>();
        List<Resource> wareLarge = new ArrayList<>();
        List<ConcreteProductionCard> prod = new ArrayList<>();

        for (BiElement<Resource, Storage> res : player.getAllResources().keySet()){
            switch (res.getSecondValue()) {
                case WAREHOUSE_SMALL -> wareSmall = res.getFirstValue();
                case WAREHOUSE_MID -> {
                    for (int i = 0; i < player.getAllResources().get(res); i++)
                        wareMid.add(res.getFirstValue());
                }
                case WAREHOUSE_LARGE -> {
                    for (int i = 0; i < player.getAllResources().get(res); i++)
                        wareLarge.add(res.getFirstValue());
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            try{
                if (player.getProductionStacks().get(i) != null && !player.getProductionStacks().get(i).isEmpty())
                    prod.add(player.getProductionStacks().get(i).peekFirst());
                else
                    prod.add(null);
            }
            catch (Exception e){
                prod.add(null);
            }
        }

        StringBuilder line = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        line.append(" ".repeat(12)).append("-".repeat(12)).append(" ".repeat(12)).append("\t").append("-".repeat(36)).append("\t").append("-".repeat(36)).append("\t").append("-".repeat(36));
        System.out.println(line);

        line = new StringBuilder();
        line.append(" ".repeat(12)).append("|").append(" ".repeat(10)).append("|").append(" ".repeat(12)).append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|");
        for (int i = 0; i < 2; i++) {
            System.out.println(line);
        }
        line2.append(" ".repeat(12)).append("|").append(wareSmall != null? printRes(wareSmall) : "   None   ").append("|").append(" ".repeat(12)).append("\t").append("|").append(prod.get(0) != null? "     Color: "+printColorprod(prod.get(0))+" Level: "+prod.get(0).getLevel()+"     ": " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(1) != null? "     Color: "+printColorprod(prod.get(1))+" Level: "+prod.get(1).getLevel()+"     ": " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(2) != null? "     Color: "+printColorprod(prod.get(2))+" Level: "+prod.get(2).getLevel()+"     ": " ".repeat(34)).append("|");
        System.out.println(line2+Color.RESET);
        for (int i = 0; i < 2; i++) {
            System.out.println(line);
        }

        line = new StringBuilder();
        line.append(" ".repeat(6)).append("-".repeat(24)).append(" ".repeat(6)).append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|");
        System.out.println(line);

        line = new StringBuilder();
        line.append(" ".repeat(6)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(6)).append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|");
        System.out.println(line);

        line2 = new StringBuilder();
        line2.append(" ".repeat(6)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(6)).append("\t").append("|").append(" ".repeat(14)).append("Input:").append(" ".repeat(14)).append("|").append("\t").append("|").append(" ".repeat(14)).append("Input:").append(" ".repeat(14)).append("|").append("\t").append("|").append(" ".repeat(14)).append("Input:").append(" ".repeat(14)).append("|");
        System.out.println(line2+Color.RESET);

        line2 = new StringBuilder();
        line2.append(" ".repeat(6)).append("| ").append(wareMid.size() > 0? printRes(wareMid.get(0)) : "   None   ").append("| ").append(wareMid.size() > 1? printRes(wareMid.get(1)) : "   None   ").append("|").append(" ".repeat(6)).append("\t").append("|").append(prod.get(0) != null? printProdList(prod.get(0).getRequiredResources()): " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(1) != null? printProdList(prod.get(1).getRequiredResources()): " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(2) != null? printProdList(prod.get(2).getRequiredResources()): " ".repeat(34)).append("|");
        System.out.println(line2+Color.RESET);

        line2 = new StringBuilder();
        line2.append(" ".repeat(6)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(6)).append("\t").append("|").append(" ".repeat(13)).append("Output:").append(" ".repeat(14)).append("|").append("\t").append("|").append(" ".repeat(13)).append("Output:").append(" ".repeat(14)).append("|").append("\t").append("|").append(" ".repeat(13)).append("Output:").append(" ".repeat(14)).append("|");
        System.out.println(line2+Color.RESET);

        line2 = new StringBuilder();
        line2.append(" ".repeat(6)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(6)).append("\t").append("|").append(prod.get(0) != null? printProdList(prod.get(0).getProduction()): " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(1) != null? printProdList(prod.get(1).getProduction()): " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(2) != null? printProdList(prod.get(2).getProduction()): " ".repeat(34)).append("|");
        System.out.println(line2+Color.RESET);

        line = new StringBuilder();
        line.append("-".repeat(36)).append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|");
        System.out.println(line);

        line = new StringBuilder();
        line.append("|").append(" ".repeat(11)).append("|").append(" ".repeat(11)).append("|").append(" ".repeat(11)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|").append("\t").append("|").append(" ".repeat(34)).append("|");
        for (int i = 0; i < 2; i++) {
            System.out.println(line);
        }
        line2 = new StringBuilder();
        line2.append("| ").append(wareLarge.size() > 0? printRes(wareLarge.get(0)) : "   None   ").append("| ").append(wareLarge.size() > 1? printRes(wareLarge.get(1)) : "   None   ").append("| ").append(wareLarge.size() > 2? printRes(wareLarge.get(2)) : "   None   ").append("|").append("\t").append("|").append(prod.get(0) != null? printVictory(prod.get(0).getVictoryPoints()): " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(1) != null? printVictory(prod.get(1).getVictoryPoints()): " ".repeat(34)).append("|").append("\t").append("|").append(prod.get(2) != null? printVictory(prod.get(2).getVictoryPoints()): " ".repeat(34)).append("|");
        System.out.println(line2+Color.RESET);
        for (int i = 0; i < 2; i++) {
            System.out.println(line);
        }

        line = new StringBuilder();
        line.append("-".repeat(37)).append("\t").append("-".repeat(36)).append("\t").append("-".repeat(36)).append("\t").append("-".repeat(36)).append("\t");
        System.out.println(line);


    }

    /**
     * Print the resource whit their own color
     */
    public String printRes (Resource res){
        String s = null;
        switch (res){
            case COIN -> s = Color.YELLOW.escape() +    "  COIN    " + Color.RESET;
            case SERVANT -> s = Color.PURPLE.escape() + "  SERVANT " + Color.RESET;
            case STONE -> s = Color.GRAY.escape() +     "  STONE   " + Color.RESET;
            case SHIELD -> s = Color.BLUE.escape() +    "  SHIELD  " + Color.RESET;
            case BLANK -> s = Color.RESET +             "  BLANK   " + Color.RESET;
            case FAITH -> s = Color.RED.escape() +      "  FAITH   " + Color.RESET;
        }
        return s;
    }
    /**
     * Print the Development card color with their own color
     */
    public String printColorprod (ConcreteProductionCard p){
        String s = null;
        switch (p.getColor()){
            case BLUE -> s = Color.BLUE.escape() +      " BLUE   " + Color.RESET;
            case GREEN -> s = Color.GREEN.escape() +    " GREEN  " + Color.RESET;
            case PURPLE -> s = Color.PURPLE.escape() +  " PURPLE " + Color.RESET;
            case YELLOW -> s = Color.YELLOW.escape() +  " YELLOW " + Color.RESET;
        }
        return s;
    }

    private String printProdList(List<Resource> resu){
        String res = resu.toString().substring(1, resu.toString().length()-1).replace(" ","");
        String s = null;

        switch (res.length()){
            case 4 ->  s = " ".repeat(15)+ res+" ".repeat(15);
            case 5 ->  s = " ".repeat(14)+ res+" ".repeat(15);
            case 6 ->  s = " ".repeat(14)+ res+" ".repeat(14);
            case 7 ->  s = " ".repeat(13)+ res+" ".repeat(14);
            case 8 ->  s = " ".repeat(13)+ res+" ".repeat(13);
            case 9 ->  s = " ".repeat(12)+ res+" ".repeat(13);
            case 10 ->  s = " ".repeat(12)+res+" ".repeat(12);
            case 11 ->  s = " ".repeat(11)+res+" ".repeat(12);
            case 12 ->  s = " ".repeat(11)+res+" ".repeat(11);
            case 13 ->  s = " ".repeat(10)+res+" ".repeat(11);
            case 14 ->  s = " ".repeat(10)+res+" ".repeat(10);
            case 15 ->  s = " ".repeat(9)+res+" ".repeat(10);
            case 16 ->  s = " ".repeat(9)+res+" ".repeat(9);
            case 17 ->  s = " ".repeat(8)+res+" ".repeat(9);
            case 18 ->  s = " ".repeat(8)+res+" ".repeat(8);
            case 19 ->  s = " ".repeat(7)+res+" ".repeat(8);
            case 20 ->  s = " ".repeat(7)+res+" ".repeat(7);
            case 21 ->  s = " ".repeat(6)+res+" ".repeat(7);
            case 22->  s = " ".repeat(6)+res+" ".repeat(6);
            case 23->  s = " ".repeat(5)+res+" ".repeat(6);
            case 24->  s = " ".repeat(5)+res+" ".repeat(5);
            case 25->  s = " ".repeat(4)+res+" ".repeat(5);
            case 26->  s = " ".repeat(4)+res+" ".repeat(4);
            case 27->  s = " ".repeat(3)+res+" ".repeat(4);
            case 28->  s = " ".repeat(3)+res+" ".repeat(3);
            case 29->  s = " ".repeat(2)+res+" ".repeat(3);
            case 30->  s = " ".repeat(2)+res+" ".repeat(2);
            case 31->  s = " ".repeat(1)+res+" ".repeat(2);
            case 32 -> s = " ".repeat(1)+res+" ".repeat(1);
            case 33 -> s = res+" ".repeat(1);
            case 34 -> s = res;
            case 35 -> s = res.substring(0, res.length()-1);

        }
        if (s == null)
            s = " ".repeat(15)+"None"+" ".repeat(15);

        return s;
    }

    private StringBuilder printVictory(int v){
        StringBuilder s = new StringBuilder();
        if (v < 10)
            s.append(" ".repeat(8)).append("Victory Points: ").append(v).append(" ".repeat(9));
        else
            s.append(" ".repeat(8)).append("Victory Points: ").append(v).append(" ".repeat(8));
        return s;
    }

    public void printProductionCard(ConcreteProductionCard card) {

        String lines = "-".repeat(160);

        System.out.println(lines);
        System.out.println("Develop Card with ID: " + card.getId());
        System.out.println("Level: " + card.getLevel());
        System.out.println("Color: " + card.getColor());
        System.out.println("Victory Points: " + card.getVictoryPoints());
        System.out.println("Cost: " + card.getCost());
        System.out.println("Input resources: " + card.getRequiredResources());
        System.out.println("Output resources:  " + card.getProduction());
        System.out.println(lines);

    }

    public void printProductionStoreGrid(List<ConcreteProductionCard> prodCard){

        String spaces;

        spaces = " ".repeat(10) ;
        System.out.print(spaces + "|");
        spaces = " ".repeat(7);

        printFirstLineGridProdCard();

        System.out.println();

        spaces = "-".repeat(95);
        System.out.println(spaces);

        spaces = " ".repeat(9);
        System.out.print("Level 1   |");
        for (ConcreteProductionCard card : prodCard)
            if (card.getLevel() == 1 && card.getColor().equals(ColorEnum.GREEN)) {
                if (card.getId() < 10)
                    System.out.print(spaces + " " + card.getId() + spaces + "|");
                else
                    System.out.print(spaces + card.getId() + spaces + "|");
            }
        for (ConcreteProductionCard card : prodCard)
            if (card.getLevel() == 1 && card.getColor().equals(ColorEnum.BLUE)) {
                if (card.getId() < 10)
                    System.out.print(spaces + " " + card.getId() + spaces + "|");
                else
                    System.out.print(spaces + card.getId() + spaces + "|");
            }
        for (ConcreteProductionCard card : prodCard)
            if (card.getLevel() == 1 && card.getColor().equals(ColorEnum.YELLOW)) {
                if (card.getId() < 10)
                    System.out.print(spaces + " " + card.getId() + spaces + "|");
                else
                    System.out.print(spaces + card.getId() + spaces + "|");
            }
        for (ConcreteProductionCard card : prodCard)
            if (card.getLevel() == 1 && card.getColor().equals(ColorEnum.PURPLE)){
                if (card.getId() < 10)
                    System.out.print(spaces + " " + card.getId() + spaces + "|");
                else
                    System.out.print(spaces + card.getId() + spaces + "|");
            }

        System.out.println();

        System.out.print("Level 2   |");
        for (ConcreteProductionCard productionCard : prodCard)
            if (productionCard.getLevel() == 2 && productionCard.getColor().equals(ColorEnum.GREEN))
                System.out.print(spaces + productionCard.getId() + spaces + "|");
        for (ConcreteProductionCard productionCard : prodCard)
            if (productionCard.getLevel() == 2 && productionCard.getColor().equals(ColorEnum.BLUE))
                System.out.print(spaces + productionCard.getId() + spaces + "|");
        for (ConcreteProductionCard productionCard : prodCard)
            if (productionCard.getLevel() == 2 && productionCard.getColor().equals(ColorEnum.YELLOW))
                System.out.print(spaces + productionCard.getId() + spaces + "|");
        for (ConcreteProductionCard productionCard : prodCard)
            if (productionCard.getLevel() == 2 && productionCard.getColor().equals(ColorEnum.PURPLE))
                System.out.print(spaces + productionCard.getId() + spaces + "|");
        System.out.println();

        System.out.print("Level 3   |");
        for (ConcreteProductionCard concreteProductionCard : prodCard)
            if (concreteProductionCard.getLevel() == 3 && concreteProductionCard.getColor().equals(ColorEnum.GREEN))
                System.out.print(spaces + concreteProductionCard.getId() + spaces + "|");
        for (ConcreteProductionCard concreteProductionCard : prodCard)
            if (concreteProductionCard.getLevel() == 3 && concreteProductionCard.getColor().equals(ColorEnum.BLUE))
                System.out.print(spaces + concreteProductionCard.getId() + spaces + "|");
        for (ConcreteProductionCard concreteProductionCard : prodCard)
            if (concreteProductionCard.getLevel() == 3 && concreteProductionCard.getColor().equals(ColorEnum.YELLOW))
                System.out.print(spaces + concreteProductionCard.getId() + spaces + "|");
        for (ConcreteProductionCard concreteProductionCard : prodCard)
            if (concreteProductionCard.getLevel() == 3 && concreteProductionCard.getColor().equals(ColorEnum.PURPLE))
                System.out.print(spaces + concreteProductionCard.getId() + spaces + "|");
        System.out.println();


    }

    public void printFirstLineGridProdCard(){
        String spaces = " ".repeat(7);
        System.out.print(spaces + Color.GREEN.escape() +  "GREEN " + Color.RESET + spaces + "|");
        System.out.print(spaces + Color.BLUE.escape() +   "BLUE  " + Color.RESET + spaces + "|");
        System.out.print(spaces + Color.YELLOW.escape() + "YELLOW" + Color.RESET + spaces + "|");
        System.out.print(spaces + Color.PURPLE.escape() + "PURPLE" + Color.RESET + spaces + "|");
    }



}
