package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.Controller;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.leader.LeaderCard;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;
import it.polimi.ingsw.model.market.Market;
import it.polimi.ingsw.model.player.Player;
import it.polimi.ingsw.model.player.ProPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public abstract class Game implements ModelObserver {
    protected Market market;
    protected Player winner;
    protected List<Deck> productionDecks;
    protected Deck leaderDeck;
    protected int totalPlayers;
    protected ProPlayer currPlayer;
    protected Controller controller; // bisogna farselo passare dal Controller in fase di costruzione di SinglePlayerGame o Multi

    public abstract void start(int numPlayers);
    public abstract boolean createPlayer(String nickname);

    //------------------GETTERS------------------
    public Controller getControllerObserver(){return controller;}
    public Market getMarket(){
        return market;
    }

    /**Returns the list of every ProductionCard still in Production Deck */
    public List<ConcreteProductionCard> getAllProductionDecks(){
        List<Card> availableCards = new ArrayList<>();
        for(Deck d : productionDecks){
            availableCards.addAll(d.getCards());
        }
        return availableCards.stream()
                .map(x -> (ConcreteProductionCard)x)
                .collect(Collectors.toList());
    }

    /**@return one of the 12 production deck. {@code numDeck} is an index: between 0 and 11.
     * It can return an empty deck, if that deck has already been used up.*/
    public Deck getProductionDeck(int numDeck){
        if(numDeck<0 || numDeck>=12){
            return null;
        }
        return productionDecks.get(numDeck);
    }

    /**Returns the list of ProductionCards that can be bought from each Production Deck*/
    public List<ConcreteProductionCard> getBuyableProductionCards(){
        List<Card> availableCards = new ArrayList<>();
        for(Deck d : productionDecks){
            if(d.size()>0) //if deck is empty (null) that means the deck doesn't exist anymore
                availableCards.add(d.peekFirst());
        }
        return availableCards.stream()
                .map(x -> (ConcreteProductionCard)x)
                .collect(Collectors.toList());
    }

    /**@return list of every LeaderCard still in Leader Deck */
    public List<LeaderCard> getLeaderDeck(){
        return leaderDeck.getCards().stream()
                .map(x -> (LeaderCard)x)
                .collect(Collectors.toList());
    }

    public Deck getLeaderDeckNew(){
        return leaderDeck;
    }

    //public abstract List<LeaderCard> distributeLeaders();
    /**Remove the specified ProductionCard from Production Deck.
     * @param card the one to be removed */
    public boolean removeFromProdDeck(ConcreteProductionCard card){
        List<Deck> decksClone = new ArrayList<>(productionDecks);
        for(Deck d : decksClone){
            if(d.contains(card) && d.size()>1 ){
                d.remove(card);
                return true;
            }else if(d.contains(card)){
                d.remove(card);
                productionDecks.remove(d);
                return true;
            }
        }
        return false;
    }
    public Player getWinner(){return winner;}

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public ProPlayer getCurrPlayer() {
        return currPlayer;
    }
}
