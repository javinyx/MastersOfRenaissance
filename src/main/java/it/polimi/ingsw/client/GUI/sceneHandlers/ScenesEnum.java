package it.polimi.ingsw.client.GUI.sceneHandlers;

/**
 * All of the FXML scenes used during the game, with their corresponding paths.
 */
public enum ScenesEnum {
    /**
     * Welcome scene.
     */
    WELCOME("welcome.fxml"),
    /**
     * Connection scene.
     */
    CONNECTION("connection.fxml"),
    /**
     * Registration scene.
     */
    REGISTRATION("registration.fxml"),
    /**
     * Waiting Room scene.
     */
    WAITING_ROOM("waitingRoom.fxml"),
    /**
     * Choose Leaders scene.
     */
    CHOOSE_LEADERS("chooseLeaders.fxml"),
    /**
     * Choose Resources scene.
     */
    CHOOSE_RESOURCES("chooseResources.fxml"),
    /**
     * Choose Storage scene.
     */
    CHOOSE_STORAGE("chooseStorage.fxml"),
    /**
     * Main Board scene.
     */
    MAIN_BOARD("mainBoard.fxml"),
    /**
     * Market scene.
     */
    MARKET("marketPopUp.fxml"),
    /**
     * Other player's board scene.
     */
    OTHER_PLAYERS("playerPopUp.fxml"),
    /**
     * Choose Payment scene.
     */
    CHOOSE_PAYMENT("choosePaymentPopUp.fxml"),
    /**
     * Choose Storage scene.
     */
    STORAGE("chooseStoragePopUp.fxml"),
    /**
     * Production cards scene.
     */
    PRODUCTION_CARDS("productionCardsPopUp.fxml"),
    /**
     * Produce scene.
     */
    PRODUCE("producePopUp.fxml"),
    /**
     * Discard and Activate leader scene.
     */
    DISCARD_ACTIVATE_LEADER("discActLeaderPopUp.fxml"),
    /**
     * Choose basic output scene.
     */
    BASIC_OUTPUT("basicOutputPopUp.fxml"),
    /**
     * Choose which leader to use during main actions.
     */
    USE_LEADER("useLeaderPopUp.fxml");

    private String path;

    ScenesEnum(String path) {
        this.path = path;
    }

    /**
     * Gets the path of the requested scene.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }
}
