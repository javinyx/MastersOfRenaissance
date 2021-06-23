package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class MultiPlayerGameStub extends MultiPlayerGame {

    //private static Controller controller = new Controller();

    public MultiPlayerGameStub(ControllerStub controller) {
        super(controller);
    }

    public void setCurrPlayer(ProPlayer p){
        currPlayer = p;
    }

    public ProPlayerStub addPlayer(String nickname){
        if(totalPlayers>3){
            return null;//too many players
        }
        List<String> nicknames = players.stream()
                .map(Player:: getNickname)
                .collect(Collectors.toList());
        if(!nicknames.isEmpty() && nicknames.contains(nickname)){
            throw new IllegalArgumentException("Already exists a player with "+ nickname + "as nickname");
        }

        totalPlayers++;
        ProPlayerStub p = new ProPlayerStub(nickname, totalPlayers, this);
        players.add(p);
        p.registerObserver(this);
        activePlayers.add(p);
        return p;
    }
}
