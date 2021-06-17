package it.polimi.ingsw.client.CLI;

/**
 * Contains the ANSI escape code for cli mode colors
 */
public enum Color {

    GREEN ("\u001B[32m"),
    PURPLE ("\u001B[35m"),
    RED("\u001B[31m"),
    YELLOW("\u001B[33m"),
    GRAY("\u001B[0;37m"),
    BLUE("\u001B[34m");


    public static final String RESET = "\u001B[0m";

    private String escapeUnix;

    Color(String escapeUnix) {
        this.escapeUnix = escapeUnix;
    }

    /**
     * @return ANSI escape for the color.
     */
    public String escape() {
        return escapeUnix;
    }
}
