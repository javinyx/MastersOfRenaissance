package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Game implements Observer{
    protected Market market;
    protected Player winner;
    protected List<Deck> productionDecks;
    protected Deck leaderDeck;

    public abstract void start();
    public abstract void createPlayer(String nickname);
    public Market getMarket(){
        return market;
    }
    /**Returns the list of every ProductionCard still in Production Deck */
    public List<ProductionCard> getProductionDecks(){
        List<Card> availableCards = new ArrayList<>();
        for(Deck d : productionDecks){
            availableCards.addAll(d.getCards());
        }
        return availableCards.stream()
                .map(x -> (ProductionCard)x)
                .collect(Collectors.toList());
    }
    /**Returns the list of ProductionCards that can be bought from each Production Deck*/
    public List<ProductionCard> getBuyableProductionCards(){
        List<Card> availableCards = new ArrayList<>();
        for(Deck d : productionDecks){
            availableCards.add(d.getCard(0));
        }
        return availableCards.stream()
                .map(x -> (ProductionCard)x)
                .collect(Collectors.toList());
    }

    /**@return list of every LeaderCard still in Leader Deck */
    public List<LeaderCard> getLeaderDeck(){
        return leaderDeck.getCards().stream()
                .map(x -> (LeaderCard)x)
                .collect(Collectors.toList());
    }

    //public abstract List<LeaderCard> distributeLeaders();
    /**Remove the specified ProductionCard from Production Deck.
     * @param card the one to be removed */
    public void removeFromProdDeck(ProductionCard card){

    }
    public Player getWinner(){return winner;}
}
