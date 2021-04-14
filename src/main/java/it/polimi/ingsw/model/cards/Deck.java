package it.polimi.ingsw.model.cards;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.MastersOfRenaissance;
import it.polimi.ingsw.model.cards.actiontoken.*;
import it.polimi.ingsw.model.cards.leader.*;
import it.polimi.ingsw.model.cards.production.ConcreteProductionCard;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.io.IOException;

public class Deck {
    private ArrayList<Card> cardList;
    private ArrayDeque<Card> cardDeque;
    private ArrayList<Deck> productionDecks; //non dovrebbe servire
    private static final String prodPath = "/json/ProductionCards.json",
                                leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
                                leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
                                leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
                                leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
                                tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
                                tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
                                tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";

    // Default constructor with only the type of deck as a parameter, uses default paths
    public Deck(String deckType){
        switch (deckType) {
            case "LeaderCard" -> {
               createLeadDeck();
            }
            case "ActionToken" -> {
                createTokenDeck();
            }
            default -> {cardDeque = new ArrayDeque<>(); cardList = null; return;}
        }

        // Convert ArrayList to ArrayDeque
        cardDeque = new ArrayDeque<>(cardList);
        cardList = null;
    }

    public Deck(){
        cardDeque = new ArrayDeque<>();
        cardList = new ArrayList<>();
    }

    // Generate a LeaderCard Deck
    private void createLeadDeck(){
        Gson gson = new Gson();

        // Read all the leader ability cards and add them to the cardList
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadDiscountPath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<DiscountAbility>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadStoragePath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<StorageAbility>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadMarblePath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<MarbleAbility>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadBoostPath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<BoostAbility>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Shuffle the leader cards in a random order
        Collections.shuffle(cardList);
    }

    // Generate an ActionToken Deck
    private void createTokenDeck(){
        Gson gson = new Gson();

        // Read all the action tokens and add them to the cardList
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(tokenDiscardPath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<DiscardToken>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(tokenDoubleMovePath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<DoubleMoveToken>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(tokenMoveShufflePath)))) {
            cardList.addAll(gson.fromJson(reader, new TypeToken<ArrayList<MoveShuffleToken>>(){}.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Shuffle the action tokens in a random order
        Collections.shuffle(cardList);
    }


    // ******************************** PUBLIC METHODS ********************************

    public ArrayDeque<Card> getCards(){
        return cardDeque;
    }

    public Card peekFirst() {
        return cardDeque.peekFirst();
    }

    public Card getFirst(){
        return cardDeque.getFirst();
    }

    public List<Deck> createProdDeckList() throws IOException{
        List<Card> allProdCards;
        List<Deck> deckies = new ArrayList<>();
        Gson gson = new Gson();

        // Read all the production cards and add them to the cardList
        Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(prodPath)));
        allProdCards = gson.fromJson(reader, new TypeToken<ArrayList<ConcreteProductionCard>>(){}.getType());

        for(int i = 0; i <= allProdCards.size()-4; i = i + 4) {
            ArrayList<Card> subList = new ArrayList<>(allProdCards.subList(i, i+4));
            Deck d = shuffle(subList);
            deckies.add(d);
        }
        return deckies;
    }

    private Deck shuffle(ArrayList<Card> subList){
        ArrayList<Card> clone = new ArrayList<>(subList);
        Collections.shuffle(clone);
        Deck miniDeck = new Deck();
        miniDeck.cardDeque = new ArrayDeque<>(subList);
        return miniDeck;
    }
}