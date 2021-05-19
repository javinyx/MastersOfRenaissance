package it.polimi.ingsw.client.CLI.printer;

import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.cards.production.ProductionCard;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.market.Resource;

import java.awt.*;
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


    public void printLeaders(LeaderCard card){

        List<Buyable> cost = card.getCost();
        List<ConcreteProductionCard> lp;
        Resource res;
        String lines = "-".repeat(160);

        System.out.println(lines);
        System.out.println("Leader Card with ID: " + card.getId());
        System.out.println("Victory Points: " + card.getVictoryPoints());

        switch (card.getNameNew()){

            case "StorageAbility" -> {
                int count;
                res = (Resource) cost.get(0);
                for (count = 0; count < cost.size(); count++);

                System.out.println("Cost: " + count +" "+ res.toString());
                System.out.println("Power: add 2 extra space for the " + res + " resource");

            }

            case"MarbleAbility"-> {
                doStuffProdCard(card);
                System.out.println("Power: convert a blank marble in the market into a " + ((MarbleAbility)card).getReplacingResource() + " resource");
            }

            case"DiscountAbility"-> {
                doStuffProdCard(card);
                System.out.println("Power: discount a Development Card with one less " + ((DiscountAbility)card).getDiscountType() + " in the cost");
            }

            case"BoostAbility"-> {
                doStuffProdCard(card);
                System.out.println("Power: spend a " + ((BoostAbility)card).getResource() + " and receive a resource you choose and an extra Faith Point");
            }
        }

        System.out.println(lines);
    }

    private void doStuffProdCard(LeaderCard card){
        List<ConcreteProductionCard> lp = new ArrayList<>();

        for (int i = 0; i < card.getCost().size(); i++) {
            lp.add((ConcreteProductionCard) card.getCost().get(i));
        }

        System.out.println("Cost: " + lp.size() + " Development Card:");
        for (int i = 0; i < lp.size(); i++) {
            System.out.println("                         "+"*Development Card number "+i+": Level: "+lp.get(i).getLevel()+"; Color: "+lp.get(i).getColor());
        }
    }

    public void printTurnOptions(){
        System.out.println("Now it's your turn, you can choose between these by typing the number:\n" +
                           "1) Buy from market: you can buy Resources from the market\n" +
                           "2) Buy a Development Card\n" +
                           "3) Start production: you can produce Resources\n" +
                           "4) View your opponents status\n" +
                           "5) Activate a Leader Card\n" +
                           "6) DiscardLeader Card\n" +
                           "7) End turn");
    }

    public void printLightTurnOptions(){
        System.out.println("Now it's your turn, you can choose between these by typing the number:\n" +
                            "1) View your opponents status\n" +
                            "2) Activate a Leader Card\n" +
                            "3) DiscardLeader Card\n" +
                            "4) End turn");
    }

    public void printTable(){

    }

    public void printProductionCard(ConcreteProductionCard card){

        String lines = "-".repeat(160);

        System.out.println(lines);
        System.out.println("Develop Card with ID: " + card.getId());
        System.out.println("Victory Points: " + card.getVictoryPoints());
        System.out.println("Cost: " + card.getCost().toString().substring(1, card.getCost().size()-1));
        System.out.println("Level: " + card.getLevel());
        System.out.println("Input resources: " + card.getRequiredResources().toString().substring(1, card.getRequiredResources().size()-1) );
        System.out.println("Output resources:  " + card.getProduction().toString().substring(1, card.getProduction().size()-1 ));
        System.out.println(lines);

    }







}
