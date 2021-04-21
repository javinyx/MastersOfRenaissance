package it.polimi.ingsw.view;

public abstract class View {

    private final String nickname;

    public View(String playerNickname){
        this.nickname=playerNickname;
        if(playerNickname == null)
            throw new NullPointerException();
    }

    public String getNickname(){
        return nickname;
    }

    /**
     * Shows the passed message to the player. Implementing classes should override it according to the specific architecture.
     * @param message message to display.
     */
    protected abstract void showMessage(String message);
}
