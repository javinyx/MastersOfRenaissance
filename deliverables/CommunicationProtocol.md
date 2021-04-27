# Communication Protocol
## Group ID: AM13 - A.Y. 2020/21

 This document's aim is to provide a clear overview of _Maestri del Rinascimento_ project's communication protocol. Most relevant and crucial exhanges are going to be highlighted here. All the messages follow the JSON format shown below:

 ```
 Message style:
 {
     "messageID" : "TAG",
     "payload" : "{message content for the receiver}"
 }
 ```
 The ```messageID``` gives information about the type of event that has arisen in the sender, the ```payload```field adds useful information for the receiver in order for it to react correctly. <br/>
 When an event occurs inside the ClientView, commands will be created based on the messageID (the effective type of the event), each specific command knows how to serialize the content of the payload so that it can encapsulate everything inside a MessageEnvelope which will be sent, through the outputStream, to the Server.
<br/>
 Once the server receives the MessageEnvelope, through a MessageAllocator it maps the messageID to the corresponding Java Message Class so that it can be deserialized correctly into the message object, which will contain all the necessary information for the Model’s public methods’ calls.

 

## Initialization
### 1. Client-server connection phase
 The connection phase is executed upon the players' insertion of their IP address and port. They will contact the server on the default port (which can be configured through the config.json file), that has the purpose of listening for new incoming players and directing them to a new port that will be dinamically assigned to them for the whole game duration. This expedient has been thought as a way to allow the server to host multiple games at the same time.<br/>
 The client app contains some logic for the correct game initialization. Players have to insert their nickname and choose the type of game they wish to play (_singleplayer mode_ or _multiplayer mode_).<br/>

 If **singleplayer mode** is chosen, then the registration will roughly be like this:
 ```
 Message: client -> server
 {
     "messageID" : "REGISTER_SINGLE",
     "payload" : "{
        "nickname" : "Banana"
     }"
 }

 Message: server -> client
 {
     "messageID" : "SERVER_STATE",
     "payload" : "{
         "accepted" : true,
         "starting" : true,
     }"
 }
 ```
 This will cause the creation of a new solo game for the player, that can start immediately.<br/>

 Otherwise, if **multiplayer mode** is chosen, then another choice has to be faced:<br/>
 * **(A)** Create a new multiplayer game and specify how large they wish the lobby to be;
 *  **(B)** Join an already existing game on hold for other players
 <br/>

 Case **(A)**:
 
 ```
 (A) Message: client -> server
 {
     "messageID" : "REGISTER_MULTI",
     "payload" : "{
        "nickname" : "Banana",
        "nPlayers" : 4
     }"
 } 
```
Players will wait until the the lobby is full. The lobby creator will be the first player once the game starts.
 <br/>

 Case **(B)**:

``` 
 (B) Message: client -> server
 {
    "messageID" : "REQUEST_LOBBY",
    "payload" : "{}"
 }
 

 (B) Message: server -> client
 {
     "messageID" : "SHOW_LOBBY",
     "payload" : "{
         "{
             "idLobby" : 1,
             "nPlayersWaiting": 2,
             "totPlayers": 4
         }",

         ...

         "{
             "idLobby": n,
             "nPlayersWaiting": 1,
             "totPlayers": 3
         }"
     }"
 }

 (B) Message: client -> server
 {
     "messageID" : "REGISTER_MULTI",
     "payload" : "{
        "nickname" : "Banana",
        "idLobby" : 1,
     }"
 }
 ```
 Where ```idLobby``` indicates which lobby the player wishes to join among those available sent by server. <br/>
 Server respose for accepting the player:
 ```
 Message: server -> client
 {
     "messageID" : "SERVER_STATE",
     "payload" : "{
         "accepted" : true,
         "starting" : false
     }"
 }
```
 Mind that, in case **(B)**, ```accepted``` could be false if simultaneously someone else filled the last free spot. In that case, the player should choose another lobby.<br/>
 Once a lobby is full, the server sends a *SERVER_STATE* message to all clients waiting for that lobby stating ```"starting" : true```.
 
