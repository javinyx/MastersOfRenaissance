package it.polimi.ingsw.client.CLI;

import com.google.gson.Gson;
import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.MessageReceiver;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.BuyMarketMessage;
import it.polimi.ingsw.messages.concreteMessages.ProduceMessage;
import it.polimi.ingsw.messages.concreteMessages.StoreResourcesMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.leader.BoostAbility;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.leader.MarbleAbility;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    * @return {@code true} if current player can perform actions, based on whether is his turn or not,
     * if match is over or if he's waiting for server updates
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
        if (!getPlayer().isMyTurn()) {
            displayMessage("It is not your turn!");
            return false;
        }
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
        if (isRegistrationPhase())
            checkInputRegistrationPhase(input);
        else
            checkInputGamePhase(input);
    }
    /**
     * Checks messages during registration phase, based on the current action, sends message to socket
     * if input is correct, else sets an error message.
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
    public synchronized void askNumberOfPlayers() { System.out.println("How many players do you want to play with?"); }

    public synchronized void confirmRegistration(String nickname) {
        super.confirmRegistration(nickname);
        setWaitingServerUpdate(true);
        cli.showMessage("Hello " + nickname + "! Registration has been completed successfully.");
    }


    // SETUP PHASE -----------------------------------------------------------------------------------------------------

    @Override
    public void chooseLeadersAction(){
        cli.showMessage("Choose 2 leaders among these:\n");
        List<Integer> lId = cli.chooseLeader(getPlayer().getLeaders());

        getPlayer().setLeaders(convertIdToLeaderCard(lId));

        messageToServerHandler.generateEnvelope(MessageID.CHOOSE_LEADER_CARDS, convertIdToLeaderCard(lId).toString());

    }

    @Override
    public void chooseResourceAction() {
        int quantity;
        //List<BiElement<Resource, Storage>> res;
        List<Resource> res;

        if(getPlayer().getTurnNumber() == 2)
            quantity = 1;
        else {
            quantity = 2;
            System.out.println("You earned one faith point");
        }

        cli.showMessage("You can choose no." + quantity + " resources");
        res = cli.chooseResources(quantity);

        chooseStorageAction(res);

    }

    @Override
    public synchronized void startGame() {
        if(isRegistrationPhase()) {
            cli.showMessage("You are ready to play, wait until it's your turn");
        }

        /*while(!getCurrPlayer().equals(player)){
            player.setMyTurn(false);
            showCurrentTurn(getCurrPlayer().getNickname());
            //qui si potrebbe potenzialmente inserire un metodo che stampi a video alcune opzione mentri si aspetta
            //ma potrebbe essere un po' complicato
            synchronized (currPlayerChange) {
                try {
                    currPlayerChange.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/
        //se siamo qui allora è il mio turno e posso ritornare nel client controller
        if(getCurrPlayer().equals(player)) {
            player.setMyTurn(true);
            setCurrPlayer(player);
            normalTurn = true;
            startTurnPhase();
        }else{
            player.setMyTurn(false);
            showCurrentTurn(getCurrPlayer().getNickname());
        }
    }

    // GAME PHASES -----------------------------------------------------------------------------------------------------

    public synchronized void startTurnPhase(){
        if (normalTurn)
            cli.displayTurnOption();
        else
            cli.displayLightTurnOption();

    }

    public void viewProductionCard(){

    }

    public void buyProductionCard(){



    }

    public void startProduction(){

        List<ConcreteProductionCard> prodCard;
        List<LeaderCard> leadCard = null;
        List<BoostAbility> betterLeaderCard = null;
        List<Resource> basicIn, leadOut = null;
        List<Resource> fromWare = new ArrayList<>();
        List<Resource> fromLoot = new ArrayList<>();
        List<Resource> fromLeader = new ArrayList<>();
        Resource basicOut = null;
        boolean basic = false;
        ResourcesWallet resWallet;
        String s;

        System.out.println("Select the Development Card you want to use");
        prodCard = cli.selectProdCard();

        System.out.println("Select the resources you want to use for the Development Card");
        cli.selectResWalletProd(prodCard, fromWare, fromLoot, fromLeader);

        if(cli.wantPlayLeader())
            for (LeaderCard led : getPlayer().getLeaders())
                if (led instanceof BoostAbility && led.isActive()) {
                    System.out.println("Select the leader you want to produce");
                    leadCard = convertIdToLeaderCard(cli.chooseLeader(getPlayer().getLeaders()));
                    betterLeaderCard = leadCard.stream().map(x -> (BoostAbility)x).collect(Collectors.toList());
                    leadOut = cli.chooseLeaderOut(leadCard);
                    System.out.println("Select the resources you want to use for the Leader Card");
                    cli.selectLeadWalletProd((BoostAbility)led, fromWare, fromLoot, fromLeader);
                }

        basicIn = cli.doBasicProd1(fromWare, fromLoot, fromLeader);
        if(basicIn != null) {
            basic = true;
            basicOut = cli.doBasicProd2();
        }

        ProduceMessage msg = new ProduceMessage(prodCard, null, betterLeaderCard, leadOut, basic, basicOut, basicIn);

        messageToServerHandler.generateEnvelope(MessageID.PRODUCE, gson.toJson(msg));

    }
    public void viewOpponents(){

    }

    public void discardLeader(){


        if(!getPlayer().getLeaders().get(0).isActive() || !getPlayer().getLeaders().get(1).isActive()) {
            List<LeaderCard> leaderCards = new ArrayList<>();
            int c;

            for (LeaderCard led : getPlayer().getLeaders())
                if(!led.isActive())
                    leaderCards.add(led);

            c = cli.discardLeader(leaderCards);

            getPlayer().getLeaders().removeIf(led -> led.getId() == c);

            messageToServerHandler.generateEnvelope(MessageID.DISCARD_LEADER, String.valueOf(c));

            System.out.println("You have earn a Faith Point");
            getPlayer().setCurrPos(getPlayer().getCurrPos()+1);

        }
        else
            System.out.println("You don't have any leader to discard");

    }

    @Override
    public void buyFromMarket(){
        String dim;
        char dimChar;
        int index, lowerBound = 1, upperBound;

        dim = cli.marketDimChoose();

        if(dim.equals("c")){
            upperBound = 4;
            dimChar = 'c';
        }else{
            upperBound = 3;
            dimChar = 'r';
        }

        index = cli.marketIntChoose(upperBound, lowerBound);

        //if players has marble leader active, ask if he/she wants to use it and for how many times
        List<LeaderCard> leaders = getPlayer().getLeaders();
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

        BuyMarketMessage msg = new BuyMarketMessage(dimChar, index, leaderUsage);

        messageToServerHandler.generateEnvelope(MessageID.BUY_FROM_MARKET, gson.toJson(msg, BuyMarketMessage.class));

        normalTurn = false;

    }

    @Override
    public void activateLeader() {
        List<LeaderCard> leaders = getPlayer().getLeaders();
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

    // Message From Server ---------------------------------------------------------------------------------------------

    @Override
    public boolean ackConfirmed(String msg) {
        if (msg.equals("True"))
            return true;
        return false;
    }

    @Override
    public void chooseStorageAfterMarketAction(String s){
        List<Resource> res = (new ArrayList<>(Arrays.asList(s.substring(1, s.length()-1).split(", ")))).stream()
                                                                                                             .map(this::convertStringToResource)
                                                                                                             .collect(Collectors.toList());

        chooseStorageAction(res);
    }

    @Override
    public void chooseStorageAction(List<Resource> res){

        cli.showMessage("Choose a storage for each of the following resources:" + res);

        storeRes = cli.storeResources(res);

        StoreResourcesMessage msg = new StoreResourcesMessage();

        messageToServerHandler.generateEnvelope(MessageID.STORE_RESOURCES, gson.toJson(msg, StoreResourcesMessage.class));

    }

    @Override
    public void showCurrentTurn(String s){
        cli.showMessage(getPlayer().isMyTurn() ? "It is your turn!" : "It is " + getCurrPlayer().getNickname() + "'s turn");
    }

    @Override
    public void updateMarket(){

    }

    @Override
    public void updateAvailableProductionCards() {

    }

    @Override
    public void updateOtherPlayer(NubPlayer pp) {

    }

    @Override
    public void moveLorenzo(int currentPosition) {

    }

}
