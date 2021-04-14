package it.polimi.ingsw.model;


import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.PopePass;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MultiplayerGame extends Game implements Observer {
    private List<ProPlayer> players;
    private List<ProPlayer> activePlayers;
    private int totalPlayers;
    private ProPlayer currPlayer;

    public MultiplayerGame(String prodFileName, String leadFileName){
        players = new ArrayList<>();
        market = new Market();
        totalPlayers = 0;
        currPlayer = null;
        winner = null;
        activePlayers = new ArrayList<>();

        leaderDeck = new Deck("LeadCard");
        //productionDecks;
    }

    public MultiplayerGame(){
        players = new ArrayList<>();
        market = new Market();
        totalPlayers = 0;
        currPlayer = null;
        winner = null;
        activePlayers = new ArrayList<>();

        leaderDeck = new Deck("LeadCard");
        //productionDecks;
    }

    /*
    1 creare il player
    2 distribuire le carte leader
    4 inizializzare prodcard
    5 mercato, gia fatto
     */

    public void start(String nick, int numPlayers){

        List<Card> tempList;

        //distribuisci leader
        for (int i = 0; i < numPlayers; i++) {
            tempList = new ArrayList<>();
            for (int j = 0; j < 4; j++) {
                tempList.add(leaderDeck.getFirst());
            }
            //players.get(i).gaveLeaderOption(tempList);
        }
        currPlayer = activePlayers.get(0);

    }

    public Player getWinner(){
        return winner;
    }

    public void createPlayer(String nickname){
        List<String> nicknames = players.stream()
                .map(Player :: getNickname)
                .collect(Collectors.toList());
        if(!nicknames.isEmpty() && nicknames.contains(nickname)){
            throw new IllegalArgumentException("Already exists a player with "+ nickname + "as nickname");
        }

        totalPlayers++;
        ProPlayer p = new ProPlayer(nickname, totalPlayers, this);
        players.add(p);

        switch(p.getTurnID()){
            case 2:
                p.chooseResource();
                break;
            case 3:
                p.moveOnBoard(1);
                p.chooseResource();
                break;
            case 4:
                p.moveOnBoard(1);
                p.chooseResource();
                p.chooseResource();
                break;
        }

        p.registerObserver(this);
        activePlayers.add(p);
    }

    public List<ProPlayer> getPlayers() {
        return players;
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
            //next active player found: give him the currPlayer title
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

    /**When {@player} reaches the end of the faith track or buys their 7th production card, call this method to finalize the game.
     * It will let all the players on the {@player}'s left side play their last turn. After that, the winner will be chosen
     * based on how many Victory Points they've cumulated during the whole game. Sometimes a draw occurs: if this
     * is the case, then the winner will be the one with more resources between the max-points-players.
     * @param player First player to reach the end of the track or to buy their 7th production card*/
    public void updateEnd(Player player){
        int currID = ((ProPlayer)player).getTurnID();
        if(currID!=totalPlayers){
            currID++;
            //method to let all the left players play their last turn
        }
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
            return;
        }
        //draw: the winner is the one with more resources between the candidates
        int maxRes = 0;
        for(ProPlayer p : possibleWinners){
            int res = p.countAllResources();
            if(res>maxRes){
                maxRes = res;
                winner = p;
            }
        }
    }
    public void updatePosition(Player player){
        //call controller to show the change on the view
    }

    /**Everytime a player triggers a Vatican Report by moving on the personal faith track, this method will inform the game
     * that will act accordingly. It will activate the {@code player}'s PopePass.
     * <p>Furthermore, by calling this method all the other players' positions will be checked, even inactive players.
     * Accordingly to position ranges set by rules, their relative PopePass either will be activated or discarded.</p>
     * <p>For reference:
     * <li>Must be in at least position 5 for 1st report;</li>
     * <li>Must be in at least position 12 for 2nd report;</li>
     * <li>Must be in at least position 19 for 2rd report.</li></p>
     * @param player player who triggered Vatican Report
     * @param vaticanReport number in range 1-3 that specifies which vatican report is triggered*/
    public void alertVaticanReport(Player player, int vaticanReport){
        //check every player position (even inactive ones in case they rejoin)
        for(ProPlayer p : players){
            ArrayList<PopePass> passes = p.getPopePasses();
            if(p.isInVaticanReportRange(vaticanReport)){
                passes.get(vaticanReport-1).activate();
            }else{
                passes.get(vaticanReport-1).disable();
            }
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
    }
}

