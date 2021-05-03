package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.MultiPlayerGame;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.stub.MultiPlayerGameStub;
import it.polimi.ingsw.model.stub.ProPlayerStub;
import it.polimi.ingsw.view.RemoteView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    Controller controller = new Controller();
    RemoteView remoteView = new RemoteView("luca", controller);

    @BeforeEach
    void setUp() {

        controller.update(MessageID.STORE_RESOURCES);

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