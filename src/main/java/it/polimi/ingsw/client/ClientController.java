package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.client.model.ClientGame;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.misc.TriElement;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class ClientController {
    private ViewInterface view;
    private boolean myTurn;
    private List<ConcreteProductionCard> allProductionCards;
    private List<LeaderCard> allLeaders = new ArrayList<>();
    private List<LeaderCard> leaders = new ArrayList<>();
    private List<ConcreteProductionCard> availableProductionCard = new ArrayList<>();
    private List<NubPlayer> otherPlayers = new ArrayList<>();
    private NubPlayer player;
    private Market market;
    private boolean active = true;
    private ClientGame game = new ClientGame();

    private MessageID lastRegistrationMessage;
    private MessageID lastGameMessage;

    private boolean waitingServerUpdate = false;
    private boolean registrationPhase = true;
    private boolean gameOver = false;

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

    public void setActive(boolean active) {
        this.active = active;
    }

    private static final String prodPath = "/json/ProductionCards.json",
            leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
            leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
            leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
            leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
            tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
            tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
            tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";

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

    public boolean getMyTurn(){return myTurn;}
    public List<LeaderCard> getLeaders(){return leaders;}
    public void addLeaders(LeaderCard leader){leaders.add(leader);}
    public void removeLeader(LeaderCard leader){leaders.remove(leader);}
    public void setMyTurn(boolean status){this.myTurn = status;}
    public void addPlayers(NubPlayer player){this.otherPlayers.add(player);}
    public List<NubPlayer> getPlayers(){return otherPlayers;}
    public NubPlayer getPlayer(){return player;}
    public void initAvailableProductionCard(){}
    public void setMarket(Resource[][] market, Resource extra){ this.market = new Market(market,extra);}

    public abstract boolean setup() throws IOException;
    // INITIALIZATION MESSAGES -----------------------------------------------------------------------------------------

    public abstract void askNickname ();
    public abstract void askNumberOfPlayers ();

    public void setTurnNumber(int turnNumber){
        player.setTurnNumber(turnNumber);
    }

    public void setLeaderAvailable(String leaders){
        player.setLeaders(new ArrayList<>(convertIdToLeaderCard(convertStringToListInteger(leaders))));

        chooseLeadersAction();
    }

    public void setPlayerList(){

    }


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

    public abstract boolean ackConfirmed (String msg);
    public abstract void displayMessage(String str);

    //---------------------------------ACTIONS: behaviours caused by Messages--------------------------

    public abstract void chooseResourceAction();
    public abstract void chooseStorageAction(ChoosePlacementsInStorageMessage msg);
    public abstract void chooseLeadersAction();


    public void updateAction(UpdateMessage msg){
        if(market==null){
            market = new Market(msg.getMarketBoard(), msg.getExtraMarble());
        }else {
            market.setMarketBoard(msg.getMarketBoard());
            market.setExtra(msg.getExtraMarble());
        }
        view.updateMarket();

        availableProductionCard = convertIdToProductionCard(msg.getAvailableProductionCards());
        view.updateAvailableProductionCards();

        ConcreteProductionCard boughtProductionCard = convertIdToProductionCard(msg.getProductionCardId().getT());
        for(NubPlayer pp : otherPlayers){
            if(pp.getTurnNumber()==msg.getPlayerId()){
                pp.setCurrPos(msg.getPlayerPos());
                pp.addProductionCard(boughtProductionCard, msg.getProductionCardId().getV()-1);
                pp.setLeaders(convertIdToLeaderCard(msg.getLeadersId()));

                List<TriElement<Resource, Storage,Integer>> actualResources, actualResourcesDupe, resources;
                actualResources = pp.getAllResources();
                actualResourcesDupe = new ArrayList<>(actualResources);
                resources = msg.getAddedResources();
                for(TriElement<Resource, Storage, Integer> actualElem : actualResourcesDupe){
                    for(TriElement<Resource,Storage,Integer> elem : resources) {
                        if (actualElem.getFirstValue().equals(elem.getFirstValue()) && actualElem.getSecondValue().equals(elem.getSecondValue())) {
                            actualElem.setThirdValue(actualElem.getThirdValue() + elem.getThirdValue());
                        }else{
                            pp.addResources(elem);
                        }
                    }
                }

                pp.addResources(msg.getAddedResources());

                resources = msg.getRemovedResources();
                actualResources = pp.getAllResources();
                actualResourcesDupe = new ArrayList<>(actualResources);
                for(TriElement<Resource,Storage, Integer> elem : resources){
                    for(TriElement<Resource,Storage,Integer> actualElem : actualResourcesDupe)
                    if(elem.getFirstValue().equals(actualElem.getFirstValue()) && elem.getSecondValue().equals(actualElem.getSecondValue())){
                        actualElem.setThirdValue(actualElem.getThirdValue() - elem.getThirdValue());
                        if(actualElem.getThirdValue()<1){
                            actualResources.remove(actualElem);
                        }
                    }
                }
                view.updateOtherPlayer(pp);
                break;
            }
        }

    }

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


    public void confirmRegistration(String nickName){
        player = new NubPlayer(nickName);
        initAllCards();
    }
}
