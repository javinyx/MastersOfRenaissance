package it.polimi.ingsw.client.GUI.sceneHandlers;

public enum ScenesEnum {
    WELCOME("welcome.fxml"),
    CONNECTION("connection.fxml"),
    REGISTRATION("registration.fxml"),
    WAITING_ROOM("waitingRoom.fxml"),
    CHOOSE_LEADERS("chooseLeaders.fxml"),
    CHOOSE_RESOURCES("chooseResources.fxml"),
    MAIN_BOARD("mainBoard.fxml");

    private String path;

    ScenesEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
