package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private List<Player> track;

    public Table() {
        track = new ArrayList<>();
    }

    /**
     * Move the selected player on the board
     */

    public void movePlayer(Player player){
        player.currPos++;
    }
}
