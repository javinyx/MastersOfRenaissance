package it.polimi.ingsw.messages;

public enum MessageID {

    DISPLAY_LEADER_CARD, // mostra al giocatore i 4 leader tra cui scegliere, quindi dovrà sceglierne 2

    LEADER_ACTIVATED,

    BAD_STORAGE_REQUEST, // quando sbaglia a scegliere la posizione della risorsa nella warehouse o leader, oppure se richiede di metterla nella lootchest

     //quando viene attivata la produzione ma nel model viene sollevata l'eccezione di BadStorageException e quindi non ha completato la richiesta

    TURN_OVER,

    PLAYER_LOSE,

    //GENERIC ACKNOWLEDGMENT
    ACK,
    END_TURN,
    CONFIRM_END_TURN,
    CONFIRM_REGISTRATION,

    //PLAYERS REGISTRATION
    ADD_PLAYER_REQUEST,
    CHOOSE_LEADER_CARDS,
    TOO_MANY_PLAYERS,
    SURRENDER,
    START_INITIAL_GAME,
    REARRANGE_WAREHOUSE,

    // GAME INITIALIZATION
    REGISTER_SINGLE,
    REGISTER_MULTI,
    ABORT_GAME,
    PLAYER_WIN,
    TURN_NUMBER,
    GAME_READY,

    // ERRORS / EXCEPTIONS -> grouping them into INFO_MESSAGE

    BAD_DIMENSION_REQUEST,
    BAD_PRODUCTION_REQUEST,
    BAD_PAYMENT_REQUEST,
    WRONG_STACK_CHOICE,
    WRONG_LEVEL_REQUEST,
    WRONG_PLAYER_REQUEST,
    LEADER_NOT_ACTIVABLE,
    BAD_REARRANGE_REQUEST,

    INFO,
    PING,
    PONG,

    CARD_NOT_AVAILABLE,

    // TURNS UTILS
    LORENZO_POSITION,
    PLAYERS_POSITION,
    VATICAN_REPORT,
    STORE_RESOURCES,    // ORGANIZZAZIONE RISORSE
    ACTIVATE_LEADER,    // ACTIVE LEADER CARD
    DISCARD_LEADER,

    CHOOSE_RESOURCE,
    TURN_SETUP,



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

    UPDATE //to inform other players of the updates in the model

    }
