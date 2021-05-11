package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.client.model.Market;
import it.polimi.ingsw.client.model.NubPlayer;
import it.polimi.ingsw.messages.concreteMessages.ChooseLeaderCardsMessage;
import it.polimi.ingsw.messages.concreteMessages.ChoosePlacementsInStorageMessage;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Resource;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClientController {
    private ViewInterface view;
    private boolean myTurn;
    private List<ConcreteProductionCard> allProductionCards;
    private List<LeaderCard> allLeaders;
    private List<LeaderCard> leaders = new ArrayList<>();
    private List<ConcreteProductionCard> availableProductionCard = new ArrayList<>();
    private List<NubPlayer> otherPlayers = new ArrayList<>();
    private NubPlayer player;
    private Market market;

    private static final String prodPath = "/json/ProductionCards.json",
            leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
            leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
            leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
            leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
            tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
            tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
            tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";

    public ClientController(ViewInterface view, NubPlayer player){
        this.view = view;
        this.player = player;
        initAllCards();
    }

    private void initAllCards(){
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(prodPath)));
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

    //---------------------------------ACTIONS: behaviours caused by Messages--------------------------
    public void chooseResourceAction(int quantity) {
        view.showMessage("Choose no." + quantity + " resources");
    }

    public void chooseStorageAction(ChoosePlacementsInStorageMessage msg){
        view.showMessage("Choose a storage for each of the following resources: " + msg.getResources());
    }

    public void chooseLeadersAction(ChooseLeaderCardsMessage msg){
        view.showMessage("Choose no." + msg.getQuantity() + " of leaders among these:\n" + msg.getLeaders());
    }
    public void errorAction(String str){
        view.showMessage(str);
    }

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
            if(pp.getTurnID()==msg.getPlayerId()){
                pp.setCurrPos(msg.getPlayerPos());
                pp.addProductionCard(boughtProductionCard, msg.getProductionCardId().getV()-1);
                pp.setLeaders(convertIdToLeaderCard(msg.getLeadersId()));
                pp.setAllResources(msg.getResources());
                view.updateOtherPlayer(pp);
                break;
            }
        }

    }

    //---------------------------CONVERTERS---------------------------------
    private List<ConcreteProductionCard> convertIdToProductionCard(List<Integer> ids){
        return allProductionCards.stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
    }
    private ConcreteProductionCard convertIdToProductionCard(int id){
        for(ConcreteProductionCard pc : allProductionCards){
            if(pc.getId()==id)
                return pc;
        }
        return null;
    }

    private List<LeaderCard> convertIdToLeaderCard(List<Integer> ids){
        return allLeaders.stream().filter(x -> ids.contains(x.getId())).collect(Collectors.toList());
    }



}
