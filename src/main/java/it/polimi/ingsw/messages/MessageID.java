package it.polimi.ingsw.messages;

public enum MessageID {

    DISPLAY_LEADER_CARD, // mostra al giocatore i 4 leader tra cui scegliere, quindi dovrà sceglierne 2

    LEADER_ACTIVATED,

    CHOOSE_CARD,

    CHOOSE_RESOURCE,

    CHOOSE_TURN_TYPE, // nel senso che quando il giocatore riceve questo messaggio è libero di cliccare su una delle 3 zone: zona mercato, zona produzione, zona acquisto carte produzione, e di conseguenza il server saprà le sue intenzioni

    CARD_NOT_AVAILABLE,

    CHOOSE_PLACEMENTS_IN_WAREHOUSE, // per quando il giocatore compra dal mercato e deve disporre ogni singola risorsa nella warehouse

    BAD_STORAGE_REQUEST, // quando sbaglia a scegliere la posizione della risorsa nella warehouse o leader, oppure se richiede di metterla nella lootchest

    BAD_PRODUCTION_REQUEST, //quando viene attivata la produzione ma nel model viene sollevata l'eccezione di BadStorageException e quindi non ha completato la richiesta

    TURN_OVER,

    CURRENT_PLAYER, //notifica il player che è il suo turno

    PLAYER_LOSE,

    PLAYER_WIN,

    REGISTER_SINGLE,

    ABORT_GAME,

    REGISTER_MULTI,

    RESOURCE_ORGANIZED,

    ORGANIZE_RESOURCES,

    BUY_FROM_MARKET;


    }
