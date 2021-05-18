package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.client.model.ClientGame;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.EndTurnMessage;
import it.polimi.ingsw.messages.concreteMessages.TurnNumberMessage;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ClientController {
    private ViewInterface view;
    private List<ConcreteProductionCard> allProductionCards;
    private List<LeaderCard> allLeaders = new ArrayList<>();
    private List<LeaderCard> leaders = new ArrayList<>();
    private List<ConcreteProductionCard> availableProductionCard = new ArrayList<>();
    private List<NubPlayer> otherPlayers = new ArrayList<>();
    private List<NubPlayer> totalPlayers = new ArrayList<>();
    private List<String> playersNames;
    private NubPlayer player;
    private NubPlayer currPlayer;
    private Market market;
    private boolean active = true;
    private ClientGame game = new ClientGame();

    private MessageID lastRegistrationMessage;
    private MessageID lastGameMessage;

    private boolean waitingServerUpdate = false;
    private boolean registrationPhase = true;
    private boolean gameOver = false;

    protected boolean storeOk = true;

    private static final String prodPath = "/json/ProductionCards.json",
            leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
            leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
            leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
            leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
            tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
            tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
            tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";

    protected MessageToServerHandler messageToServerHandler;

    protected MessageToServerHandler getMessageToServerHandler(){return messageToServerHandler;}

    public MessageID getLastRegistrationMessage() {
        return lastRegistrationMessage;
    }

    public MessageID getLastGameMessage() {
        return lastGameMessage;
    }

    public boolean isWaitingServerUpdate() {
        return waitingServerUpdate;
    }

    public void setRegistrationPhase(boolean registrationPhase) {
        this.registrationPhase = registrationPhase;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public synchronized boolean isActive(){
        return active;
    }
    public void setLastGameMessage(MessageID lastGameMessage) {
        this.lastGameMessage = lastGameMessage;
    }

    public void setLastRegistrationMessage(MessageID lastRegistrationMessage) {
        this.lastRegistrationMessage = lastRegistrationMessage;
    }

    public synchronized void setClosedConnection(String s) {
        gameOver = true;
        displayMessage(s);
    }

    public synchronized void setWaitingServerUpdate(boolean waitingServerUpdate) {
        this.waitingServerUpdate = waitingServerUpdate;
    }

    public synchronized boolean isRegistrationPhase() {
        return registrationPhase;
    }

    protected void connectionError(){
        setActive(false);
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    private void initAllCards(){
        Gson gson = new Gson();
        Reader reader;

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(prodPath)));
        allProductionCards = gson.fromJson(reader, new TypeToken<List<ConcreteProductionCard>>(){}.getType());

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadDiscountPath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<DiscountAbility>>(){}.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadStoragePath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<StorageAbility>>(){}.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadMarblePath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<MarbleAbility>>(){}.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadBoostPath)));
        allLeaders.addAll(gson.fromJson(reader, new TypeToken<List<BoostAbility>>(){}.getType()));
    }

    public List<LeaderCard> getLeaders(){return leaders;}
    public void addLeaders(LeaderCard leader){leaders.add(leader);}
    public void removeLeader(LeaderCard leader){leaders.remove(leader);}
    public void addPlayers(NubPlayer player){this.otherPlayers.add(player);}
    public List<NubPlayer> getOtherPlayers(){return otherPlayers;}

    public List<NubPlayer> getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(List<NubPlayer> totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public void setOtherPlayers(List<NubPlayer> otherPlayers) {
        this.otherPlayers = otherPlayers;
    }

    public void setPlayer(NubPlayer player) {
        this.player = player;
    }

    public NubPlayer getPlayer(){return player;}
    public void initAvailableProductionCard(){}
    public void setMarket(Resource[][] market, Resource extra){ this.market = new Market(market,extra);}

    public abstract boolean setup() throws IOException;
    // SETUP MESSAGES -----------------------------------------------------------------------------------------

    public abstract void askNickname ();
    public abstract void askNumberOfPlayers ();

    public void setTotalPlayers(TurnNumberMessage msg){
        for (int i = 0; i < msg.getTurnAss().size(); i++) {
            if (!msg.getTurnAss().get(i).getFirstValue().equals(player.getNickname())){
                NubPlayer np = new NubPlayer(msg.getTurnAss().get(i).getFirstValue());
                totalPlayers.add(np);
                np.setTurnNumber(msg.getTurnAss().get(i).getSecondValue());
                otherPlayers.add(np);
            }
            else
                player.setTurnNumber(msg.getTurnAss().get(i).getSecondValue());
        }

        System.out.println("Before sorting: " + totalPlayers.stream().map(NubPlayer::getTurnNumber).collect(Collectors.toList()));

        totalPlayers.stream().sorted(NubPlayer.getComparator());

        System.out.println("After sorting: " + totalPlayers.stream().map(NubPlayer::getTurnNumber).collect(Collectors.toList()));
    }

    public void setLeaderAvailable(String leaders){
        player.setLeaders(new ArrayList<>(convertIdToLeaderCard(convertStringToListInteger(leaders))));

        chooseLeadersAction();
    }

    public abstract void startGame();
    // ERROR MESSAGE ACTIONS -------------------------------------------------------------------------------------------

    public void cardNotAvailable(){
        displayMessage("The chosen card is not available");
    }

    public void badProductionRequest(){
        displayMessage("There something wrong with the storage");
    }

    public void badPaymentRequest(){
        displayMessage("don't have enough resources");
    }

    public void badDimensionRequest(){
        displayMessage("The dimension is wrong");
    }
    public void wrongStackRequest(){
        displayMessage("Stacks are from 1 to 3/4");
    }
    public void wrongLevelRequest(){
        displayMessage("The level of the card is wrong");
    }

    public void badStorageRequest(){
        displayMessage("Wrong resources placement");
        storeOk = false;
    }

    public abstract boolean ackConfirmed (String msg);
    public abstract void displayMessage(String str);

    //---------------------------------ACTIONS: behaviours caused by Messages--------------------------

    public abstract void chooseResourceAction();
    public abstract void chooseStorageAction(String s);
    public abstract void chooseLeadersAction();

    public abstract void updateMarket();
    public abstract void updateAvailableProductionCards();
    public abstract void updateOtherPlayer(NubPlayer pp);
    public abstract void moveLorenzo(int currentPosition);

    public void updateAction(UpdateMessage msg){
        if(market==null){
            market = new Market(msg.getMarketBoard(), msg.getExtraMarble());
        }else {
            market.setMarketBoard(msg.getMarketBoard());
            market.setExtra(msg.getExtraMarble());
        }

        if(msg.getAvailableProductionCards() != null || !msg.getAvailableProductionCards().isEmpty())
            availableProductionCard = convertIdToProductionCard(msg.getAvailableProductionCards());

        if(!isRegistrationPhase()) {
            ConcreteProductionCard boughtProductionCard = null;
            if(msg.getProductionCardId() != null)
                boughtProductionCard = convertIdToProductionCard(msg.getProductionCardId().getFirstValue());
            for (NubPlayer pp : otherPlayers) {
                if (pp.getTurnNumber() == msg.getPlayerId()) {
                    pp.setCurrPos(msg.getPlayerPos());
                    if (boughtProductionCard != null)
                        pp.addProductionCard(boughtProductionCard, msg.getProductionCardId().getSecondValue() - 1);
                    if (msg.getLeadersId() != null || !msg.getLeadersId().isEmpty())
                        pp.setLeaders(convertIdToLeaderCard(msg.getLeadersId()));

                    Map<BiElement<Resource, Storage>, Integer> resources = msg.getAddedResources();
                    if(resources.size()>0) {
                        resources.forEach(pp::addResources);
                    }

                    resources = msg.getRemovedResources();
                    if(resources.size()>0){
                        resources.forEach(pp::removeResources);
                    }

                    updateOtherPlayer(pp);
                    break;
                }
            }

        }
        updateMarket();
        updateAvailableProductionCards();

        if (isRegistrationPhase())
            startGame();
        else{
            currPlayer = totalPlayers.get(msg.getNextPlayerId()-1);

            if (currPlayer.getTurnNumber() == player.getTurnNumber())
                player.setMyTurn(true);

            showCurrentTurn(currPlayer.getNickname());
        }


    }

    public abstract void showCurrentTurn(String s);

    public NubPlayer getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(NubPlayer currPlayer) {
        this.currPlayer = currPlayer;
    }

    public void endTurn(EndTurnMessage msg){
        this.currPlayer = totalPlayers.get(msg.getNextPlayerId()-1);
        this.availableProductionCard = convertIdToProductionCard(msg.getBuyableProdCardsIds());

        if (getPlayer().getTurnNumber() == currPlayer.getTurnNumber()) {
            getPlayer().setMyTurn(true);
            startTurnPhase();
        }
        else
            for (NubPlayer p : getTotalPlayers()) {
                if (p.getTurnNumber() == 1)
                    showCurrentTurn(p.getNickname());
            }            
    }

    protected abstract void startTurnPhase();

    public abstract void buyFromMarket();
    public abstract void activateLeader();

    /*public void buyFromMarket(){
        String dim;
        char dimChar;
        int index, lowerBound = 1, upperBound;
        do {
            System.out.println("Choose a row or a column from the market by typing R or C");
            dim = scanner.nextLine();
            dim = dim.toLowerCase();
        }while(!dim.equals("c") && !dim.equals("r"));
        if(dim.equals("c")){
            upperBound = 3;
            dimChar = 'c';
        }else{
            upperBound = 4;
            dimChar = 'r';
        }
        do{
            System.out.println("Choose the index - must be between " + lowerBound + " and " + upperBound);
            index = scanner.nextInt();
        }while(index<lowerBound || index>upperBound);

        //if players has marble leader active, ask if he/she wants to use it and for how many times
        List<LeaderCard> leaders = controller.getLeaders();
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
                System.out.println("How many for this card?\n" + card);
                int j = scanner.nextInt();
                if(j>0){
                    leaderUsage.add(new BiElement<>(card, j));
                }
            }
        }

        BuyMarketCommand buyMarketCommand = new BuyMarketCommand(controller);
        BuyMarketMessage message = new BuyMarketMessage(dimChar, index, leaderUsage);
        buyMarketCommand.generateEnvelope(message);
    }

    public void activateLeader() {
        int leaderId;
        List<LeaderCard> leaders = contrgetLeaders();
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
        System.out.println("You can activate " + activable.size() + " leaders. Which one do you want to activate?" +
                "Insert its id.\n" + activable);
        leaderId = scanner.nextInt();
        MessageHandler.generateEnvelope(MessageID.ACTIVATE_LEADER, gson.toJson(leaderId, Integer.class));
    }*/

    //---------------------------CONVERTERS---------------------------------
    protected List<ConcreteProductionCard> convertIdToProductionCard(List<Integer> ids){
        return allProductionCards.stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
    }
    protected ConcreteProductionCard convertIdToProductionCard(int id){
        for(ConcreteProductionCard pc : allProductionCards){
            if(pc.getId()==id)
                return pc;
        }
        return null;
    }

    protected List<LeaderCard> convertIdToLeaderCard(List<Integer> ids){

        List <LeaderCard> leaders = (allLeaders.stream().filter(x -> ids.contains(x.getId()))
                                                        .collect(Collectors.toList()));

        return leaders;

    }

    protected List<Integer> convertStringToListInteger (String s){
        List<Integer> intList = (new ArrayList<>(Arrays.asList(s.substring(1, s.length()-1).split(", ")))).stream()
                                                                                                                .map(Integer::parseInt)
                                                                                                                .collect(Collectors.toList());
        return intList;
    }

    public Resource convertStringToResource(String s){
        switch(s){
            case "STONE": return Resource.STONE;
            case "SHIELD": return Resource.SHIELD;
            case "SERVANT": return Resource.SERVANT;
            case "COIN": return Resource.COIN;
        }
        return null;
    }


    public void confirmRegistration(String nickName){
        player = new NubPlayer(nickName);
        totalPlayers.add(player);
        initAllCards();
    }
}
