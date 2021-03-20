package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class testGame {

    @Test
    public void testCreatePlayer() {

        List<Player> players = new ArrayList<>();

        players.add(new Player("luca", 1));
        players.add(new Player("franco", 2));
        players.add(new Player("javis", 3));
        players.add(new Player("coco", 4));

        assertEquals(Integer.valueOf(2), players.get(1).turnID );
        //assertEquals(String.valueOf("javis"), players.get(2).nickname);

    }

}
