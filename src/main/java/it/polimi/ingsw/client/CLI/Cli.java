package it.polimi.ingsw.client.CLI;

import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.messages.concreteMessages.ChooseResourceMessage;
import it.polimi.ingsw.messages.concreteMessages.InfoMessage;

public class Cli extends ViewInterface {


    @Override
    public void showLeaderMessage(int msg) {
        System.out.println("Choose no." + msg + " resources");
    }

    @Override
    public void showMessage(ChoosePlacementsInStorageMessage msg) {
        System.out.println("Choose a storage for each of the following resources: " + msg.getResources());
    }

    @Override
    public void showMessage(ChooseLeaderCardsMessage msg){
        System.out.println("Choose no." + msg.getQuantity() + " of leaders among these:\n" + msg.getLeaders());
    }

    @Override
    public void showMessage(InfoMessage msg){
        System.out.println("Error: " + msg.getInfo());
    }
}
