# Master of Renaissance Board Game

This project is the digitalised version of "*Master of Renaissance*" board game. It has been developed as final test of Software Engineering, course of Computer Science Engineering at Politecnico di Milano during the A.Y. 2020/21.
<br/>
## Game
The original board game is by *Cranio Creations s.r.l.* and allows at maximum 4 players to play together. 
<br>
[Ruleset (IT)](http://www.craniocreations.it/wp-content/uploads/2021/04/Lorenzo_Cardgame_Rules_ITA_small-3.pdf)
<br>
The digital version here aims to mimic the original one adding those extra comforts given by online gaming. 


## Devs 
AM13 team members: 
[Javin Barone](https://github.com/Javinyx), [Ottavia Belotti](https://github.com/OttaviaBelotti), [Alessio Braccini](https://github.com/AlessioBraccini)
<br/>
Professor Supervisor: Alessandro Margara

## Advanced Features

| Feature       | Status        |
|---------------|:---------------:|
|Command Line Interface| Done |
|Graphing User Interface| Implementing|
|Local Game     | Done|
|Multiple Games | Done|
|Disconnection Resilience| Probably dropped|

## JAR usage
The game is splitted into two JAR files, both can be downloaded form Deliverables directory.
<br>Unless it is desired to play a local game, one Server app instance must be running before the client can actually start playing.</br> 

### Server initialization
Run from console the `MasterOfRenaissanceServer.jar` file using the command:
```
java -jar MasterOfRenaissanceServer.jar
```
By default, if no extra argument is added in the command above, the server will listen for incoming connection on port **27001**. Otherwise, the user can choose the custom port on cmd:
```
java -jar MasterOfRenaissanceServer.jar 2500
```
### Client initialization
To start the client app, either click on the `MasterOfRenaissance.jar` file (GUI mode only) or from cmd for more options:
* GUI mode: `java -jar MasterOfRenaissance.jar`
* CLI mode: `java -jar MasterOfRenaissance.jar cli`
* CLI mode local game: `java -jar MasterOfRenaissance.jar cli local`

## Client App


