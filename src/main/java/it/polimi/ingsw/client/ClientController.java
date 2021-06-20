package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.*;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.actiontoken.ActionToken;
import it.polimi.ingsw.model.cards.actiontoken.DiscardToken;
import it.polimi.ingsw.model.cards.actiontoken.DoubleMoveToken;
import it.polimi.ingsw.model.cards.actiontoken.MoveShuffleToken;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public abstract class ClientController {

    private NubPlayer currPlayer;
    protected int lorenzoPos = 0;
    private Market market;
    protected NubPlayer player;
    protected MessageDispatcher messageToServerHandler;
    private int count = 0;
    protected int countPope = 0;
    protected boolean localGame = false;
    protected int prodCardCounter = 0;

    private List<ConcreteProductionCard> allProductionCards;
    private List<NubPlayer> totalPlayers = new ArrayList<>();
    private final List<LeaderCard> allLeaders = new ArrayList<>();
    private final List<ActionToken> allToken = new ArrayList<>();
    protected List<ConcreteProductionCard> availableProductionCard = new ArrayList<>();
    protected List<NubPlayer> otherPlayers = new ArrayList<>();
    protected List<BiElement<Resource, Storage>> storeRes;
    protected List<Integer> popeStatusGeneral = new ArrayList<>();

    private MessageID lastRegistrationMessage;
    private MessageID lastGameMessage;

    private boolean waitingServerUpdate = false;
    private boolean registrationPhase = true;
    private boolean gameOver = false;
    private boolean active = true;
    protected boolean normalTurn;

    private static final String prodPath = "/json/ProductionCards.json",
            leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
            leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
            leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
            leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
            tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
            tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
            tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";


    // SETTER & GETTER
    protected MessageToServerHandler getMessageToServerHandler(){return (MessageToServerHandler) messageToServerHandler;}

    public MessageID getLastRegistrationMessage() {return lastRegistrationMessage;}
    public void setLastRegistrationMessage(MessageID lastRegistrationMessage) {this.lastRegistrationMessage = lastRegistrationMessage;}

    public MessageID getLastGameMessage() {return lastGameMessage;}
    public void setLastGameMessage(MessageID lastGameMessage) {this.lastGameMessage = lastGameMessage;}

    public boolean isWaitingServerUpdate() {return waitingServerUpdate;}
    public synchronized void setWaitingServerUpdate(boolean waitingServerUpdate) {this.waitingServerUpdate = waitingServerUpdate;}

    public synchronized boolean isRegistrationPhase() {return registrationPhase;}
    public void setRegistrationPhase(boolean registrationPhase) {this.registrationPhase = registrationPhase; }

    public boolean isGameOver() {return gameOver;}
    public void setGameOver(boolean gameOver) {this.gameOver = gameOver;}

    public synchronized boolean isActive() {return active;}
    public synchronized void setActive(boolean active) {this.active = active;}

    public synchronized void setClosedConnection(String s) {
        gameOver = true;
        displayMessage(s);
    }

    protected void connectionError() {setActive(false);}

    public List<NubPlayer> getOtherPlayers(){return otherPlayers;}
    public void setOtherPlayers(List<NubPlayer> otherPlayers) {this.otherPlayers = otherPlayers;}

    public List<NubPlayer> getTotalPlayers() {return totalPlayers;}
    public void setTotalPlayers(List<NubPlayer> totalPlayers) {this.totalPlayers = totalPlayers;}

    public NubPlayer getPlayer(){return player;}
    public void setPlayer(NubPlayer player) {this.player = player;}

    public Market getMarket() {return market;}
    public void setMarket(Resource[][] market, Resource extra){ this.market = new Market(market,extra);}

    public void addPlayers(NubPlayer player){this.otherPlayers.add(player);}

    public NubPlayer getCurrPlayer() {return currPlayer;}
    public void setCurrPlayer(NubPlayer currPlayer) {this.currPlayer = currPlayer;}

    public boolean isLocalGame(){return localGame;}


    // SETUP PHASE -----------------------------------------------------------------------------------------------------

    public abstract boolean setup() throws IOException;
    public abstract void askNickname();
    public abstract void askNumberOfPlayers();
    public abstract void startGame();

    public abstract void displayWaitMessage();

    /**
     * Initiate all the player and put them in a list that contains all the players in that game
     * @param msg message containing all the player information
     */
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

        totalPlayers.sort(NubPlayer.getComparator());
    }

    /**
     * Set the leaders that a player can choose
     * @param leaders
     */
    public void setLeaderAvailable(String leaders){
        player.setLeaders(new ArrayList<>(convertIdToLeaderCard(convertStringToListInteger(leaders))));

        chooseLeadersAction();
    }

    /**
     * Create and set the player and do {@code initAllCards()}
     * @param nickName the nickname of the player
     */
    public void confirmRegistration(String nickName){
        player = new NubPlayer(nickName);
        totalPlayers.add(player);
        initAllCards();
    }

    public abstract void startLocalGame();

    // ERROR MESSAGE ACTIONS -------------------------------------------------------------------------------------------

    public abstract void cardNotAvailable();
    public abstract void badProductionRequest();
    public abstract void badRearrangeRequest();
    public abstract void badPaymentRequest();
    public abstract void badDimensionRequest();
    public abstract void wrongStackRequest();
    public abstract void wrongLevelRequest();
    public abstract void badStorageRequest();
    public abstract void leaderNotActivable();
    public abstract boolean ackConfirmed (String msg);
    public abstract void displayMessage(String str);
    public abstract void nickError();

    //---------------------------------ACTIONS: behaviours caused by Messages--------------------------

    public abstract void chooseStorageAfterMarketAction(String s);
    public abstract void chooseResourceAction(int quantity);
    public abstract void chooseStorageAction(List<Resource> s);
    public abstract void chooseLeadersAction();
    public abstract void updateMarket();
    public abstract void updateAvailableProductionCards();
    public abstract void showCurrentTurn(String s);
    public abstract void startTurnPhase();
    public abstract void buyFromMarket();
    public abstract void activateLeader();
    public abstract void updatePositionAction(PlayersPositionMessage msg);
    public abstract void showBoard();
    public abstract void rearrangeWarehouse();
    public abstract void showLorenzoStatus(ActionToken act);
    public abstract void startGameNotEndTurn();


    /**
     * Move Lorenzo on the faith track
     */
    public void moveLorenzo(int currentPosition){
        lorenzoPos += currentPosition;
    }

    /**
     * Show other players what the last player has done in his/her turn once it has been set on the corresponding
     * {@link NubPlayer} object.
     * @param player the player who played last turn
     */
    public abstract void updateOtherPlayer(NubPlayer player);


    public synchronized void updateAction(UpdateMessage msg){
        if(market==null){
            market = new Market(msg.getMarketBoard(), msg.getExtraMarble());
        }else {
            market.setMarketBoard(msg.getMarketBoard());
            market.setExtra(msg.getExtraMarble());
        }

        if(msg.getAvailableProductionCards() != null || !msg.getAvailableProductionCards().isEmpty())
            availableProductionCard = convertIdToProductionCard(msg.getAvailableProductionCards());


        ConcreteProductionCard boughtProductionCard = null;
        if(msg.getProductionCardId() != null)
            boughtProductionCard = convertIdToProductionCard(msg.getProductionCardId().getFirstValue());

        for (NubPlayer pp : totalPlayers) {
            //update last player state for this client
            if (pp.getTurnNumber() == msg.getPlayerId()) { //search player
                pp.setCurrPos(msg.getPlayerPos());
                if(pp.equals(player)){
                    //updateFaithTrack();
                    //System.out.println("You're now in position " + player.getCurrPos()); //for debugging
                }

                if (boughtProductionCard != null) {
                    pp.addProductionCard(boughtProductionCard, msg.getProductionCardId().getSecondValue() - 1);
                    prodCardCounter++;
                }

                if (msg.getLeadersId() != null && !msg.getLeadersId().isEmpty())
                    pp.setLeaders(convertIdToLeaderCard(msg.getLeadersId()));

                Map<BiElement<Resource, Storage>, Integer> resources = msg.getAddedResources();

                if (resources != null && resources.size()>0) {
                    resources.forEach(pp::addResources);
                }


                resources = msg.getRemovedResources();

                if (resources != null && resources.size()>0) {
                    resources.forEach(pp::removeResources);
                }
                updateOtherPlayer(pp);
            }

            if(pp.getTurnNumber() == msg.getNextPlayerId()){
                pp.setMyTurn(true);
                currPlayer = pp;
            }else{
                pp.setMyTurn(false);
            }

        }
        updateMarket();
        updateAvailableProductionCards();

        if(isRegistrationPhase() && totalPlayers.size() == 1) {
            startGame();
        }
        else
            displayWaitMessage();

        count++;

        /*if (player.getTurnNumber() == 1) {
            if (count == totalPlayers.size()) {
                registrationPhase = false;
            }
        }
        else {
            if (count == totalPlayers.size() - 1)
                registrationPhase = false;
        }*/
    }

    /**
     * If an action is performed and the turn is not finished continue the turn
     * @param basicActionDone if it's true show only the minor actions a player can do
     */
    public void continueTurn(Boolean basicActionDone){
        if (!registrationPhase){
            normalTurn = !basicActionDone;
            startTurnPhase();
        }
    }

    public void startInitialGame(){
        startGame();
    }

    /**
     * Manage the end turn phase. If it's your turn let the player play.
     * @param msg message from the server with all the player information
     */
    public synchronized void endTurn(EndTurnMessage msg){

        this.currPlayer = totalPlayers.get(msg.getNextPlayerId()-1);

        normalTurn=true;

        for(NubPlayer pp : totalPlayers){
            if(pp.getTurnNumber() == msg.getNextPlayerId()){
                pp.setMyTurn(true);
                if(pp.equals(player)){
                    startTurnPhase();
                }
            }else{
                pp.setMyTurn(false);
                if(pp.equals(player)){
                    showCurrentTurn(currPlayer.getNickname());
                }
            }
        }
    }

    /**
     * In case of a player's disconnection, set the player which {@code turnNumber} matches with {@code nextPlayerId}.
     * If the latter is the one to whom the client app belongs, then start the regular turn. But if he/she is already
     * in the middle of the turn, continue without restarting it.
     * @param nextPlayerId id of the player which has to play the current turn
     */
    public void playerCrashed(int nextPlayerId){
        for(NubPlayer p : totalPlayers){
            if(p.getTurnNumber() == nextPlayerId){
                boolean oldStatus = p.isMyTurn();
                p.setMyTurn(true);
                currPlayer = p;
                if(p.equals(player) && !oldStatus){
                    normalTurn = true;
                    startTurnPhase();
                }
            }else{
                p.setMyTurn(false);
                if(p.equals(player)){
                    showCurrentTurn(currPlayer.getNickname());
                }
            }
        }
    }

    /**
     * Manage the vatican report triggered by the server
     * @param msg message from the server with all the information
     */
    public void infoVaticanReport(VaticanReportMessage msg){

        countPope++;

        int reportId = msg.getReportId();
        List<BiElement<Integer, Boolean>> playersPopeStatus = msg.getAllPlayerPopeFavorStatus();
        for(BiElement<Integer,Boolean> bi : playersPopeStatus){
            for(NubPlayer p : totalPlayers){
                if(p.getTurnNumber()== bi.getFirstValue() && bi.getSecondValue()){
                    p.activatePopePass(reportId);
                    break;
                }
            }
        }

        int i = msg.getReportId();

        for(NubPlayer p : totalPlayers){
            for (BiElement<Integer, Boolean> bi : msg.getAllPlayerPopeFavorStatus()){
                if(bi.getFirstValue() == p.getTurnNumber())
                    p.getPopePasses()[i-1] = bi.getSecondValue();
            }
        }

        if(player.getPopePasses()[i-1])
            popeStatusGeneral.add(0);
        else if (countPope == i)
            popeStatusGeneral.add(1);
        else
            popeStatusGeneral.add(2);

    }

    public void activateLeader(int lID){
        for (LeaderCard led : player.getLeaders()) {
            if (led.getId() == lID)
                led.setStatus(true);
        }
    }

    /**
     * Trigger the end turn when a player want to pass its turn
     */
    public void passTurn(){
        if (!normalTurn)
            messageToServerHandler.generateEnvelope(MessageID.END_TURN, "");
        else
            startGameNotEndTurn();
    }

    /**
     * Update Lorenzo situation and show the token that he has drawn
     * @param msg message from the server with all the Lorenzo's information
     */
    public void upLorenzoToken (String msg){

        List<Integer> num = convertStringToListInteger(msg);
        ActionToken act = convertIdToActionToken(num.get(0));

        lorenzoPos = num.get(1);

        showLorenzoStatus(act);
    }

    public void abortGame(){
        winner("None");
    }

    protected abstract void winner(String winner);

    //---------------------------CONVERTERS---------------------------------

    public List<ConcreteProductionCard> convertIdToProductionCard(List<Integer> ids){
        return allProductionCards.stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
    }
    public ConcreteProductionCard convertIdToProductionCard(int id){
        for(ConcreteProductionCard pc : allProductionCards){
            if(pc.getId()==id)
                return pc;
        }
        return null;
    }

    public List<LeaderCard> convertIdToLeaderCard(List<Integer> ids){

        List <LeaderCard> leaders = (allLeaders.stream().filter(x -> ids.contains(x.getId()))
                                                        .collect(Collectors.toList()));

        return leaders;

    }

    protected ActionToken convertIdToActionToken(int id){
        for (ActionToken a : allToken)
            if (a.getId() == id)
                return a;

        return null;

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

    /**
     * Set all the card reading the information from JSON files
     */
    private void initAllCards() {
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

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(tokenDiscardPath)));
        allToken.addAll(gson.fromJson(reader, new TypeToken<List<DiscardToken>>(){}.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(tokenDoubleMovePath)));
        allToken.addAll(gson.fromJson(reader, new TypeToken<List<DoubleMoveToken>>(){}.getType()));

        reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(tokenMoveShufflePath)));
        allToken.addAll(gson.fromJson(reader, new TypeToken<List<MoveShuffleToken>>(){}.getType()));
    }
}


