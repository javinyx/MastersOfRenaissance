package it.polimi.ingsw.model;


import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.messages.MessageID;
import it.polimi.ingsw.messages.concreteMessages.UpdateMessage;
import it.polimi.ingsw.misc.BiElement;
import it.polimi.ingsw.misc.Storage;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.market.Resource;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PopePass;
import it.polimi.ingsw.model.player.ProPlayer;
import it.polimi.ingsw.model.player.Warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiPlayerGame extends Game implements ModelObserver {
    protected List<ProPlayer> players;
    protected List<ProPlayer> activePlayers;


    public MultiPlayerGame(Controller controller){
        super(new Market());
        players = new ArrayList<>();
        totalPlayers = 0;
        currPlayer = null;
        winner = null;
        activePlayers = new ArrayList<>();

        leaderDeck = new Deck("LeaderCard");
        productionDecks = Deck.createProdDeckList();
        this.controller = controller;
    }


    public Player getWinner(){
        return winner;
    }

    /**Create the player with also the initial extra resources.
     * @param nickname the nickname of the player*/
    public boolean createPlayer(String nickname){
        totalPlayers++;
        ProPlayer p = new ProPlayer(nickname, totalPlayers, this);
        players.add(p);
        activePlayers.add(p);
        p.registerObserver(this);
        return true;
    }

    public ProPlayer getPlayerFromNickname(String nickname){
        for(ProPlayer p : activePlayers){
            if(p.getNickname().equals(nickname))
                return p;
        }
        return null;
    }

    /**Let the player choose an extra resource to add during initialization phase.*/
    public void chooseResource(){
        //wait for the player to choose a resource
        //then add to warehouse
    }

    public void start(ProPlayer p){

        currPlayer = activePlayers.get(0);

        Map<BiElement<Resource, Storage>, Integer> addRes = new HashMap<>();

        Warehouse war = p.getWarehouse();


        if (p.getWarehouse().getSmallInventory() != null)
            addRes.put(new BiElement<>(war.getSmallInventory(), Storage.WAREHOUSE_SMALL), 1);
        if (p.getWarehouse().getMidInventory().size() != 0) {
            for (Resource r : war.getMidInventory())
                addRes.put(new BiElement<>(r, Storage.WAREHOUSE_MID), 1);
        }
        if (p.getWarehouse().getLargeInventory().size() != 0) {
            for (Resource r : war.getLargeInventory())
                addRes.put(new BiElement<>(r, Storage.WAREHOUSE_LARGE), 1);
        }

        List<BiElement<Integer,Boolean>> leaders = new ArrayList<>();
        for(LeaderCard l : p.getLeaderCards()){
            leaders.add(new BiElement<>(l.getId(), l.isActive()));
        }

        UpdateMessage msg = new UpdateMessage(p.getTurnID(), p.getCurrentPosition(), 1,
                getMarket().getMarketBoard(), getMarket().getExtraMarble(), getBuyableProductionID(),
                null, leaders, addRes, null);


        p.setUpdate(msg);

    }

    public List<ProPlayer> getPlayers() {
        return players;
    }
    public List<ProPlayer> getActivePlayers(){return activePlayers;}


    /**
     * Remove the player from active players pool.
     * @param playerToRemove player that has to be removed from the active pool
     */
    public void removeFromActivePlayers(ProPlayer playerToRemove){
        activePlayers.remove(playerToRemove);
    }

    public Market getMarket(){
        return market;
    }

    /**Once the current player has finished their turn, the game will give the turn to the next one between those
     * active at the moment. */
    public void updateEndTurn(ProPlayer player){
        int currID = player.getTurnID();
        currID = (currID == totalPlayers) ? 1 : currID+1;
        if(activePlayers.isEmpty()){
            //kill game: nobody is in the lobby right now, we could send the thread to sleep (like for 2 mins)
            //if after that nobody joined back, end the game
            return;
        }
        for(ProPlayer p : activePlayers){
            //next active player found: give him/her the currPlayer title
            if(p.getTurnID() == currID){
                currPlayer = p;
                return;
            }
        }
        //search for the one who should have been playing the next turn, then call the method (recursion)
        for(ProPlayer p : players){
            if(p.getTurnID() == currID){
                updateEndTurn(p);
            }
        }
    }

    /**When {@code player} reaches the end of the faith track or buys their 7th production card, call this method to finalize the game.
     * It will let all the players on the {@code player}'s left side play their last turn. After that, the winner will be chosen
     * based on how many Victory Points they've cumulated during the whole game. Sometimes a draw occurs: if this
     * is the case, then the winner will be the one with more resources between the max-points-players.
     * @param player First player to reach the end of the track or to buy their 7th production card*/
    public void updateEnd(Player player){
        int currID = ((ProPlayer)player).getTurnID();
        if(currID!=totalPlayers){
            //method to let all the left players play their last turn
            controller.initEndPhase(currID);
        }
    }

    public Player countFinalPointsAndWinner(){
        int maxPoints = 0;
        List<ProPlayer> possibleWinners = new ArrayList<>();
        int points;
        for(ProPlayer p : activePlayers){
            points = p.getVictoryPoints();
            if(points > maxPoints){
                possibleWinners = null; //we delete those that might be considered winners
                maxPoints = points;
                possibleWinners.add(p);
            }else if(points == maxPoints){
                possibleWinners.add(p);
            }
        }
        if(possibleWinners.size()==1){
            winner = possibleWinners.get(0);
            return winner;
        }
        //draw: the winner is the one with more resources between the candidates
        int maxRes = 0;
        for(ProPlayer p : possibleWinners){
            int res = p.countAllResources();
            if(res>maxRes){
                maxRes = res;
                winner = p;
                return winner;
            }
        }
        return null;
    }
    public void updatePosition(Player player){
        controller.update(MessageID.PLAYERS_POSITION);
    }

    /**Everytime a player triggers a Vatican Report by moving on the personal faith track, this method will inform the game
     * that will act accordingly. It will activate the {@code player}'s PopePass.
     * <p>Furthermore, by calling this method all the other players' positions will be checked, even inactive players.
     * Accordingly to position ranges set by rules, their relative PopePass either will be activated or discarded.</p>
     * <p>For reference:</p>
     * <ul>
     * <li>Must be in at least position 5 for 1st report;</li>
     * <li>Must be in at least position 12 for 2nd report;</li>
     * <li>Must be in at least position 19 for 2rd report.</li>
     * </ul>
     * @param player player who triggered Vatican Report
     * @param vaticanReport number in range 1-3 that specifies which vatican report is triggered
     * */
    public void alertVaticanReport(Player player, int vaticanReport){
        if(reportTriggered < vaticanReport) {
            triggerPlayerReport = player;
            reportTriggered = vaticanReport;
            //check every player position (even inactive ones in case they rejoin)
            for (ProPlayer p : players) {
                List<PopePass> passes = p.getPopePasses();
                if (p.isInVaticanReportRange(vaticanReport)) {
                    passes.get(vaticanReport - 1).activate();
                } else {
                    passes.get(vaticanReport - 1).disable();
                }
            }
            controller.update(MessageID.VATICAN_REPORT);
        }
    }

    /**Add 1 faith point to each active player (except the discarding one).
     * @param player player discarding a resource */
    public void alertDiscardResource(Player player){
        for(ProPlayer p : activePlayers){
            if(!p.equals(player)){
                p.addFaithPoints(1);
            }
        }
        controller.update(MessageID.PLAYERS_POSITION);
    }

    public void alertDiscardResource(Player player, int quantity){
        for(ProPlayer p : activePlayers){
            if(!p.equals(player)){
                p.addFaithPoints(quantity);
                controller.update(MessageID.PLAYERS_POSITION);
            }
        }
    }

    public ProPlayer getNextPlayer() {
        int turnID = getCurrPlayer().getTurnID();
        turnID++;
        if(activePlayers.contains(activePlayers.get(turnID)))
            return activePlayers.get(turnID);
        return null;
    }
}

