# Fantasy Football Server/Client (FFB)

FFB is the Fantasy Football software used by [FUMBBL](https://fumbbl.com)

Client and server are both implemented using Jav8 with Swing/AWT.

# Module structure

## ffb-common

As the name suggests, this is the base dependency for both applications containing the most basic classes.

## ffb-server

Server component providing a websocket endpoint for clients to connect. Commands by the client are stored in queues.

Game sequences are implemented in Step classes that reside on a stack. The top step will receive commands from the queue
and ideally processes them, publishing data to other steps on the stack and either waits for other commands or causes
the stack to be processed further.

Changes to the field model and game data will be published to clients using similar commands.

## ffb-client-logic

This currently contains 99,9% of the client code but in the future this should only contain device and presentation
agnostic aspects.

Similar to the server commands are enqueued and processed one by one. Usually these are changes like player positions or
states and turn modes.

Input events like mouse clicks and keyboard input are processed by ClientState classes which are specialized depending
on the current game phase, e.g. a player is moving, teams are setting up or some out of turn sequences.

Quite a few third party classes for websockets or json handling are repackaged in this module to keep the jar file small
instead of adding bloated dependencies. Though there are also some actual dependencies related to sound and reflection.

## ffb-client

For now this only contains the client's main class but should at one point hold all AWT/Swing related classes and logic
concerning desktop applications only.

## ffb-resources

Separate artifact for sound and icon files.

## ffb-tools

Small utility classes needed to e.g. rebuild the icon folder of [ffb-resources](ffb-resources)

# Start up

## Server

The main class is `com.fumbbl.ffb.server.FantasyFootballServer`

| Arguments            | Description                                                                                                                                                                 |
|----------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [mode]               | * standalone - you usually want this <br/> * fumbbl - used in test and live environments on fumbbl<br/> * standaloneInitDb/fumbblInitDb - used to setup the database schema |
| -inifile [filepath]  | Path to server config file, [server.ini](ffb-server/server.ini) can be used as a template                                                                                   |
| -override [filepath] | Path to an override file supporting the same syntax and values as `inifile`. This allows to use a base ini file and apply environment specific overrides                    |

The server requires a database (connection data has to be defined in ini files), currently mysql up to 5.6 or mariadb up to 10.4. are supported by the used mysql-connector lib (5.1.27).

## Client

The main class is `com.fumbbl.ffb.client.FantasyFootballClientAwt`

| Arguments            | Description                                                                                                                                                                                                                           |
|----------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [mode]               | * -player - start client as player <br/> * -spectator - to join as specatator <br/> * -replay - to load replay data                                                                                                                   |
| -server [hostname]   | Hostname of the server to connect to, e.g. `localhost`                                                                                                                                                                                |
| -port [port]         | Websocket port as defined in server config                                                                                                                                                                                            |
| -coach [coachname]   | Name of the coach used to log in                                                                                                                                                                                                      |
| -teamid [teamid]     | Id of a locally stored team as defined in the [team's xml](ffb-server/teams) - only required for player mode                                                                                                                          |
| -teamName [teamName] | Name of the team defined by -teamId, can also be taken from the xml                                                                                                                                                                   |
| -auth [hexstring]    | Log in information, optional. If ommitted you can log in via the client dialog but we recommend to use auth data. For ease of development you can simply add a string into the coach table in the database and use that as auth info. |
| -gameId [gameId]     | Numeric game id as stored in the data base - only required for replay mode                                                                                                                                                            |
