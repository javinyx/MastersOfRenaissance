# Communication Protocol
## Group ID: AM13 - A.Y. 2020/21

This document's aim is to provide a clear overview of *Maestri del Rinascimento* project's communication protocol. Most relevant and crucial exchanges are going to be highlighted here. 
<br>

Each message that goes through the network is a `MesssageEnvelope` class that has been serialized with JSON. So all of them follow the JSON format shown below:

```
{
	"messageID" : "TAG",
	"payload" : "{message content for the receiver}"
}
```
And the Java class:
```Java
public class MessageEnvelope{
	MessageID messageId;
	String payload;
}
```

The `messageID` gives information about the type of event that has arisen in the sender, the `payload` field adds useful information for the receiver in order for it to react correctly. The `payload` could be a simple String or one of the Message class serializd with JSON as well if the `MessageId` has one associated to it (see below).
<br/>

Whether it's an event occurred whithin the Client or a Server's response, respective messages will be created. For more complex messages that has to be sent through the network, we use ad hoc Java classes to incapsulate all the information in an organized way.  
<br/>

Thanks to the `MessageEnvelope` we always know how to immediately deserialize a package coming from the network. Once we have extracted the Json Object into a `MessageEnvelope` Java object, we can deserialize also the payload into a "primitive" type (i.e. String, Integer) or into one of our `Message` Java object based on the messageID (the effective type of the event).
<br/>

We decided to use Json as means of serialization instead of Java serialization because it is universally manageable, allowing us the potential of rewrite the client app in another programming languages in the future. Furthermore is readable, easing the debug process for us.


