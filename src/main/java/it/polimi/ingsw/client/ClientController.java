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
    private List<ConcreteProductionCard> availableProductionCard = new ArrayList<>();
    private List<NubPlayer> otherPlayers = new ArrayList<>();
    private List<NubPlayer> totalPlayers = new ArrayList<>();
    private List<String> playersNames;
    protected NubPlayer player;
    private NubPlayer currPlayer;
    private Market market;
    private boolean active = true;
    private ClientGame game = new ClientGame();
    protected List<BiElement<Resource, Storage>> storeRes;

    private MessageID lastRegistrationMessage;
    private MessageID lastGameMessage;

    private boolean waitingServerUpdate = false;
    private boolean registrationPhase = true;
    private boolean gameOver = false;

    protected final Object currPlayerChange = new Object();

    protected boolean normalTurn;

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

        //System.out.println("Before sorting: " + totalPlayers.stream().map(NubPlayer::getTurnNumber).collect(Collectors.toList()));

        totalPlayers.sort(NubPlayer.getComparator());

        //System.out.println("After sorting: " + totalPlayers.stream().map(NubPlayer::getTurnNumber).collect(Collectors.toList()));
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
        chooseStorageAction(new ArrayList<>(storeRes.stream().map(BiElement::getFirstValue).collect(Collectors.toList())));
    }

    public abstract boolean ackConfirmed (String msg);
    public abstract void displayMessage(String str);

    //---------------------------------ACTIONS: behaviours caused by Messages--------------------------

    public abstract void chooseStorageAfterMarketAction(String s);
    public abstract void chooseResourceAction();
    public abstract void chooseStorageAction(List<Resource> s);
    public abstract void chooseLeadersAction();

    public abstract void updateMarket();
    public abstract void updateAvailableProductionCards();

    /**Show other players what the last player has done in his/her turn once it has been set on the corresponding
     * {@link NubPlayer} object.
     * @param player the player who played last turn*/
    public abstract void updateOtherPlayer(NubPlayer player);

    /**Move Lorenzo on the faith track*/
    public abstract void moveLorenzo(int currentPosition);

    public synchronized void updateAction(UpdateMessage msg){

        System.out.println(msg.getPlayerId());
        System.out.println(msg.getNextPlayerId());

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
                //update last player state for this client
                if (pp.getTurnNumber() == msg.getPlayerId()) {
                    pp.setCurrPos(msg.getPlayerPos());
                    if (boughtProductionCard != null)
                        pp.addProductionCard(boughtProductionCard, msg.getProductionCardId().getSecondValue() - 1);

                    if (msg.getLeadersId() != null || !msg.getLeadersId().isEmpty())
                        pp.setLeaders(convertIdToLeaderCard(msg.getLeadersId()));

                    if (msg.getAddedResources() != null) {
                        Map<BiElement<Resource, Storage>, Integer> resources = msg.getAddedResources();
                        if (resources.size() > 0) {
                            resources.forEach(pp::addResources);
                        }

                        resources = msg.getRemovedResources();
                        if (resources.size() > 0) {
                            resources.forEach(pp::removeResources);
                        }
                    }

                    updateOtherPlayer(pp);
                    break;
                }
            }

        }else{
            //update message iniziale
        }
        updateMarket();
        updateAvailableProductionCards();

        for(NubPlayer p : totalPlayers){
            if(p.getTurnNumber() == msg.getNextPlayerId())
                currPlayer = p;
        }

        startGame();

        if (registrationPhase) {
            registrationPhase = false;
            //startGame();
        }
    }

    public abstract void showCurrentTurn(String s);

    public NubPlayer getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(NubPlayer currPlayer) {
        this.currPlayer = currPlayer;
    }

    public void continueTurn(Boolean basicActionDone){
        if (!registrationPhase){
            System.out.println(basicActionDone);
            normalTurn = !basicActionDone;
            startTurnPhase();
        }
    }

    public synchronized void endTurn(EndTurnMessage msg){

        for (BiElement<Resource, Storage> elem : storeRes)
            player.addResources(elem, 1);
        storeRes.clear();

        player.setMyTurn(false);

        this.currPlayer = totalPlayers.get(msg.getNextPlayerId()-1);
        this.availableProductionCard = convertIdToProductionCard(msg.getBuyableProdCardsIds());


        if (player.getTurnNumber() == currPlayer.getTurnNumber()) {
            getPlayer().setMyTurn(true);
            normalTurn = true;
            startTurnPhase();
        } else {
            for (NubPlayer p : totalPlayers) {
                if (p.getTurnNumber() == 1)
                    showCurrentTurn(p.getNickname());
            }
        }

    }

    protected abstract void startTurnPhase();

    public abstract void buyFromMarket();
    public abstract void activateLeader();


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

    public void passTurn(){
        messageToServerHandler.generateEnvelope(MessageID.END_TURN, "");
    };
}
