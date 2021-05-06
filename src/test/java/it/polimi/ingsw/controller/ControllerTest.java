package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.MessageEnvelope;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.BuyMarketMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.stub.ProPlayerStub;
import it.polimi.ingsw.model.stub.SinglePlayerGameStub;
import it.polimi.ingsw.view.RemoteView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ControllerTest {
    SinglePlayerGameStub game = new SinglePlayerGameStub();
    Controller controller = new Controller();
    RemoteView remoteView = new RemoteView("luca", controller);
    ProPlayerStub p = new ProPlayerStub("luca", 0, game);
    Gson gson = new Gson();

    @BeforeEach
    void setUp() {

       /* char c = 'c';
        int i = 2;

        BuyMarketMessage mar = new BuyMarketMessage(p, c, i, null);

        MessageEnvelope msg = new MessageEnvelope(MessageID.BUY_FROM_MARKET, mar.toString());
        */
        BiElement<Integer, String> lol = new BiElement<Integer, String>(1,"lol");

        String js = gson.toJson(lol);
        BiElement<Integer, String> lol2 = gson.fromJson(js, new TypeToken<BiElement<Integer, String>>() {}.getType());

    }

    @Test
    void createSinglePlayerGame() {
    }

    @Test
    void createMultiplayerGame() {
    }

    @Test
    void addPlayer() {
    }

    @Test
    void update() {
    }

    @Test
    void buyFromMarAction() {
    }

    @Test
    void organizeResourceAction() {
    }

    @Test
    void buyProdCardAction() {
    }

    @Test
    void activateProdAction() {
    }

    @Test
    void isGameOver() {
    }

    @Test
    void gameOver() {
    }

    @Test
    void abortGame() {
    }
}