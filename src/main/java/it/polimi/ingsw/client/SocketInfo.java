package it.polimi.ingsw.client;

public class SocketInfo {
    /**
     * The socket's ip
     */
    private String hostName;
    /**
     * The socket's port
     */
    private Integer portNumber;

    /**
     * Constructor for the class
     * @param hostName The hostname
     * @param portNumber The port number
     */
    public SocketInfo(String hostName, Integer portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    /**
     * Getter for the hostname attribute
     * @return The hostname
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Setter for the hostname parameter
     * @param hostName The hostname to be set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Getter for the port number
     * @return The port number
     */
    public Integer getPortNumber() {
        return portNumber;
    }

    /**
     * Setter for the port number
     * @param portNumber The port number to be set
     */
    public void setPortNumber(Integer portNumber) {
        this.portNumber = portNumber;
    }
}
