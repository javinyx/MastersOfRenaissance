package it.polimi.ingsw.client.CLI;

import com.google.gson.Gson;
import it.polimi.ingsw.client.ClientController;
import it.polimi.ingsw.client.LocalAdapter;
import it.polimi.ingsw.client.MessageReceiver;
import it.polimi.ingsw.client.MessageToServerHandler;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.*;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.ResourcesWallet;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Class that manage the phases of the game in cli mode
 */
public class CliController extends ClientController {

    private final Cli cli;
    private Gson gson = new Gson();

    public CliController() {
        this.cli = new Cli(this);
    }

    /**
     * Setup the cli, start the message receiver thread and the input from keyboard thread
     * @return true if the setup isn't crash
     */
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

    /**
     * Check the user input and allow it only if the player is in his turn
     */
    public void checkInput(String input ) {
        if(!isActive())
            return;
        if(input.equals("/ff") || input.equals("/surrender")){
            messageToServerHandler.manageSurrender();
            setActive(false);
            return;
        }
        if (!canPlay()) return;
        if (isRegistrationPhase())
            checkInputRegistrationPhase(input);
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
                if (input.length() > 12){
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

    public synchronized void nickError(){
        cli.showMessage("The chosen Nickname can't be used in this game");
    }
    // SETUP PHASE -----------------------------------------------------------------------------------------------------

    /**
     * Let the player choose the initial 2 leader cards
     */
    @Override
    public void chooseLeadersAction(){
        cli.clearScreen();
        cli.showMessage("Choose 2 leaders among these:");
        List<Integer> lId = cli.chooseInitialLeader(getPlayer().getLeaders());

        getPlayer().setLeaders(convertIdToLeaderCard(lId));

        messageToServerHandler.generateEnvelope(MessageID.CHOOSE_LEADER_CARDS, lId.toString());
    }

    /**
     * Let the player choose the initial resource if possible
     */
    @Override
    public void chooseResourceAction(int quantity) {
        List<Resource> res;

        if(getPlayer().getTurnNumber() == 3 || getPlayer().getTurnNumber()==4){
            System.out.println("You earned one faith point");
        }

        cli.showMessage("You can choose no." + quantity + " resources");
        res = cli.chooseResources(quantity);

        chooseStorageAction(res);
    }

    /**
     * Set the turn of the player or display the player that is playing
     */
    @Override
    public synchronized void startGame() {
        if(isRegistrationPhase()) {
            cli.showMessage("You are ready to play, wait until it's your turn");
        }
        setRegistrationPhase(false);
        //se siamo qui allora è il mio turno e posso ritornare nel client controller
        if(getCurrPlayer().equals(player)) {
            player.setMyTurn(true);
            setCurrPlayer(player);
            normalTurn = true;
            startTurnPhase();
        }else{
            showBoard();
            player.setMyTurn(false);
            showCurrentTurn(getCurrPlayer().getNickname());
        }
    }

    /**
     * Don't allow the player to skip the turn
     */
    public void startGameNotEndTurn(){
        System.out.println("You can't skip the turn.");
        cli.pressEnter();
        startTurnPhase();
    }

    /**
     * Tell to the player that other players have to join before start the game
     */
    @Override
    public void displayWaitMessage() {
        displayMessage("Wait other player to join.");
        setWaitingServerUpdate(true);
    }

    // GAME PHASES -----------------------------------------------------------------------------------------------------

    /**
     * Display the turn options
     */
    public synchronized void startTurnPhase(){
        if (normalTurn)
            cli.displayTurnOption();
        else
            cli.displayLightTurnOption();
    }

    public void showBoard(){
        cli.clearScreen();
        cli.showPlayerBoard();
    }

    public void viewProductionCard(){
        cli.showProductionCardInTheStore(availableProductionCard);
        cli.pressEnter();
        startTurnPhase();
    }

    /**
     * Display the information of the player
     */
    public void viewYourInfo(){
        List<Resource> extra = new ArrayList<>();
        int shield = 0, stone = 0, ser = 0, coin = 0;

        cli.showYourLeader(player.getLeaders());
        System.out.println();

        for(BiElement<Resource, Storage> res : player.getAllResources().keySet()){
            if (res.getSecondValue().equals(Storage.LOOTCHEST)) {
                switch (res.getFirstValue()){
                    case SERVANT -> ser++;
                    case SHIELD -> shield++;
                    case STONE -> stone++;
                    case COIN -> coin++;
                }
            }
            else if (res.getSecondValue().equals(Storage.EXTRA1) || res.getSecondValue().equals(Storage.EXTRA2))
                extra.add(res.getFirstValue());
        }

        System.out.println("In the Strong Box there are:");
        System.out.println("Servant: " + ser);
        System.out.println("Stone: " + stone);
        System.out.println("Shield: " + shield);
        System.out.println("Coin: " + coin);

        for (LeaderCard led : player.getLeaders()) {
            if (led instanceof StorageAbility) {
                System.out.println("In the extra space there are: ");
                System.out.println(extra);
                break;
            }
        }

        System.out.println("Status of Pope Passes:");
        for (int i = 1; i < 4; i++) {
            if (i <= popeStatusGeneral.size())
                switch (popeStatusGeneral.get(i-1)){
                    case 0 -> System.out.println("Pope Passe n." + i + ": ACTIVE");
                    case 1 -> System.out.println("Pope Passe n." + i + ": ALREADY ACTIVATE BUT NOT FOR YOU");
                    case 2 -> System.out.println("Pope Passe n." + i + ": NOT ACTIVE");
                }
            else
                System.out.println("Pope Passe n." + i + ": NOT ACTIVE");
        }

        cli.pressEnter();
        startTurnPhase();

    }

    public void buyProductionCard(){

        int prodId, stack;
        List<Integer> leadCard = new ArrayList<>();
        List<Resource> fromWare = new ArrayList<>();
        List<Resource> fromLoot = new ArrayList<>();
        List<Resource> fromLeader1 = new ArrayList<>();
        List<Resource> fromLeader2 = new ArrayList<>();
        ResourcesWallet resWal = new ResourcesWallet();

        cli.showProductionCardInTheStore(availableProductionCard);
        prodId = cli.chooseProdCard(availableProductionCard);
        stack = cli.chooseProdStack();

        if(cli.wantPlayLeader()) {
            for (LeaderCard led : getPlayer().getLeaders())
                if (led instanceof DiscountAbility && led.isActive()) {
                    System.out.println("Select the leader you want to get the discount");
                    leadCard = cli.chooseLeader(getPlayer().getLeaders());
                }
        }
        System.out.println("You have to spend these resources: "+ convertIdToProductionCard(prodId).getCost());

        System.out.println("Select the resources you want to use for the Development Card");

        boolean a, b, c, d;

        do{

            List<ConcreteProductionCard> prod = new ArrayList<>();
            prod.add(convertIdToProductionCard(prodId));
            cli.selectResWalletProd(prod, fromWare, fromLoot, fromLeader1, fromLeader2);

            a = resWal.setWarehouseTray(fromWare);
            b = resWal.setLootchestTray(fromLoot);
            c = resWal.setExtraStorage(fromLeader1, 0);
            d = resWal.setExtraStorage(fromLeader2, 1);

            if ((!a && !resWal.getWarehouseTray().isEmpty()) || (!b && !resWal.getLootchestTray().isEmpty()) || (!c && !resWal.getExtraStorage(0).isEmpty()) || (!d && !resWal.getExtraStorage(1).isEmpty()))
                System.out.println("An error occur in the selection of the Resources");

        }while ((!a && !resWal.getWarehouseTray().isEmpty()) || (!b && !resWal.getLootchestTray().isEmpty()) || (!c && !resWal.getExtraStorage(0).isEmpty()) || (!d && !resWal.getExtraStorage(1).isEmpty()));


        BuyProductionMessage msg = new BuyProductionMessage(prodId, stack, leadCard, resWal);
        messageToServerHandler.generateEnvelope(MessageID.BUY_PRODUCTION_CARD, gson.toJson(msg));
    }

    public void startProduction(){
        /*List<Resource> cost = new ArrayList<>();
        cost.add(Resource.SHIELD);cost.add(Resource.SERVANT);cost.add(Resource.STONE);
        List<Resource> reqRes = new ArrayList<>();
        reqRes.add(Resource.STONE);
        List<Resource> prod11 = new ArrayList<>();
        prod11.add(Resource.SERVANT);
        ConcreteProductionCard prod = new ConcreteProductionCard(5,2, ColorEnum.GREEN,1, cost, reqRes, prod11);
        player.addProductionCard(prod, 0);*/

        List<ConcreteProductionCard> prodCard;
        List<LeaderCard> leadCard = null;
        List<BoostAbility> betterLeaderCard = null;
        List<Resource> basicIn, leadOut = null;
        List<Resource> fromWare = new ArrayList<>();
        List<Resource> fromLoot = new ArrayList<>();
        List<Resource> fromLeader1 = new ArrayList<>();
        List<Resource> fromLeader2 = new ArrayList<>();
        Resource basicOut = null;
        boolean basic = false;
        ResourcesWallet resWal = new ResourcesWallet();

        if (player.getProductionStacks().get(0).size() != 0 && player.getProductionStacks().get(1).size() != 0 && player.getProductionStacks().get(2).size() != 0){
            System.out.println("Select the Development Card you want to use");
            prodCard = cli.selectProdCard();

            System.out.println("Select the resources you want to use for the Development Card");

            boolean a, b, c, d;
            do {

                cli.selectWalletForProduction(prodCard, fromWare, fromLoot, fromLeader1, fromLeader2);

                a = resWal.setWarehouseTray(fromWare);
                b = resWal.setLootchestTray(fromLoot);
                c = resWal.setExtraStorage(fromLeader1, 0);
                d = resWal.setExtraStorage(fromLeader2, 1);

                if ((!a && !resWal.getWarehouseTray().isEmpty()) || (!b && !resWal.getLootchestTray().isEmpty()) || (!c && !resWal.getExtraStorage(0).isEmpty()) || (!d && !resWal.getExtraStorage(1).isEmpty()))
                    System.out.println("An error occur in the selection of the Resources");

            } while ((!a && !resWal.getWarehouseTray().isEmpty()) || (!b && !resWal.getLootchestTray().isEmpty()) || (!c && !resWal.getExtraStorage(0).isEmpty()) || (!d && !resWal.getExtraStorage(1).isEmpty()));

            if (cli.wantPlayLeader()) {
                for (LeaderCard led : getPlayer().getLeaders())
                    if (led instanceof BoostAbility && led.isActive()) {
                        System.out.println("Select the leader you want to produce");
                        leadCard = convertIdToLeaderCard(cli.chooseLeader(getPlayer().getLeaders()));
                        betterLeaderCard = leadCard.stream().map(x -> (BoostAbility) x).collect(Collectors.toList());
                        leadOut = cli.chooseLeaderOut(leadCard);
                        System.out.println("Select the resources you want to use for the Leader Card");
                        cli.selectLeadWalletProd((BoostAbility) led, fromWare, fromLoot, fromLeader1, fromLeader2);
                    }
            }

            basicIn = cli.doBasicProd1(fromWare, fromLoot, fromLeader1, fromLeader2);
            if (basicIn != null) {
                basic = true;
                basicOut = cli.doBasicProd2();
            }

            resWal.setWarehouseTray(fromWare);
            resWal.setLootchestTray(fromLoot);
            resWal.setExtraStorage(fromLeader1, 0);
            resWal.setExtraStorage(fromLeader2, 1);

            ProduceMessage msg = new ProduceMessage(prodCard, resWal, betterLeaderCard, leadOut, basic, basicOut, basicIn);

            messageToServerHandler.generateEnvelope(MessageID.PRODUCE, gson.toJson(msg));
        }

        else{
            displayMessage("You have no leader cards");
            cli.pressEnter();
            startTurnPhase();
        }

    }

    /**
     * Display the situation of the other players
     */
    public void viewOpponents(){

        if(otherPlayers.size() == 0)
            System.out.println("You're playing alone");
        else{
            for(NubPlayer np : otherPlayers){
                System.out.println(np.getNickname() + " is in position number: " + np.getCurrPos());
                System.out.println("Here the list of the stuff of "+np.getNickname());
                cli.showPlayerProdCard(np.getProductionStacks());
                cli.showPlayerLeader(np.getLeaders());
                cli.showPlayerResources(np.getAllResources());
            }
        }

        cli.pressEnter();
        startTurnPhase();

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

        messageToServerHandler.generateEnvelope(MessageID.ACTIVATE_LEADER, String.valueOf(cli.activeLeaderFromId(activable)));
    }

    public void rearrangeWarehouse(){

        List<Resource> elem = new ArrayList<>();

        for (BiElement<Resource, Storage> res : player.getAllResources().keySet()) {
            if (res.getSecondValue().equals(Storage.WAREHOUSE_SMALL) || res.getSecondValue().equals(Storage.WAREHOUSE_MID) || res.getSecondValue().equals(Storage.WAREHOUSE_LARGE))
                for (int i = 0; i < player.getAllResources().get(res); i++) {
                    elem.add(res.getFirstValue());
                }

        }
        cli.showMessage("Choose a storage for each of the following resources:" + elem);
        setRegistrationPhase(true);
        storeRes = cli.storeResources(elem);
        setRegistrationPhase(false);

        StoreResourcesMessage msg = new StoreResourcesMessage(storeRes, getPlayer().getTurnNumber());

        messageToServerHandler.generateEnvelope(MessageID.REARRANGE_WAREHOUSE, gson.toJson(msg, StoreResourcesMessage.class));

    }

    @Override
    public void showLorenzoStatus(ActionToken act) {

        System.out.println("Lorenzo now is in position: " + lorenzoPos);
        System.out.println(act.print());
        cli.pressEnter();

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

    /**
     * Let the player choose in what storage he want to put his resources
     */
    @Override
    public void chooseStorageAction(List<Resource> res){

        cli.showMessage("Choose a storage for each of the following resources:" + res);

        storeRes = cli.storeResources(res);

        StoreResourcesMessage msg = new StoreResourcesMessage(storeRes, getPlayer().getTurnNumber());

        messageToServerHandler.generateEnvelope(MessageID.STORE_RESOURCES, gson.toJson(msg, StoreResourcesMessage.class));

    }

    @Override
    public void showCurrentTurn(String s){
        cli.showMessage(getPlayer().isMyTurn() ? "It is your turn!" : "It is " + getCurrPlayer().getNickname() + "'s turn, wait until your turn.");
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

    /**
     * Show the position of Lorenzo on the track
     */
    @Override
    public void moveLorenzo(int currentPosition) {
        if(currentPosition == 24){
            cli.showMessage("Lorenzo completed the faith track. \nYou lose! :)");
        }
        cli.showMessage("Lorenzo moved. Now he is cell " + currentPosition);
    }

    @Override
    public void updatePositionAction(PlayersPositionMessage msg){
        List<NubPlayer> allPlayers = getTotalPlayers();

        for(BiElement<Integer, Integer> pos : msg.getNewPlayersPosition()){
            if(pos.getFirstValue()==0){
                //System.out.println("Lorenzo is at cell " + pos.getSecondValue());
            }else{
                for(NubPlayer p : allPlayers){
                    if(p.getTurnNumber()== pos.getFirstValue() && p.getCurrPos()!=pos.getSecondValue()){
                        //System.out.println(p.getNickname() + " moved. Now is at cell " + pos.getSecondValue());
                        p.setCurrPos(pos.getSecondValue());
                        break;
                    }else if(p.getTurnNumber()==pos.getFirstValue() && p.equals(getPlayer())){
                        //System.out.println("You're in cell " + pos.getSecondValue() +" on the faith track.");
                        player.setCurrPos(pos.getSecondValue());
                        break;
                    }
                }
            }
        }
    }

    // ERROR MESSAGE FROM SERVER ---------------------------------------------------------------------------------------
    public void cardNotAvailable(){
        displayMessage("The chosen card is not available");
        startTurnPhase();

    }

    public void badProductionRequest(){
        displayMessage("There something wrong with the storage");
        startTurnPhase();

    }

    public void badPaymentRequest(){
        displayMessage("don't have enough resources");
        startTurnPhase();

    }

    public void badDimensionRequest(){
        displayMessage("The dimension is wrong");
        startTurnPhase();

    }

    public void wrongStackRequest(){
        displayMessage("Stacks are from 1 to 3/4");
        startTurnPhase();

    }

    public void wrongLevelRequest(){
        displayMessage("The level of the card is wrong");
        startTurnPhase();
    }

    public void badStorageRequest(){
        displayMessage("Wrong resources placement");
        chooseStorageAction(new ArrayList<>(storeRes.stream().map(BiElement::getFirstValue).collect(Collectors.toList())));
    }

    public void badRearrangeRequest(){
        displayMessage("Wrong resources rearrange");
        rearrangeWarehouse();
    }

    public void leaderNotActivable(){
        displayMessage("This leader is not activable, you don't have enough resources.");
        cli.pressEnter();
        startTurnPhase();
    }

    /**
     * Show the win message
     */
    @Override
    public void winner(String win) {
        setGameOver(true);

        cli.showMessage(player.getNickname().equals(win)
                ? "Congratulations, you have won the match!"
                : win + " has won the match");
        setActive(false);
    }

    //Local mode -------------------------------------------------------------------------------

    /**
     * Setup the game for local mode
     */
    public void startLocalGame(){
        localGame = true;
        messageToServerHandler = new LocalAdapter(this);

        if (!cli.initialScreen())
            return;

        askNickname();
        messageToServerHandler.sendMessageToServer(cli.askNick());
        try {
            Thread t0 = cli.startContinuousRead();
            t0.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void normalProcedure(){
        cli.clearScreen();
        System.out.println("This game was created by: \nOttavia\nAlessio\nJavin\nYou can find our name in the initial ascii art too.");
        InputStream in = getClass().getResourceAsStream("/normalFile.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            for (int i = 0; i < 44; i++)
                System.out.println(reader.readLine());
        }catch (Exception e){}
        cli.pressEnter();
        startTurnPhase();
    }
}
