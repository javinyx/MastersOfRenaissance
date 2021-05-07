package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.client.commands.BuyMarketCommand;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.BuyMarketMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Cli extends ViewInterface {
    private final Scanner scanner = new Scanner(System.in);

    public Cli(ClientController controller){
        super(controller);
    }


    @Override
    public void showMessage(String str){
        System.out.println(str);
    }

    @Override
    public void buyFromMarket(){
        String dim;
        char dimChar;
        int index, lowerBound = 1, upperBound;
        do {
            System.out.println("Choose a row or a column from the market by typing R or C");
            dim = scanner.nextLine();
            dim = dim.toLowerCase();
        }while(!dim.equals("c") && !dim.equals("r"));
        if(dim.equals("c")){
            upperBound = 3;
            dimChar = 'c';
        }else{
            upperBound = 4;
            dimChar = 'r';
        }
        do{
            System.out.println("Choose the index - must be between " + lowerBound + " and " + upperBound);
            index = scanner.nextInt();
        }while(index<lowerBound || index>upperBound);

        //if players has marble leader active, ask if he/she wants to use it and for how many times
        List<LeaderCard> leaders = controller.getLeaders();
        List<MarbleAbility> marbleAbilities = new ArrayList<>();
        int tot = 0;
        if(!leaders.isEmpty()){
            for(LeaderCard card : leaders){
                if(card.isActive() && card instanceof MarbleAbility){
                    tot++;
                    marbleAbilities.add((MarbleAbility) card);
                }
            }
        }

        List<BiElement<MarbleAbility, Integer>> leaderUsage = new ArrayList<>();
        if(tot>0){
            System.out.println("You have " + tot + " MarbleAbility Leaders that can be used now.\n" +
                    "For each card, type how many marble exchanges you want to perform with them. 0 if no exchange.");
            for(MarbleAbility card : marbleAbilities){
                System.out.println("How many for this card?\n" + card);
                int j = scanner.nextInt();
                if(j>0){
                    leaderUsage.add(new BiElement<>(card, j));
                }
            }
        }

        BuyMarketCommand buyMarketCommand = new BuyMarketCommand(controller);
        BuyMarketMessage message = new BuyMarketMessage(dimChar, index, leaderUsage);
        buyMarketCommand.generateEnvelope(message);
    }

    @Override
    public void activateLeader() {
        int leaderId;
        List<LeaderCard> leaders = controller.getLeaders();
        List<LeaderCard> activable = new ArrayList<>();
        for(LeaderCard leader : leaders){
            if(!leader.isActive()){
                activable.add(leader);
            }
        }
        if(leaders.size()==0){
            System.out.println("You don't have any leader.");
            return;
        }
        if(activable.size()==0){
            System.out.println("All your leaders are already active.");
            return;
        }
        System.out.println("You can activate " + activable.size() + " leaders. Which one do you want to activate?" +
                "Insert its id.\n" + activable);
        leaderId = scanner.nextInt();
        MessageHandler.generateEnvelope(MessageID.ACTIVATE_LEADER, gson.toJson(leaderId, Integer.class));
    }
}
