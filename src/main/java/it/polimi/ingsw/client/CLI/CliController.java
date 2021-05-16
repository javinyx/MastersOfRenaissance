package it.polimi.ingsw.client.CLI;

import com.google.gson.Gson;
import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.MessageReceiver;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.market.Resource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class CliController extends ClientController {

    private final Cli cli;
    private Gson gson = new Gson();



    public CliController() {
        this.cli = new Cli(this);
    }

    public boolean setup() throws IOException {

        if (!cli.initialScreen())
            return false;

        String ip=cli.askForIp();
        int port=cli.askForPort();
        Socket socket = new Socket(ip,port);
        socket.setKeepAlive(true);

        PrintWriter toServer = new PrintWriter(socket.getOutputStream());
        messageToServerHandler = new MessageToServerHandler(toServer, this);

        cli.showMessage("Connection established");

        try (socket; ObjectInputStream socketIn = new ObjectInputStream(socket.getInputStream()); toServer) {
            Thread t0 = cli.startContinuousRead();
            Thread t1 = new Thread(new MessageReceiver(socketIn, this));
            t1.start();
            t1.join();
            t0.join();
            synchronized (this){this.wait(3000);}

        } catch (InterruptedException | NoSuchElementException e) {
            setClosedConnection("Connection closed from the client side");
            Thread.currentThread().interrupt();
        }
        return true;
    }

    @Override
    public void displayMessage(String str) {
        cli.showMessage(str);
    }

    // Input Part ------------------------------------------------------------------------------------------------------

    /**
    * @return {@code true} if current player can perform actions, based on whether is his turn or not, if match is over or if he's waiting for server updates
    */
    public boolean canPlay() {
        if(isGameOver()){
            displayMessage("The current match is over.");
            return false;
        }
        if (isWaitingServerUpdate()){
            displayMessage("Waiting Server's updates");
            return false;
        }
        if (isRegistrationPhase()) return true;
        /*if (!game.isMyTurn()) {
            displayMessage("It is not your turn!");
            return false;
        }*/
        return true;
    }

    public void checkInput(String input ) {
        if(!isActive())
            return;
        if(input.equals("/ff") || input.equals("/surrender")){
            /*if (game.getPlayersInGame().size()==0){
                cli.showMessage("It's too early to surrender!");
                return;
            }*/
            messageToServerHandler.manageSurrender();
            setActive(false);
            return;
        }
        if (!canPlay()) return;
        if (isRegistrationPhase()) checkInputRegistrationPhase(input);
        else checkInputGamePhase(input);
    }
    /**
     * Checks messages during registration phase, based on the current action, sends message to socket if input is correct, else sets an error message
     * @param input cli input
     */
    private void checkInputRegistrationPhase(String input) {

        switch (getLastRegistrationMessage()){
            case ASK_NICK , NICK_ERR-> {
                if (input.equals("")) {
                    cli.showMessage("Name not valid!");
                    askNickname();
                    return;
                }
                if (input.length() > 25){
                    cli.showMessage("Name too long!");
                    askNickname();
                    return;
                }
            }
            case PLAYER_NUM, TOO_MANY_PLAYERS -> {
                if (!input.equals("2") && !input.equals("3") && !input.equals("1") && !input.equals("4")) {
                    cli.showMessage("Number of players not available!");
                    askNumberOfPlayers();
                    return;
                }
            }
        }
        messageToServerHandler.sendMessageToServer(input);
    }
    private void checkInputGamePhase(String input) {


    }

    // REGISTRATION PHASE --------------------------------------------------------------------------------------------

    @Override
    public synchronized void askNickname (){ System.out.println("Hello, what's your Nickname?"); }

    @Override
    public synchronized void askNumberOfPlayers() { System.out.println("How many players do you want to play with"); }

    public synchronized void confirmRegistration(String nickname) {
        super.confirmRegistration(nickname);
        setWaitingServerUpdate(true);
        cli.showMessage("Hello " + nickname + "! Registration has been completed successfully.");
    }

    // SETUP PHASE -----------------------------------------------------------------------------------------------------

    @Override
    public void chooseLeadersAction(){
        cli.showMessage("Choose 2 of leaders among these:\n");
        List<Integer> lId = cli.chooseLeader(getPlayer().getLeaders());

        getPlayer().setLeaders(convertIdToLeaderCard(lId));

    }

    @Override
    public void chooseResourceAction() {
        int quantity;
        List<BiElement<Resource, Integer>> res;

        if(getPlayer().getTurnNumber() == 2)
            quantity = 1;
        else
            quantity = 2;

        cli.showMessage("You can choose no." + quantity + " resources");
        res = cli.chooseResources(quantity);

        // TODO: set resources in the correct place;

    }


    // Message From Server ---------------------------------------------------------------------------------------------

   @Override
    public boolean ackConfirmed(String msg) {
        if (msg.equals("True"))
            return true;
        return false;
    }

    @Override
    public void chooseStorageAction(ChoosePlacementsInStorageMessage msg){
        cli.showMessage("Choose a storage for each of the following resources: " + msg.getResources());
    }

    // GAME PHASES -----------------------------------------------------------------------------------------------------

    @Override
    public void buyFromMarket(){
        String dim;
        char dimChar;
        int index, lowerBound = 1, upperBound;

        dim = cli.marketDimChoose();

        if(dim.equals("c")){
            upperBound = 3;
            dimChar = 'c';
        }else{
            upperBound = 4;
            dimChar = 'r';
        }

        index = cli.marketIntChoose(upperBound, lowerBound);

        //if players has marble leader active, ask if he/she wants to use it and for how many times
        List<LeaderCard> leaders = getLeaders();
        List<MarbleAbility> marbleAbilities = new ArrayList<>();
        int tot = 0;

        if(!leaders.isEmpty()){
            for(LeaderCard card : leaders){
                if(card.isActive() && card instanceof MarbleAbility){
                    tot++;
                    marbleAbilities.add((MarbleAbility) card);
                }
            }
        }

        List<BiElement<MarbleAbility, Integer>> leaderUsage = new ArrayList<>();
        if(tot>0){
            System.out.println("You have " + tot + " MarbleAbility Leaders that can be used now.\n" +
                    "For each card, type how many marble exchanges you want to perform with them. 0 if no exchange.");
            for(MarbleAbility card : marbleAbilities){
                int j = cli.marketLeaderChoose(card);
                if(j>0){
                    leaderUsage.add(new BiElement<>(card, j));
                }
            }
        }

        /*BuyMarketCommand buyMarketCommand = new BuyMarketCommand();
        BuyMarketMessage message = new BuyMarketMessage(dimChar, index, leaderUsage);
        buyMarketCommand.generateEnvelope(message);*/
    }

    @Override
    public void activateLeader() {
        List<LeaderCard> leaders = getLeaders();
        List<LeaderCard> activable = new ArrayList<>();
        for(LeaderCard leader : leaders){
            if(!leader.isActive()){
                activable.add(leader);
            }
        }
        if(leaders.size()==0){
            System.out.println("You don't have any leader.");
            return;
        }
        if(activable.size()==0){
            System.out.println("All your leaders are already active.");
            return;
        }

        messageToServerHandler.generateEnvelope(MessageID.ACTIVATE_LEADER, gson.toJson(cli.activeLeaderFromId(activable.size()), Integer.class));
    }


}
