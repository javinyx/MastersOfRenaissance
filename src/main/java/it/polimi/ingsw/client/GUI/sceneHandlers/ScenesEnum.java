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
    STORAGE("chooseStoragePopUp.fxml");

    private String path;

    ScenesEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
