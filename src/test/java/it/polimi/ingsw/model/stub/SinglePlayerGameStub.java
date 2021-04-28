package it.polimi.ingsw.model.stub;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.model.SinglePlayerGame;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.player.ProPlayer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;

public class SinglePlayerGameStub extends SinglePlayerGame {

    public SinglePlayerGameStub(){
        super();
    }

    public ConcreteProductionCard getFirstProdAvailable(){
        return (ConcreteProductionCard) productionDecks.get(0).peekFirst();
    }

    public void setCurrPlayer(ProPlayer p){
        currPlayer = p;
    }

    public LeaderCard createMarbleCard(){
        ArrayList<LeaderCard> cardList = new ArrayList<>();
        ArrayDeque<LeaderCard> cardDeque;
        LeaderCard marbleAbilityCard;

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream("/json/LeaderCards/MarbleAbilityCards.json")))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<MarbleAbility>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cardDeque = new ArrayDeque<>(cardList);
        marbleAbilityCard = cardDeque.peekFirst();

        return marbleAbilityCard;
    }

    public LeaderCard createBoostAbility(){
        ArrayList<LeaderCard> cardList = new ArrayList<>();
        ArrayDeque<LeaderCard> cardDeque;
        LeaderCard boostAbilityCard;

        Gson gson = new Gson();

        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream("/json/LeaderCards/BoostAbilityCards.json")))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<BoostAbility>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cardDeque = new ArrayDeque<>(cardList);
        boostAbilityCard = cardDeque.peekFirst();

        return boostAbilityCard;
    }
}