## Mid-game messages
### 2. Messages from server to client
#### 2.1 Update client view
The following messages are sent to each player in the game, because everyone has to see the changes in the model caused by other players as well.
<br/>

**Current player notification**
```
 {
     "messageID" : “CURRENT_PLAYER",
     "payload" : "{
         "player" : 1
     }"
 }
```
This incapsulate the player's id that has to play.
**Progression onto the _Faith Track_**
```
 {
     "messageID" : “PLAYER_POSITION",
     "payload" : "{
         "player" : 1,
         "boardCell" : 17
     }"
 }
```
* ```player``` specifies which player has gone forward onto the _Faith Track_. The id refers to the player's turn. This message can be caused by the current player's production phase if some faith points are generated;
* ```boardCell``` is the updated position on the player's board.

**Purchase from Market**
```
 {
     "messageID" : "MARKET_UPDATE",
     "payload" : "{
         "dimension" : "column/row",
         "index" : 2,
         "{
             modifiche apportate a quella riga/colonna
             non mi ricordo più cosa vogliamo fare
         }"
     }"
 }
```
* ```dimension``` and ```index``` are the coordinates on the market board where some changes have been applied because somobody has bought resources from there.
* new setup of market (only changes notification)

**Purchase of Production Cards**
```
 {
     "messageID" : "AVAILABLE_PRUDUCTION_CARDS",
     "payload" : "{
         "{
             "deck" : 1,
             "cardID" : 3
         }",

         ...

        "{
            "deck" : 12,
            "cardID" : 3
        }"
     }"
 }
```
The payload displays all 12 decks containing still available production cards. A player can buy only the card on top of all the others belonging to the same deck, so the view can just show the 12 available ones. If a deck has been totally consumed, then ```"cardID" : 0```.
* ```deck``` indicates the deck among the (initial) 12 available;
* ```cardID```is the ID of the card to show on that deck.
<br/>



**End game**
```
{
    "messageID" : "END_GAME",
    "payload" : "{
        "winner" : 1
    }"
}
```
In a singleplayer game, if Lorenzo wins, then ```"winner" : 0```.

#### 2.2 Requests for the current turn player
**Production**
```
{
    "messageID" : "PRODUCTION_RESULT",
    "payload" : "{
        "state" : boolean,
        "outcome" : {"SHIELD", "STONE", "SHIELD"}
    }"
}
```
* ```state```singals the success of the production asked by the player
* ```outcome``` the actual production that will be put in the player's lootchest. If state is negative, then this will be empty.
<br/>

**Resources placements**
```
{
    "messageID" : "RESOURCES_PLACEMENTS",
    "payload" : "{
    }"
}
```
The server notifies the players that they have to place the resources that they previously acquired, for example, from the market.

### 3. Messages from client to server
**Produce**
```
 {
     "messageID" : “PRODUCE",
     "payload" : "{
         "productionCards" : [id1, id2...],
         "fromWarehouse" : {"STONE"},
         "fromLootchest" : {"SHIELD", "STONE"},
         "fromExtraStorage1" : {"SERVANT"},
         "fromExtraStorage2" : {},
         "boostAbilityCards" : [id1, id2],
         "outputLeader" : [out1, out2],
         "basicProduction" : boolean,
         "outputBasic" : "COIN"
     }"
 }
```

**Buy from Market**
```
 {
     "messageID" : “BUY_MARKET",
     "payload" : "{
         "dimension" : "row",
         "index" : 2,
         "marbleLeader" : boolean
     }"
 }
 ----------------------

Message: sever -> client
 {
     "messageID" : "CHOOSE_BLANK",
     "payload" : "{}"
 }

 Message: client -> server
 {
     "messageID" : "BLANK_CHOICE",
     "payload" : "{
         "marbleCard" : id
     }"
 }
```

**Buy Production Cards**
```
 {
     "messageID" : “BUY_PRODUCTION",
     "payload" : "{
         "prodCards" : [id1, id2, id3],
         "fromWarehouse" : ["SHIELD"],
         "fromLootchest" : ["COIN", "COIN"],
         "fromExtraStorage1" : [],
         "fromExtraStorage2" : [],
         "discountAbility" : [id1, id2]
     }"
 }
```
