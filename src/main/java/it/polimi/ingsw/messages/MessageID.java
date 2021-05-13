package it.polimi.ingsw.messages;

public enum MessageID {

    DISPLAY_LEADER_CARD, // mostra al giocatore i 4 leader tra cui scegliere, quindi dovrà sceglierne 2

    LEADER_ACTIVATED,





    CHOOSE_TURN_TYPE, // nel senso che quando il giocatore riceve questo messaggio è libero di cliccare su una delle 3 zone: zona mercato, zona produzione, zona acquisto carte produzione, e di conseguenza il server saprà le sue intenzioni



    CHOOSE_PLACEMENTS_IN_STORAGE, // per quando il giocatore compra dal mercato e deve disporre ogni singola risorsa nella warehouse

    BAD_STORAGE_REQUEST, // quando sbaglia a scegliere la posizione della risorsa nella warehouse o leader, oppure se richiede di metterla nella lootchest

     //quando viene attivata la produzione ma nel model viene sollevata l'eccezione di BadStorageException e quindi non ha completato la richiesta

    TURN_OVER,

    CURRENT_PLAYER, //notifica il player che è il suo turno

    PLAYER_LOSE,

    //GENERIC ACKNOWLEDGMENT
    ACK,
    CONFIRM_REGISTRATION,

    //PLAYERS REGISTRATION
    ADD_PLAYER_REQUEST,
    CHOOSE_LEADER_CARDS,
    TOO_MANY_PLAYERS,
    SURRENDER,

    // GAME INITIALIZATION
    REGISTER_SINGLE,
    REGISTER_MULTI,
    ABORT_GAME,
    PLAYER_WIN,

    // ERRORS / EXCEPTIONS -> grouping them into INFO_MESSAGE

    BAD_DIMENSION_REQUEST,
    BAD_PRODUCTION_REQUEST,
    BAD_PAYMENT_REQUEST,
    WRONG_STACK_CHOICE,
    WRONG_LEVEL_REQUEST,
    WRONG_PLAYER_REQUEST,

    INFO,

    CARD_NOT_AVAILABLE,

    // TURNS UTILS
    STORE_RESOURCES,    // ORGANIZZAZIONE RISORSE
    ACTIVATE_LEADER,    // ACTIVE LEADER CARD

    CHOOSE_RESOURCE,


    // BUY PRODUCTION CARD
    BUY_PRODUCTION_CARD,
    OK_BUY_PRODUCTION_CARD,

    // START PRODUCTION
    PRODUCE,
    OK_PRODUCTION,

    // BUY FROM MARKET
    BUY_FROM_MARKET,
    OK_BUY_MARKET,

    // SERVER STUFF
    SERVER_STATE,
    PLAYER_NUM,
    PLAYER_LIST,
    ASK_NICK,
    NICK_ERR,

    UPDATE

    }