# Table of Contents
- [Communication Protocol](#communication-protocol)
  * [Group ID: AM13 - A.Y. 2020/21](#group-id--am13---ay-2020-21)
- [Initialization](#initialization)
  * [1. Player registration](#1-player-registration)
    + [Case (A)](#case--a--)
    + [Case (B)](#case--b--)
  * [2. Game initiation](#2-game-initiation)
- [Mid-game messages](#mid-game-messages)
  * [3. Server to Client Messages](#3-server-to-client-messages)
    + [3.1 Client View Update](#3-1-client-view-update)
      - [Current player notification](#current-player-notification)
      - [Progression inside the _Faith Track_](#progression-inside-the--faith-track-)
      - [Purchase from Market](#purchase-from-market)
      - [Purchase of Production Cards](#purchase-of-production-cards)
      - [End game](#end-game)
    + [Requests for current player turn](#requests-for-current-player-turn)
      - [Production](#production)
      - [Resources placement](#resources-placement)
  * [4. Client to Server Messages](#4-client-to-server-messages)
    + [Production](#production-1)
    + [Buy from Market](#buy-from-market)
    + [Buy Production Cards](#buy-production-cards)

# Initialization
## 1. Player registration
The connection phase is executed upon the players' insertion of their IP address and port. They will contact the server on the default port (which can be configured through the config.json file), that has the purpose of listening for new incoming players. Once the connection is accepted, it submits the respective `ClientSocketConnection` (which implements `Runnable` interface) of that player to a ThreadPool which will be in charge of serving the player throughout the game. This lets the Server keep listening for new connection requests without beign interrupted by players already registered. 
<br/>
Now the ClientConnetionSocket begins the RegistrationPhase by sending the Client some requests of which we report here the respective messageIds: `ASK_NICK`, `PLAYER_NUM` and `CONFIRM_REGISTRATION`.

Once this is done, if the multiplayer option has been chosen, the player is put into a lobby waiting for other players whom would like to play with the same game size. The *waiting lobby* structure is a map (i.e. `Map<String, ClientConnection> threePlayerWait`) in which each entry is the the nickname and the ClientConnection of players waiting to find a group. We keep filling this map as play requests for the same size arrives until the number of players in it equates the lobby capacity (for example, the `threePlayerWait` has a capacity of 3 people). Once it's filled, we clear that map, and move the players to an another map since the game can be finally started. Following the previous example, the new map is a `Map<ClientConnection, List<ClientConnection>> threePlayerPlay` and it has one entry for each player currently in a game of size 3 and, as value, has a list of his/her opponents. This expedient has been thought as a way to allow the server to host multiple games at the same time. In fact, we  allow just one room waiting for a game of size 3 to start as we wish to fill quickly the lobby, but multiple games of size 3 going on at the same time.

 
## 2. Game initiation

![Game initiation](img/gameinit.png)

There are a few things to do upon game initiation:
* Update each player's view with their initial position on the faith track based on their turn order, with messages like the following:
```
// Message: server -> client
{
	"messageID" : "PLAYER_POSITION",
	"payload" : {
		"player" : 1,
		"boardCell" : 2
	}
}
```
* Each player needs to choose 2 out of their 4 given LeaderCards, the messages will look as such:
```
// Message: server -> client
{
	"messageID" : "CHOOSE_LEADER_CARDS",
	"payload" : {
		"leaderCards" : [3, 4, 6, 8]
	}
}

// Message: client -> server
{
	"messageID" : "CHOOSE_LEADER_CARDS",
	"payload" : {
		"leaderCardsToKeep" : "[4, 8]"
	}
}
```

* Request players' preferred resources based on their turn order, with messages like the following: 
```
// Message: server -> client
{
	"messageID" : "CHOOSE_RESOURCE",
	"payload" : {
		"quantity": 2
	}
} 

// Message: client -> server
{
	"messageID" : "STORE_RESOURCES",
	"payload" : {
		{
			"resource": "SHIELD",
			"position": "SmallWarehouse"
		},
		{
			"resource": "COIN",
			"position": "MidWarehouse"
		}
	}
}

//If the player's requests cannot be processed because malformed
// Message: server -> client
{
	"messageID" : "BAD_STORAGE_REQUEST"
	"payload"   : {}
}
```
Every value and faithPoint assignment follows the ruleset in the table below:

| Player | Resources of your choosing | Faith Points |
|:------:|:--------------------------:|:------------:|
|   1st  |              0             |       0      |
|   2nd  |              1             |       0      |
|   3rd  |              1             |       1      |
|   4th  |              2             |       1      |



# Mid-game messages

![Mid-Game](img/midgame.png)

## 3. Server to Client Messages
### 3.1 Client View Update
The following messages are sent to each player in the game, because everyone has to see the changes in the model caused by other players as well:

#### Update Message
```Java
public class UpdateMessage extends SimpleMessage{
	private final int playerId, playerPos, nextPlayerId;
    private final Resource[][] marketBoard;
    private final Resource extraMarble;
    private final List<Integer> availableProductionCards;

    private final List<BiElement<Integer,Integer>> productionCardsId;
    private final List<BiElement<Integer, Boolean>> leadersId;
    private final Map<BiElement<Resource, Storage>, Integer> addedResources, removedResources;
}
```
Usually this message is sent by the server at the end of each player's turn to all the players, but it's occasionally used even in registrationPhase to communicate the initial status. This class is one of the Messages class that will be serialized in JSON and the resulting string will be squished into the `payload` field of `MessageEnvelope`.
* `playerId`and `playerPos`are the id and current position of the player who has triggered this update message (at the end of the turn);
* `nextPlayerId`is the id of the player who has to play next;
* `marketBoard`, `extraMarble` and `availableProductionCards` are included since they're global information that concern every player. In fact, after a market action or a development card's purchasen done by `playerId`, the market state or the cards still available have been through changes that the other must know, so their client status has to be updated as well since the model has changed;
* `productionCards`is a list of pairs of cardId bought and in which of the 3 stacks on the `playerId` board has been put;
* `leadersId`is a list of pairs of leadersId and their activation status represeted through a boolean. This can be used to communicate how many leaders the `playerId` has, which are active and which not;
* `addedResources` and `removedResources` represents the modification that the player storage has been through during his/her turn. So everyone can update their local storage status of `playerId` accordingly by adding those resources specified in `addedresources` and removing those in `removedResources`. The map contains a pair of Resource and Storage type as key and the quantity.

So this message incapsulate the player's id that has to play the next turn as well as the modifications and progression happened last turn.


#### Progression onto the _Faith Track_
```
{
	"messageID" : “PLAYER_POSITION",
	"payload" : {
		"player" : 1,
		"boardCell" : 17
	}
}
```
* `player` specifies which player has gone forward onto the _Faith Track_. The id refers to the player's turn. This message can be caused by the current player's production phase 
   if some faith points are generated, or from someone else discarding resources;
* `boardCell` is the updated position on the player's board.

#### Purchase from Market
```
{
	"messageID" : "MARKET_UPDATE",
	"payload" : {
		"dimension" : "column/row",
		"index" : 2,
		"changes" : ["BLANK", "FAITH", "SHIELD"],
		"extra" : "COIN"
	}
}
```
* `dimension` and `index` are the coordinates on the market board where some changes have been applied because somobody has bought resources from there.
* new setup of market (only changes notification).

#### Purchase of Production Cards
```
{
	"messageID" : "AVAILABLE_PRODUCTION_CARDS",
	"payload" : {
		{
			"deck" : 1,
			"cardID" : 3
		},
		
		...
		
		{
			"deck" : 12,
			"cardID" : 3
		}
	}
}
```
The payload displays all 12 decks containing the currently available production cards. A player can buy only the card on top of all the others belonging to the same deck, so the view can just show the 12 available ones. If a deck is empty, then `"cardID" : 0`.
* `deck` indicates the deck among the (initial) 12 available;
* `cardID` is the ID of the card to show on that deck.

#### End game
```
{
	"messageID" : "END_GAME",
	"payload" : {
		"winner" : 1
	}
}
```
In a singleplayer game, if Lorenzo wins, then `"winner" : 0`.

### 3.2 Requests for current player turn
#### Production
```
{
	"messageID" : "PRODUCTION_RESULT",
	"payload" : {
		"state" : boolean,
		"outcome" : {"SHIELD", "STONE", "SHIELD"}
	}
}
```
* `state` signals the success of the production asked by the player;
* `outcome` is the actual production that will be put in the player's lootchest; if state is negative, then this will be empty.

#### Resources placement
```
{
	"messageID" : "RESOURCES_PLACEMENTS",
	"payload" : {}
}
```
The server notifies the players that they have to place the resources that they previously acquired, for example, from the market.

## 4. Client to Server Messages
### Leader card activation
```
{
  "messageID" : "ACTIVATE_LEADER",
  "payload" : {
       "leaderCard" : id
  }
}
```
### Production
```
{
	"messageID" : "PRODUCTION",
	"payload" : {
		"productionCards" : [id1, id2...],
		"fromWarehouse" : {"STONE"},
		"fromLootchest" : {"SHIELD", "STONE"},
		"fromExtraStorage1" : {"SERVANT"},
		"fromExtraStorage2" : {},
		"boostAbilityCards" : [id1, id2],
		"outputLeader" : [out1, out2],
		"basicProduction" : boolean,
		"outputBasic" : "COIN"
	}
}
```
* `productionCards` contains the cards that the user wants to use for production.
* `fromWarehouse` indicates the resources that should be taken from the warehouse.
* `fromLootchest` indicates the resources that should be taken from the lootchest.
* `fromExtraStorage1` and `fromExtraStorage2` indicate the resource that should be taken from the extra storage supplied by an active leader card of StorageAbility.
* `boostAbilityCards` contains the leader cards of type BoostAbility that will be used during production.
* `outputLeader` contains all the BoostAbility leaderCards outputs of player's choice as a list of resources.
* `basicProduction` indicates if the user would like to use the standard production given by the game board.
* `outputBasic` indicates the type of Resource the user wants to receive from basicProduction.

### Buy from Market
```
{
	"messageID" : "BUY_MARKET",
	"payload" : {
        "info" : {
            "dimension" : "row",
            "index" : 2
		},
		{
		"leaders" : [
            {
                "marbleLeader" : id,
                "quantity" : 1
            },
            {
                "marbleLeader" : id,
                "quantity" : 2
            }
		]
	}
}
```
`leaders` contains the information regarding active MarbleAbility leader cards
which the player would like to use upon the specified quantity of 
blank marbles collected from the market.

### Buy Production Cards
```
{
	"messageID" : "BUY_PRODUCTION",
	"payload" : {
		"prodCards" : [id1, id2, id3],
		"fromWarehouse" : ["SHIELD"],
		"fromLootchest" : ["COIN", "COIN"],
		"fromExtraStorage1" : [],
		"fromExtraStorage2" : [],
		"discountAbility" : [id1, id2]
	}
}
```
* `prodCards` contains the cards that the user wants to buy.
* `fromWarehouse` indicates the resources that should be taken from the warehouse.
* `fromLootchest` indicates the resources that should be taken from the lootchest.
* `fromExtraStorage1` and `fromExtraStorage2` indicate the resource that should be taken from the extra storage supplied by an active leader card of StorageAbility.
* `discountAbility` indicates the leader cards of Discount type that the player wants
to use during this phase.

## Message classes mapping to MessageIDs
Most important messages are reported below. Mind that some error messages are not included in the list since they don't have any `Payload` associeted to them and are self-explanatory.

Furthermore, some messages are used 

| MessageID | Payload | Meaning |
|-----------|:---------:|:-------|
|ASK_NICK |String|
|PLAYER_NUM | Integer| The size the player wants the game to be|
|CONFIRM_REGISTRATION|| Player is now registered correctly in the server|
|UPDATE| UpdateMessage| Used to show the status of a player or just the changes between turns|
|CHOOSE_LEADER_CARD| List\<Integer\>| List of 4 leaders to choose and the 2 in response
|ACK | Boolean | Player's request has been fullfilled by the server. `True` if the action is a major one (that can be done just once per turn), `False` otherwise.|
|
