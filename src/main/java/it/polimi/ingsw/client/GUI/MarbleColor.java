package it.polimi.ingsw.client.GUI;

public enum MarbleColor {
    SERVANT ("#8A2BA2"),
    FAITH   ("#D63350"),
    BLANK   ("#FFFFFF"),
    STONE   ("#8697AA"),
    COIN    ("#FEF17E"),
    SHIELD  ("#1697D2");

    private String hexCode;

    MarbleColor(String hexCode){
        this.hexCode = hexCode;
    }

}
