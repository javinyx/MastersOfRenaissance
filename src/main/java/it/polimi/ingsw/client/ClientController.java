package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;

public class ClientController {
    private ViewInterface view;
    private boolean myTurn;
    private List<LeaderCard> leaders = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private ProPlayer player;

    public ClientController(ViewInterface view, ProPlayer player){
        this.view = view;
        this.player = player;
    }

    public boolean getMyTurn(){return myTurn;}
    public List<LeaderCard> getLeaders(){return leaders;}
    public void addLeaders(LeaderCard leader){leaders.add(leader);}
    public void removeLeader(LeaderCard leader){leaders.remove(leader);}
    public void setMyTurn(boolean status){this.myTurn = status;}
    public void addPlayers(ProPlayer player){this.players.add(player);}
    public List<Player> getPlayers(){return players;}
    public ProPlayer getPlayer(){return player;}

    public void chooseResourceAction(int quantity) {
        view.showMessage("Choose no." + quantity + " resources");
    }

    public void chooseStorageAction(ChoosePlacementsInStorageMessage msg){
        view.showMessage("Choose a storage for each of the following resources: " + msg.getResources());
    }

    public void chooseLeadersAction(ChooseLeaderCardsMessage msg){
        view.showMessage("Choose no." + msg.getQuantity() + " of leaders among these:\n" + msg.getLeaders());
    }

    public void errorAction(String str){
        view.showMessage(str);
    }

}
