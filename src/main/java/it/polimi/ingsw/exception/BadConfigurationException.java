package it.polimi.ingsw.exception;
/**
 * Exception thrown when the configuration file contains a power not recognized by the game
 */
public class BadConfigurationException extends Exception {
    public BadConfigurationException(String message) {
        super("Configuration error. " + message);
    }
}