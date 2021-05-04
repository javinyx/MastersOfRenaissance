package it.polimi.ingsw.controller;

import it.polimi.ingsw.view.RemoteView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ControllerTest {

    Controller controller = new Controller();
    RemoteView remoteView = new RemoteView("luca", controller);

    @BeforeEach
    void setUp() {

        //controller.createMultiplayerGame(3); //infinite loop

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