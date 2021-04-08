package it.polimi.ingsw.model.cards.actiontoken;

import com.google.gson.Gson;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.model.cards.Card;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public interface ActionToken extends Card {

    public static void main(String[] args) {

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(MastersOfRenaissance.class.getResourceAsStream("/json/ActionTokens.json"))) {

            // Convert JSON File to Java Object
            ActionToken[] actionTokens = gson.fromJson(reader, ActionToken[].class);

            // Pass each set of action tokens to the corresponding ability class
            // TO-DO: Assign the json objects to corresponding abilities

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw();

}
