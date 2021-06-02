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
    private Market market;
    protected NubPlayer player;
    protected MessageToServerHandler messageToServerHandler;
    private int count = 0;

    private List<ConcreteProductionCard> allProductionCards;
    private List<LeaderCard> allLeaders = new ArrayList<>();
    private List<NubPlayer> totalPlayers = new ArrayList<>();
    protected List<ConcreteProductionCard> availableProductionCard = new ArrayList<>();
    protected List<NubPlayer> otherPlayers = new ArrayList<>();
    protected List<BiElement<Resource, Storage>> storeRes;

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
    protected MessageToServerHandler getMessageToServerHandler(){return messageToServerHandler;}

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


    // SETUP PHASE -----------------------------------------------------------------------------------------------------

    public abstract boolean setup() throws IOException;
    public abstract void startLocalGame();
    public abstract void askNickname();
    public abstract void askNumberOfPlayers();
    public abstract void startGame();

    public abstract void refreshView();

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

    public void setLeaderAvailable(String leaders){
        player.setLeaders(new ArrayList<>(convertIdToLeaderCard(convertStringToListInteger(leaders))));

        chooseLeadersAction();
    }

    public void confirmRegistration(String nickName){
        player = new NubPlayer(nickName);
        totalPlayers.add(player);
        initAllCards();
    }

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

    /**Move Lorenzo on the faith track*/
    public abstract void moveLorenzo(int currentPosition);

    /**Show other players what the last player has done in his/her turn once it has been set on the corresponding
     * {@link NubPlayer} object.
     * @param player the player who played last turn*/
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

        //modifica: da for(NubPlayer pp: otherPLayers) a for(NubPlayer pp: totalPlayers)
        for (NubPlayer pp : totalPlayers) {
            //update last player state for this client
            if (pp.getTurnNumber() == msg.getPlayerId()) { //search player
                pp.setCurrPos(msg.getPlayerPos());
                if(pp.equals(player)){
                    //updateFaithTrack();
                    System.out.println("You're now in position " + player.getCurrPos()); //for debugging
                }
                if (boughtProductionCard != null)
                    pp.addProductionCard(boughtProductionCard, msg.getProductionCardId().getSecondValue() - 1);

                if (msg.getLeadersId() != null && !msg.getLeadersId().isEmpty())
                    pp.setLeaders(convertIdToLeaderCard(msg.getLeadersId()));

                Map<BiElement<Resource, Storage>, Integer> resources = msg.getAddedResources();

                if (resources != null && resources.size()>0) {
                    resources.forEach(pp::addResources);
                }

                System.out.println(pp.getAllResources());
                System.out.println(resources);

                resources = msg.getRemovedResources();

                System.out.println(pp.getAllResources());
                System.out.println(resources);

                if (resources != null && resources.size()>0) {
                    resources.forEach(pp::removeResources);
                }

                if(pp.equals(player)){
                    refreshView();
                }
                updateOtherPlayer(pp);
                break;
            }
        }
        updateMarket();
        updateAvailableProductionCards();

        for(NubPlayer p : totalPlayers){
            if(p.getTurnNumber() == msg.getNextPlayerId())
                currPlayer = p;
        }

        if(!isRegistrationPhase() || totalPlayers.size() == 1)
            startGame();

        count++;

        if (player.getTurnNumber() == 1) {
            if (count == totalPlayers.size()) {
                registrationPhase = false;
            }
        }
        else {
            if (count == totalPlayers.size() - 1)
                registrationPhase = false;
        }
    }


    public void continueTurn(Boolean basicActionDone){
        if (!registrationPhase){
            System.out.println(basicActionDone);
            normalTurn = !basicActionDone;
            startTurnPhase();
        }
    }

    public void startInitialGame(){
        startGame();
    }

    public synchronized void endTurn(EndTurnMessage msg){

        for (BiElement<Resource, Storage> elem : storeRes)
            player.addResources(elem, 1);
        storeRes.clear();

        player.setMyTurn(false);

        this.currPlayer = totalPlayers.get(msg.getNextPlayerId()-1);
        this.availableProductionCard = convertIdToProductionCard(msg.getBuyableProdCardsIds());

        showBoard();

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

    public void infoVaticanReport(VaticanReportMessage msg){
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
        //TODO: show on UI who caused the vatican report and the player (client owner) his/her status
    }

    public void passTurn(){
        messageToServerHandler.generateEnvelope(MessageID.END_TURN, "");
    }

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
    }
}


