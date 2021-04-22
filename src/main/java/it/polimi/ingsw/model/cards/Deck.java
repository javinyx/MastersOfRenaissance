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

/**
 * The Deck contains a list of Cards ({@code cardList}) that is only used for internal operations and a Deque of Cards ({@code cardDeque})
 * which is used for calls from the outside.
 */
public class Deck {
    private ArrayList<Card> cardList;
    private ArrayDeque<Card> cardDeque;
    private static final String prodPath = "/json/ProductionCards.json",
                                leadDiscountPath = "/json/LeaderCards/DiscountAbilityCards.json",
                                leadStoragePath = "/json/LeaderCards/StorageAbilityCards.json",
                                leadMarblePath = "/json/LeaderCards/MarbleAbilityCards.json",
                                leadBoostPath = "/json/LeaderCards/BoostAbilityCards.json",
                                tokenDiscardPath = "/json/ActionTokens/DiscardTokens.json",
                                tokenDoubleMovePath = "/json/ActionTokens/DoubleMoveTokens.json",
                                tokenMoveShufflePath = "/json/ActionTokens/MoveShuffleTokens.json";

    /**
     * Instantiate a new Deck of the given type, it uses the default paths for the .json files found in /resources/json.
     *
     * @param deckType "LeaderCard" for a Leader Deck, "ActionToken" for a Token Deck
     */
    public Deck(String deckType){
        switch (deckType) {
            case "LeaderCard" -> {
               createLeadDeck();
            }
            case "ActionToken" -> {
                createTokenDeck();
            }
            default -> {
                cardDeque = new ArrayDeque<>();
                cardList = null;
                return;
            }
        }
        // Convert ArrayList to ArrayDeque
        cardDeque = new ArrayDeque<>(cardList);
        cardList = null;
    }

    /**
     * Instantiate a new Deck with no parameters, used for ConcreteProductionCards.
     */
    public Deck(){
        cardDeque = new ArrayDeque<>();
        cardList = new ArrayList<>();
    }

    // Generate a LeaderCard Deck
    private void createLeadDeck(){
        Gson gson = new Gson();

        // Read all the leader ability cards and add them to the cardList
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(leadDiscountPath)))) {
            cardList = gson.fromJson(reader, new TypeToken<ArrayList<DiscountAbility>>(){}.getType());
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
            cardList = gson.fromJson(reader, new TypeToken<ArrayList<DiscardToken>>(){}.getType());
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

    private static Deck createMiniDeck(ArrayList<Card> currentCardList){
        ArrayList<Card> clonedCardList = new ArrayList<>(currentCardList);
        Collections.shuffle(clonedCardList);

        Deck miniDeck = new Deck();
        miniDeck.cardDeque = new ArrayDeque<>(clonedCardList);

        return miniDeck;
    }

    // ******************************** PUBLIC METHODS ********************************

    /**
     * Get the deque of cards inside a deck.
     * @return the deque of cards inside a single deck
     */
    public Deque<Card> getCards(){
        return cardDeque;
    }

    /**
     * Peek the first card of the chosen deck, without removing it.
     * @return the first card of the deck, or null if the deck is empty
     */
    public Card peekFirst() {
        return cardDeque.peekFirst();
    }

    /**
     * Get the first card of the chosen deck and remove it.
     * @return the first card of the deck, or null if the deck is empty
     */
    public Card getFirst(){
        return cardDeque.pollFirst();
    }

    public boolean remove(Card card){return this.cardDeque.remove(card);}
    public boolean contains(Card card){return this.cardDeque.contains(card);}

    /**
     * Create the list that contains all the ProductionCard Decks, 12 in total.
     * @return the list that contains all the small decks
     */
    public static List<Deck> createProdDeckList(){
        List<Card> allProdCards;
        List<Deck> prodDecks = new ArrayList<>();
        Gson gson = new Gson();

        // Read all the production cards and add them to the cardList
        try(Reader reader = new InputStreamReader(Objects.requireNonNull(MastersOfRenaissance.class.getResourceAsStream(prodPath)))) {
            allProdCards = gson.fromJson(reader, new TypeToken<ArrayList<ConcreteProductionCard>>(){}.getType());

            for (int i = 0; i <= allProdCards.size() - 4; i = i + 4) {
                ArrayList<Card> currentCardList = new ArrayList<>(allProdCards.subList(i, i + 4));
                Deck newDeck = createMiniDeck(currentCardList);
                prodDecks.add(newDeck);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        return prodDecks;
    }

    public int size(){
        return cardDeque.size();
    }

}