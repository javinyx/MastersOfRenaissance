package it.polimi.ingsw.messages.concreteMessages;

import it.polimi.ingsw.messages.SimpleMessage;
import it.polimi.ingsw.misc.BiElement;

import java.util.List;

public class VaticanReportMessage extends SimpleMessage {
    private final String triggeringPlayerNick;
    private final int reportId;
    private final List<BiElement<Integer, Boolean>> allPlayerPopeFavorStatus;

    public VaticanReportMessage(String triggeringPlayerNick, int reportId, List<BiElement<Integer,Boolean>> allPlayerPopeFavorStatus){
        this.triggeringPlayerNick = triggeringPlayerNick;
        this.reportId = reportId;
        this.allPlayerPopeFavorStatus = allPlayerPopeFavorStatus;
    }

    /**
     * @return nickname of the player who triggered the Vatican Report
     */
    public String getTriggeringPlayerNick() {
        return triggeringPlayerNick;
    }

    /**
     * @return number of the Vatican Report triggered
     * <ul>
     * <li>1: cell 8, interval for Pope's Favor activation 5~8</li>
     * <li>2: cell 16, interval for Pope's Favor activation 12~16</li>
     * <li>3: cell 24 (last one), interval for Pope's Favor activation 19~24</li>
     * </ul>
     */
    public int getReportId() {
        return reportId;
    }

    /**
     * @return a list containing each player's id associated with his/her Pope's Favor activation tile for this specific
     * Vatican Report ({@code true} if active, {@code false} otherwise)
     */
    public List<BiElement<Integer, Boolean>> getAllPlayerPopeFavorStatus() {
        return allPlayerPopeFavorStatus;
    }
}
