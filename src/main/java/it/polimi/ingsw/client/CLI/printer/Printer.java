package it.polimi.ingsw.client.CLI.printer;

import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ColorEnum;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.awt.*;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Printer {
    protected final int SIDEBAR_SIZE = 51;

    public void clearScreen() {
        System.out.print("\n".repeat(60));
    }


    public void printAlignedCenter(String toPrint, int sizeToCenterComparedTo) {
        int blanks = (sizeToCenterComparedTo / 2 + (sizeToCenterComparedTo % 2 == 0 ? 0 : 1)) - (toPrint.length() / 2 + (toPrint.length() % 2 == 0 ? 0 : 1));
        String builder = " ".repeat(Math.max(0, blanks)) +
                toPrint;
        System.out.println(builder);
    }


    public void printLeaders(LeaderCard card) {

        List<Buyable> cost = card.getCost();
        List<ConcreteProductionCard> lp;
        Resource res;
        String lines = "-".repeat(160);

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
                "1) Buy from market: you can buy Resources from the market\n" +
                "2) Buy a Development Card\n" +
                "3) Start production: you can produce Resources\n" +
                "4) View your opponents status\n" +
                "5) Activate a Leader Card\n" +
                "6) DiscardLeader Card\n" +
                "7) View Develop Card that you can buy\n" +
                "8) End turn");
    }

    public void printLightTurnOptions() {
        System.out.println("Now it's your turn, you can choose between these by typing the number:\n" +
                "1) View your opponents status\n" +
                "2) Activate a Leader Card\n" +
                "3) DiscardLeader Card\n" +
                "4) View Develop Card that you can buy\n" +
                "5) End turn");
    }

    public void printTable() {

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
        System.out.print(spaces + "GREEN " + spaces + "|");
        System.out.print(spaces + "BLUE  " + spaces + "|");
        System.out.print(spaces + "YELLOW" + spaces + "|");
        System.out.print(spaces + "PURPLE" + spaces + "|");

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



}
