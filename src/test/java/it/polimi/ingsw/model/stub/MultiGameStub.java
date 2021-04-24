package it.polimi.ingsw.model.stub;

import it.polimi.ingsw.model.MultiplayerGame;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.List;
import java.util.stream.Collectors;

public class MultiGameStub extends MultiplayerGame {

    public void setCurrPlayer(ProPlayer p){
        currPlayer = p;
    }
    public PlayerStub addPlayer(String nickname){
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
        PlayerStub p = new PlayerStub(nickname, totalPlayers, this);
        players.add(p);
        p.registerObserver(this);
        activePlayers.add(p);
        return p;
    }
}
