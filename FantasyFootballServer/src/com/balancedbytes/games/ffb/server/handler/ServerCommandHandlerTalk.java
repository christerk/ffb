package com.balancedbytes.games.ffb.server.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import com.balancedbytes.games.ffb.Card;
import com.balancedbytes.games.ffb.CardFactory;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.SkillFactory;
import com.balancedbytes.games.ffb.SoundId;
import com.balancedbytes.games.ffb.SoundIdFactory;
import com.balancedbytes.games.ffb.Weather;
import com.balancedbytes.games.ffb.WeatherFactory;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.AnimationTypeFactory;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.model.Team;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTalk;
import com.balancedbytes.games.ffb.option.GameOptionFactory;
import com.balancedbytes.games.ffb.option.GameOptionId;
import com.balancedbytes.games.ffb.option.GameOptionIdFactory;
import com.balancedbytes.games.ffb.option.IGameOption;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.ReceivedCommand;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.net.SessionManager;
import com.balancedbytes.games.ffb.server.util.UtilServerGame;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerTalk extends ServerCommandHandler {

  private static final String _ADD = "add";
  private static final String _REMOVE = "remove";

  protected ServerCommandHandlerTalk(FantasyFootballServer server) {
    super(server);
  }

  public NetCommandId getId() {
    return NetCommandId.CLIENT_TALK;
  }

  public boolean handleCommand(ReceivedCommand receivedCommand) {

    ClientCommandTalk talkCommand = (ClientCommandTalk) receivedCommand.getCommand();
    SessionManager sessionManager = getServer().getSessionManager();
    ServerCommunication communication = getServer().getCommunication();
    long gameId = sessionManager.getGameIdForSession(receivedCommand.getSession());
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    String talk = talkCommand.getTalk();

    if (talk != null) {

      String coach = sessionManager.getCoachForSession(receivedCommand.getSession());
      if ((gameState != null) && (sessionManager.getSessionOfHomeCoach(gameId) == receivedCommand.getSession())
        || (sessionManager.getSessionOfAwayCoach(gameId) == receivedCommand.getSession())) {
        if (isTestMode(gameState) && talk.startsWith("/animations")) {
          handleAnimationsCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/animation")) {
          handleAnimationCommand(gameState, talkCommand);
        } else if (isTestMode(gameState) && talk.startsWith("/box")) {
          handleBoxCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/card")) {
          handleCardCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/injury")) {
          handleInjuryCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/options")) {
          handleOptionsCommand(gameState, talkCommand);
        } else if (isTestMode(gameState) && talk.startsWith("/option")) {
          handleOptionCommand(gameState, talkCommand);
        } else if (isTestMode(gameState) && talk.startsWith("/pitches")) {
          handlePitchesCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/pitch")) {
          handlePitchCommand(gameState, talkCommand);
        } else if (isTestMode(gameState) && talk.startsWith("/prone")) {
          handleProneOrStunCommand(gameState, talkCommand, false, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/roll")) {
          handleRollCommand(gameState, talkCommand);
        } else if (isTestMode(gameState) && talk.startsWith("/skill")) {
          handleSkillCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/sounds")) {
          handleSoundsCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/sound")) {
          handleSoundCommand(gameState, talkCommand);
        } else if (isTestMode(gameState) && talk.startsWith("/stat")) {
          handleStatCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/stun")) {
          handleProneOrStunCommand(gameState, talkCommand, true, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/turn")) {
          handleTurnCommand(gameState, talkCommand, receivedCommand.getSession());
        } else if (isTestMode(gameState) && talk.startsWith("/weather")) {
          handleWeatherCommand(gameState, talkCommand);
        } else if (talk.startsWith("/spectators") || talk.startsWith("/specs")) {
          handleSpectatorsCommand(gameState, talkCommand, receivedCommand.getSession());
        } else {
          communication.sendPlayerTalk(gameState, coach, talk);
        }

      } else {
        if (talk.startsWith("/aah")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_AAH);
        } else if (talk.startsWith("/boo")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_BOO);
        } else if (talk.startsWith("/cheer")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CHEER);
        } else if (talk.startsWith("/clap")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CLAP);
        } else if (talk.startsWith("/crickets")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_CRICKETS);
        } else if (talk.startsWith("/laugh")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_LAUGH);
        } else if (talk.startsWith("/ooh")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_OOH);
        } else if (talk.startsWith("/shock")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_SHOCK);
        } else if (talk.startsWith("/stomp")) {
          playSoundAfterCooldown(gameState, coach, SoundId.SPEC_STOMP);
        } else if (talk.startsWith("/spectators") || talk.startsWith("/specs")) {
          handleSpectatorsCommand(gameState, talkCommand, receivedCommand.getSession());
        } else {
          getServer().getCommunication().sendSpectatorTalk(gameState, coach, talk);
        }
      }

    }
    
    return true;

  }
  
  private boolean isTestMode(GameState pGameState) {
    if (pGameState == null) {
      return false;
    }
    String testSetting = getServer().getProperty(IServerProperty.SERVER_TEST);
    return (pGameState.getGame().isTesting() || (StringTool.isProvided(testSetting) && Boolean.parseBoolean(testSetting)));
  }

  private String[] findSpectators(GameState gameState) {
    List<String> spectatorList = new ArrayList<String>();
    SessionManager sessionManager = getServer().getSessionManager();
    Session[] sessions = sessionManager.getSessionsOfSpectators(gameState.getId());
    for (Session session : sessions) {
      String spectator = sessionManager.getCoachForSession(session);
      if (spectator != null) {
        spectatorList.add(spectator);
      }
    }
    String[] spectatorArray = spectatorList.toArray(new String[spectatorList.size()]);
    Arrays.sort(spectatorArray);
    return spectatorArray;
  }

  private void playSoundAfterCooldown(GameState pGameState, String pCoach, SoundId pSound) {
    if ((pGameState != null) && (pCoach != null) && (pSound != null)) {
      if (StringTool.isProvided(getServer().getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN))) {
        long spectatorCooldown = Long.parseLong(getServer().getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN));
        long currentTime = System.currentTimeMillis();
        if (currentTime > (pGameState.getSpectatorCooldownTime(pCoach) + spectatorCooldown)) {
          getServer().getCommunication().sendSound(pGameState, pSound);
          pGameState.putSpectatorCooldownTime(pCoach, currentTime);
        }
      } else {
        getServer().getCommunication().sendSound(pGameState, pSound);
      }
    }
  }

  private void handleAnimationCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 1)) {
      return;
    }
    AnimationType animationType = new AnimationTypeFactory().forName(commands[1]);
    if ((animationType == null) || (animationType == AnimationType.PASS) || (animationType == AnimationType.KICK)
        || (animationType == AnimationType.THROW_TEAM_MATE)) {
      return;
    }
    Card card = null;
    Animation animation = null;
    FieldCoordinate animationCoordinate = null;
    if ((commands.length > 2) && (animationType == AnimationType.CARD) && StringTool.isProvided(commands[2])) {
      card = new CardFactory().forShortName(commands[2].replaceAll("_", " "));
    }
    if (commands.length > 3) {
      try {
        animationCoordinate = new FieldCoordinate(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
      } catch (NumberFormatException nfe) {
        animationCoordinate = null;
      }
    }
    StringBuilder info = new StringBuilder();
    info.append("Playing Animation ").append(animationType.getName());
    if (card != null) {
      animation = new Animation(card);
      info.append(" ").append(card.getShortName());
    } else if (animationCoordinate != null) {
      animation = new Animation(animationType, animationCoordinate);
      info.append(" at ").append(animationCoordinate.toString());
    } else {
      animation = new Animation(animationType);
    }
    info.append(".");
    getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    UtilServerGame.syncGameModel(pGameState, null, animation, null);
  }
  
  private void handleAnimationsCommand(GameState pGameState, ClientCommandTalk pTalkCommand, Session pSession) {
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 0)) {
      List<String> animationNames = new ArrayList<String>();
      for (AnimationType animationType : AnimationType.values()) {
        animationNames.add(animationType.getName());
      }
      Collections.sort(animationNames);
      String[] info = new String[animationNames.size() + 1];
      for (int i = 0; i < info.length; i++) {
        if (i > 0) {
          info[i] = animationNames.get(i - 1);
        } else {
          info[i] = "Available animations:";
        }
      }
      getServer().getCommunication().sendTalk(pSession, pGameState, null, info);
    }
  }

  private void handleOptionCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    Game game = pGameState.getGame();
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 2)) {
      GameOptionId optionId = new GameOptionIdFactory().forName(commands[1]);
      if (optionId == null) {
        return;
      }
      IGameOption gameOption = new GameOptionFactory().createGameOption(optionId);
      if (gameOption == null) {
        return;
      }
      gameOption.setValue(commands[2]);
      game.getOptions().addOption(gameOption);
      StringBuilder info = new StringBuilder();
      info.append("Setting game option ").append(gameOption.getId().getName()).append(" to value ")
          .append(gameOption.getValueAsString()).append(".");
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
      if (game.getStarted() != null) {
        UtilServerGame.syncGameModel(pGameState, null, null, null);
      }
    }
  }

  private void handlePitchCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    Game game = pGameState.getGame();
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 1)) {
      String pitchName = commands[1];
      if (!StringTool.isProvided(pitchName)) {
        return;
      }
      GameOptionFactory gameOptionFactory = new GameOptionFactory();
      String propertyKey = new StringBuilder().append("pitch.").append(commands[1]).toString();
      String pitchUrl = getServer().getProperty(propertyKey);
      if (StringTool.isProvided(pitchUrl)) {
        game.getOptions().addOption(gameOptionFactory.createGameOption(GameOptionId.PITCH_URL).setValue(pitchUrl));
        StringBuilder info = new StringBuilder();
        info.append("Setting pitch to ").append(pitchName);
        getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
        UtilServerGame.syncGameModel(pGameState, null, null, null);
      }
    }
  }
  
  private void handlePitchesCommand(GameState pGameState, ClientCommandTalk pTalkCommand, Session pSession) {
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 0)) {
      List<String> pitchNames = new ArrayList<String>();
      for (String property : getServer().getProperties()) {
        if (property.startsWith("pitch.")) {
          pitchNames.add(property.substring(6));
        }
      }
      Collections.sort(pitchNames);
      String[] info = new String[pitchNames.size() + 1];
      for (int i = 0; i < info.length; i++) {
        if (i > 0) {
          info[i] = pitchNames.get(i - 1);
        } else {
          info[i] = "Available pitches:";
        }
      }
      getServer().getCommunication().sendTalk(pSession, pGameState, null, info);
    }
  }

  private void handleSoundCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 1)) {
      SoundId soundId = new SoundIdFactory().forName(commands[1]);
      if (soundId == null) {
        return;
      }
      StringBuilder info = new StringBuilder();
      info.append("Playing sound ").append(soundId.getName());
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
      getServer().getCommunication().sendSound(pGameState, soundId);
    }
  }

  private void handleSoundsCommand(GameState pGameState, ClientCommandTalk pTalkCommand, Session pSession) {
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 0)) {
      List<String> soundNames = new ArrayList<String>();
      for (SoundId soundId : SoundId.values()) {
        soundNames.add(soundId.getName());
      }
      Collections.sort(soundNames);
      String[] info = new String[soundNames.size() + 1];
      for (int i = 0; i < info.length; i++) {
        if (i > 0) {
          info[i] = soundNames.get(i - 1);
        } else {
          info[i] = "Available sounds:";
        }
      }
      getServer().getCommunication().sendTalk(pSession, pGameState, null, info);
    }
  }

  private void handleWeatherCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    Game game = pGameState.getGame();
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 1)) {
      Weather weather = new WeatherFactory().forShortName(commands[1]);
      if (weather != null) {
        game.getFieldModel().setWeather(weather);
        StringBuilder info = new StringBuilder();
        info.append("Setting weather to ").append(game.getFieldModel().getWeather().getName()).append(".");
        getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
        UtilServerGame.syncGameModel(pGameState, null, null, null);
      }
    }
  }

  private void handleOptionsCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    Game game = pGameState.getGame();
    List<IGameOption> optionList = new ArrayList<IGameOption>();
    for (GameOptionId optionId : GameOptionId.values()) {
      optionList.add(game.getOptions().getOptionWithDefault(optionId));
    }
    Collections.sort(optionList, new Comparator<IGameOption>() {
      public int compare(IGameOption pO1, IGameOption pO2) {
        return pO1.getId().getName().compareTo(pO2.getId().getName());
      }
    });
    for (IGameOption option : optionList) {
      StringBuilder info = new StringBuilder();
      info.append("Option ").append(option.getId().getName()).append(" = ").append(option.getValueAsString());
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    }
  }

  private void handleBoxCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 2)) {
      return;
    }
    Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 2)) {
      if ("rsv".equalsIgnoreCase(commands[1])) {
        putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.RESERVE), "Reserve", null);
      } else if ("ko".equalsIgnoreCase(commands[1])) {
        putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.KNOCKED_OUT), "Knocked Out", null);
      } else if ("bh".equalsIgnoreCase(commands[1])) {
        putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.BADLY_HURT), "Badly Hurt", null);
      } else if ("si".equalsIgnoreCase(commands[1])) {
        int[] roll = { gameState.getServer().getFortuna().getDieRoll(6),
            gameState.getServer().getFortuna().getDieRoll(6) };
        SeriousInjury seriousInjury = DiceInterpreter.getInstance().interpretRollSeriousInjury(roll);
        putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.SERIOUS_INJURY), "Serious Injury",
            seriousInjury);
      } else if ("rip".equalsIgnoreCase(commands[1])) {
        putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.RIP), "RIP", SeriousInjury.DEAD);
      } else if ("ban".equalsIgnoreCase(commands[1])) {
        putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.BANNED), "Banned", null);
      } else {
        break;
      }
    }
    UtilServerGame.syncGameModel(gameState, null, null, null);
  }

  private void handleCardCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 2)) {
      return;
    }
    Card card = new CardFactory().forShortName(commands[2].replace('_', ' '));
    if (card == null) {
      return;
    }
    boolean homeCoach = (sessionManager.getSessionOfHomeCoach(game.getId()) == session);
    TurnData turnData = homeCoach ? game.getTurnDataHome() : game.getTurnDataAway();
    if (_ADD.equals(commands[1])) {
      turnData.getInducementSet().addAvailableCard(card);
      StringBuilder info = new StringBuilder();
      info.append("Added card ").append(card.getName()).append(" for coach ")
          .append(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()).append(".");
      getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
    }
    if (_REMOVE.equals(commands[1])) {
      turnData.getInducementSet().removeAvailableCard(card);
      StringBuilder info = new StringBuilder();
      info.append("Removed card ").append(card.getName()).append(" for coach ")
          .append(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()).append(".");
      getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
    }
    UtilServerGame.syncGameModel(gameState, null, null, null);
  }

  private void handleProneOrStunCommand(GameState gameState, ClientCommandTalk talkCommand, boolean stun, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 1)) {
      return;
    }
    Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 1)) {
      FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
      if (!playerCoordinate.isBoxCoordinate()) {
        StringBuilder info = new StringBuilder();
        info.append("Player ").append(player.getName());
        if (stun) {
          info.append(" stunned.");
          game.getFieldModel().setPlayerState(player, new PlayerState(PlayerState.STUNNED).changeActive(true));
        } else {
          info.append(" placed prone.");
          game.getFieldModel().setPlayerState(player, new PlayerState(PlayerState.PRONE).changeActive(true));
        }
        getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
      }
    }
    UtilServerGame.syncGameModel(gameState, null, null, null);
  }

  private Player[] findPlayersInCommand(Team pTeam, String[] pCommands, int pIndex) {
    Set<Player> players = new HashSet<Player>();
    if (ArrayTool.isProvided(pCommands) && (pIndex < pCommands.length)) {
      if ("all".equalsIgnoreCase(pCommands[pIndex])) {
        for (Player player : pTeam.getPlayers()) {
          players.add(player);
        }
      } else {
        for (int i = pIndex; i < pCommands.length; i++) {
          try {
            Player player = pTeam.getPlayerByNr(Integer.parseInt(pCommands[i]));
            if (player != null) {
              players.add(player);
            }
          } catch (NumberFormatException doNothing) {
          }
        }
      }
    }
    return players.toArray(new Player[players.size()]);
  }

  private void putPlayerIntoBox(GameState pGameState, Player pPlayer, PlayerState pPlayerState, String pBoxName,
      SeriousInjury pSeriousInjury) {
    Game game = pGameState.getGame();
    PlayerResult playerResult = game.getGameResult().getPlayerResult(pPlayer);
    playerResult.setSeriousInjury(pSeriousInjury);
    playerResult.setSeriousInjuryDecay(null);
    game.getFieldModel().setPlayerState(pPlayer, pPlayerState);
    UtilBox.putPlayerIntoBox(game, pPlayer);
    StringBuilder info = new StringBuilder();
    info.append("Player ").append(pPlayer.getName()).append(" moved into box ").append(pBoxName).append(".");
    getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
  }

  private void handleRollCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 1)) {
      if ("clear".equals(commands[1])) {
        pGameState.getDiceRoller().clearTestRolls();
      } else {
        for (int i = 1; i < commands.length; i++) {
          try {
            int testRoll = Integer.parseInt(commands[i]);
            pGameState.getDiceRoller().addTestRoll(testRoll);
          } catch (NumberFormatException doNothing) {
          }
        }
      }
    }
    int[] testRolls = pGameState.getDiceRoller().getTestRolls();
    if (ArrayTool.isProvided(testRolls)) {
      StringBuilder diceRolls = new StringBuilder();
      diceRolls.append("Next dice rolls will be ");
      for (int i = 0; i < testRolls.length; i++) {
        if (i > 0) {
          diceRolls.append(", ");
        }
        diceRolls.append(testRolls[i]);
      }
      getServer().getCommunication().sendPlayerTalk(pGameState, null, diceRolls.toString());
    } else {
      getServer().getCommunication().sendPlayerTalk(pGameState, null, "Next dice rolls will be random.");
    }
  }

  private void handleSkillCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 3)) {
      return;
    }
    Skill skill = new SkillFactory().forName(commands[2].replace('_', ' '));
    if (skill == null) {
      return;
    }
    Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 3)) {
      if (_ADD.equals(commands[1])) {
        player.addSkill(skill);
        getServer().getCommunication().sendAddPlayer(gameState, team.getId(), player,
            game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
        StringBuilder info = new StringBuilder();
        info.append("Added skill ").append(skill.getName()).append(" to player ").append(player.getName()).append(".");
        getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
      }
      if (_REMOVE.equals(commands[1])) {
        player.removeSkill(skill);
        getServer().getCommunication().sendAddPlayer(gameState, team.getId(), player,
            game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
        StringBuilder info = new StringBuilder();
        info.append("Removed skill ").append(skill.getName()).append(" from player ").append(player.getName())
            .append(".");
        getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
      }
    }
  }

  private void handleInjuryCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 2)) {
      return;
    }
    Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 2)) {
      SeriousInjury lastingInjury;
      if ("ni".equalsIgnoreCase(commands[1])) {
        lastingInjury = (gameState.getServer().getFortuna().getDieRoll(6) > 3) ? SeriousInjury.DAMAGED_BACK
            : SeriousInjury.SMASHED_KNEE;
      } else if ("-ma".equalsIgnoreCase(commands[1])) {
        lastingInjury = (gameState.getServer().getFortuna().getDieRoll(6) > 3) ? SeriousInjury.SMASHED_HIP
            : SeriousInjury.SMASHED_ANKLE;
      } else if ("-av".equalsIgnoreCase(commands[1])) {
        lastingInjury = (gameState.getServer().getFortuna().getDieRoll(6) > 3) ? SeriousInjury.SERIOUS_CONCUSSION
            : SeriousInjury.FRACTURED_SKULL;
      } else if ("-ag".equalsIgnoreCase(commands[1])) {
        lastingInjury = SeriousInjury.BROKEN_NECK;
      } else if ("-st".equalsIgnoreCase(commands[1])) {
        lastingInjury = SeriousInjury.SMASHED_COLLAR_BONE;
      } else {
        lastingInjury = null;
      }
      if ((player != null) && (lastingInjury != null)) {
        player.addLastingInjury(lastingInjury);
        getServer().getCommunication().sendAddPlayer(gameState, team.getId(), player,
            game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
        StringBuilder info = new StringBuilder();
        info.append("Player ").append(player.getName()).append(" suffers injury ").append(lastingInjury.getName())
            .append(".");
        getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
      }
    }
  }

  private void handleStatCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 2)) {
      return;
    }
    int stat;
    try {
      stat = Integer.parseInt(commands[2]);
    } catch (NumberFormatException nfe) {
      return;
    }
    Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 3)) {
      if ((player != null) && (stat >= 0)) {
        if ("ma".equalsIgnoreCase(commands[1])) {
          player.setMovement(stat);
          reportStatChange(gameState, player, "MA", stat);
        }
        if ("st".equalsIgnoreCase(commands[1])) {
          player.setStrength(stat);
          reportStatChange(gameState, player, "ST", stat);
        }
        if ("ag".equalsIgnoreCase(commands[1])) {
          player.setAgility(stat);
          reportStatChange(gameState, player, "AG", stat);
        }
        if ("av".equalsIgnoreCase(commands[1])) {
          player.setArmour(stat);
          reportStatChange(gameState, player, "AV", stat);
        }
      }
    }
  }

  private void reportStatChange(GameState pGameState, Player pPlayer, String pStat, int pValue) {
    if ((pGameState != null) && (pPlayer != null)) {
      Game game = pGameState.getGame();
      Team team = game.getTeamHome().hasPlayer(pPlayer) ? game.getTeamHome() : game.getTeamAway();
      getServer().getCommunication().sendAddPlayer(pGameState, team.getId(), pPlayer,
          game.getFieldModel().getPlayerState(pPlayer), game.getGameResult().getPlayerResult(pPlayer));
      StringBuilder info = new StringBuilder();
      info.append("Set ").append(pStat).append(" stat of player ").append(pPlayer.getName()).append(" to ")
          .append(pValue).append(".");
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    }
  }

  private void handleTurnCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
    Game game = gameState.getGame();
    SessionManager sessionManager = getServer().getSessionManager();
    String talk = talkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 1)) {
      int newTurnNr = -1;
      try {
        newTurnNr = Integer.parseInt(commands[1]);
      } catch (NumberFormatException doNothing) {
      }
      if (newTurnNr >= 0) {
        int turnDiff = 0;
        if (sessionManager.getSessionOfHomeCoach(game.getId()) == session) {
          turnDiff = newTurnNr - game.getTurnDataHome().getTurnNr();
        } else {
          turnDiff = newTurnNr - game.getTurnDataAway().getTurnNr();
        }
        game.getTurnDataHome().setTurnNr(game.getTurnDataHome().getTurnNr() + turnDiff);
        game.getTurnDataAway().setTurnNr(game.getTurnDataAway().getTurnNr() + turnDiff);
        StringBuilder info = new StringBuilder();
        info.append("Jumping to turn ").append(newTurnNr).append(".");
        getServer().getCommunication().sendPlayerTalk(gameState, null, info.toString());
        UtilServerGame.syncGameModel(gameState, null, null, null);
      }
    }
  }

  private void handleSpectatorsCommand(GameState pGameState, ClientCommandTalk pTalkCommand, Session pSession) {
    String[] spectators = findSpectators(pGameState);
    Arrays.sort(spectators, new SpecsComparator());
    String[] info;
    StringBuilder spectatorMessage = new StringBuilder();
    if (spectators.length == 1) {
      info = new String[1];
      info[0] = "You are the only spectator of this game.";
    } else {
      info = new String[spectators.length + 1];
      spectatorMessage.append(spectators.length).append(" spectators are watching this game:");
      info[0] = spectatorMessage.toString();
      System.arraycopy(spectators, 0, info, 1, spectators.length);
    }
    getServer().getCommunication().sendTalk(pSession, pGameState, null, info);
  }

  private static class SpecsComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
      if (o1 == null) {
        return o2 == null ? 0 : -1;
      } else if (o2 == null) {
          return 1;
      }

      return o1.compareToIgnoreCase(o2);
    }
  }

}
