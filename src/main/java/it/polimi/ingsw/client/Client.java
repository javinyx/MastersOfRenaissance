package it.polimi.ingsw.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The main client class, starts the application
 */
public class Client {
    /**
     * Max number of parameters to read from the console on startup
     */
    static final Integer nParam = 3;

    /**
     * Main for the application
     * @param args The main's arguments
     */
    public static void main(String[] args) {
        boolean interfaceArg = false;
        //ConfigExporter.exportNonExistingConf();
        if (args.length <= nParam) {
            for (String argument : args) {
                if (argument.toUpperCase().equals("CLI")) {
                    //CLIApp.main(args);
                    interfaceArg = true;
                } else if (argument.toUpperCase().equals("GUI")) {
                    //GUIApp.main(args);
                    interfaceArg = true;
                }
            }
            if (!interfaceArg) {
                System.out.println("User interface parameter not specified. GUI will be launched");
                //GUIApp.main(args);
            }
        }
        else {
            System.out.println();
            System.out.println("Parameters not specified correctly.");
            System.out.println("List of accepted parameters in any order (max " + nParam.toString() + "):");
            System.out.println("IP address of the server you want to connect to");
            System.out.println("Port of the server you want to connect to");
            System.out.println("Interface you want to play with (CLI/GUI)");
            System.out.println();
        }
        System.exit(0);
    }

    /**
     * Method to check if the given address+port combination is correct
     * @param args The main's args
     * @return A socketInfo if the parameters correspond to a server, null otherwise
     */
    public static SocketInfo checkAddress(String[] args) {
        /*InetAddress ipAddress;
        Integer ipArg = -1, port = 0;
        if (args.length == 2 || args.length == 3) {
            for (int i = 0; i < args.length; i++) {
                if (isIp(args[i])) {
                    try {
                        ipAddress = InetAddress.getByName(args[i]);
                        ipArg = i;
                    }
                    catch (UnknownHostException exception) {
                        return null;
                    }
                }
                else
                if (ViewInterface.isNumber(args[i])) {
                    port = Integer.parseInt(args[i]);
                    if (port <= 1023 || port > 65535) {
                        port = 0;
                    }
                }
            }
            if (ipArg != -1 && port != 0)
                return new SocketInfo(args[ipArg], port);
        }*/
        return null;
    }

    /**
     * Checks if the given string is an IP address
     * @param s The string to check
     * @return True if the given string respects IP format, False otherwise
     */
    public static boolean isIp(String s) {
        String[] candidates;
        /*Integer found = 0, n;
        if (s == null || s.isEmpty())
            return false;
        if (!s.contains("."))
            return false;
        candidates = s.split(Pattern.quote("."));
        if (candidates.length == 4) {
            for (String number : candidates) {
                if (ViewInterface.isNumber(number)) {
                    n = Integer.parseInt(number);
                    if (n >= 0 && n <= 255)
                        found++;
                }
            }
            if (found == 4 && !s.endsWith(".")) {
                return true;
            }
        }*/
        return false;
    }
}
