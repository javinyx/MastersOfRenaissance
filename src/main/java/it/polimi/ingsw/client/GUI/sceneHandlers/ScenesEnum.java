package it.polimi.ingsw.client.GUI.sceneHandlers;

public enum ScenesEnum {
    WELCOME("welcome.fxml"),
    CONNECTION("connection.fxml"),
    REGISTRATION(""),
    WAITING_ROOM(""),
    CHOOSE_LEADERS(""),
    CHOOSE_RESOURCES(""),
    MAIN_BOARD("");

    private String path;
    ScenesEnum(String path){this.path = path;}

    public String getPath() {
        return path;
    }
}
