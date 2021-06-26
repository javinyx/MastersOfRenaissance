package it.polimi.ingsw.client.GUI.sceneHandlers;

public enum ScenesEnum {
    WELCOME("welcome.fxml"),
    CONNECTION("connection.fxml"),
    REGISTRATION("registration.fxml"),
    WAITING_ROOM("waitingRoom.fxml"),
    CHOOSE_LEADERS("chooseLeaders.fxml"),
    CHOOSE_RESOURCES("chooseResources.fxml"),
    CHOOSE_STORAGE("chooseStorage.fxml"),
    MAIN_BOARD("mainBoard.fxml"),
    MARKET("marketPopUp.fxml"),
    OTHER_PLAYERS("playerPopUp.fxml"),
    CHOOSE_PAYMENT("choosePaymentMethod.fxml"),
    STORAGE("chooseStoragePopUp.fxml"),
    PRODUCTION_CARDS("productionCardsPopUp.fxml"),
    PRODUCE("producePopUp.fxml"),
    DISCARD_ACTIVATE_LEADER("discActLeaderPopUp.fxml");

    private String path;

    ScenesEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
