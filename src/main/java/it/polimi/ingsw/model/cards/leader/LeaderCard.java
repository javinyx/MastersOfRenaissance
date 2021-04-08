package it.polimi.ingsw.model.cards.leader;

import com.google.gson.Gson;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.market.Buyable;
import it.polimi.ingsw.model.player.ProPlayer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public interface LeaderCard extends Card{

    public static void main(String[] args) {

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream("/json/LeaderCards.json"))) {

            // Convert JSON File to Java Object
            LeaderCard[] leadCards = gson.fromJson(reader, LeaderCard[].class);

            // Pass each set of leader cards to the corresponding ability class
            // TO-DO: Assign the json objects to corresponding abilities

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean isActive();

    void setStatus(boolean activate);

    int getVictoryPoints();

    List<Buyable> getCost();

    void applyEffect(ProPlayer player);
}
