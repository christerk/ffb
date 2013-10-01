package com.balancedbytes.games.ffb.server.handler;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.GameOption;
import com.balancedbytes.games.ffb.GameOptionValue;
import com.balancedbytes.games.ffb.Player;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SeriousInjury;
import com.balancedbytes.games.ffb.Skill;
import com.balancedbytes.games.ffb.Sound;
import com.balancedbytes.games.ffb.Team;
import com.balancedbytes.games.ffb.model.Animation;
import com.balancedbytes.games.ffb.model.AnimationType;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.PlayerResult;
import com.balancedbytes.games.ffb.net.NetCommand;
import com.balancedbytes.games.ffb.net.NetCommandId;
import com.balancedbytes.games.ffb.net.commands.ClientCommandTalk;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.FantasyFootballServer;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.IServerProperty;
import com.balancedbytes.games.ffb.server.net.ChannelManager;
import com.balancedbytes.games.ffb.server.net.ServerCommunication;
import com.balancedbytes.games.ffb.server.util.UtilGame;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.util.StringTool;
import com.balancedbytes.games.ffb.util.UtilBox;

/**
 * 
 * @author Kalimar
 */
public class ServerCommandHandlerTalk extends ServerCommandHandler {
  
  protected ServerCommandHandlerTalk(FantasyFootballServer pServer) {
    super(pServer);
  }
  
  public NetCommandId getId() {
    return NetCommandId.CLIENT_TALK;
  }

  public void handleNetCommand(NetCommand pNetCommand) {

    ClientCommandTalk talkCommand = (ClientCommandTalk) pNetCommand;
    
    ChannelManager channelManager = getServer().getChannelManager();
    ServerCommunication communication = getServer().getCommunication();
    long gameId = channelManager.getGameIdForChannel(talkCommand.getSender());
    GameState gameState = getServer().getGameCache().getGameStateById(gameId);
    Game game = (gameState != null) ? gameState.getGame() : null;
    String talk = talkCommand.getTalk();
    
    if (talk != null) {

      String coach = channelManager.getCoachForChannel(talkCommand.getSender());
      if ((game != null) && (channelManager.getChannelOfHomeCoach(gameState) == talkCommand.getSender()) || (channelManager.getChannelOfAwayCoach(gameState) == talkCommand.getSender())) {
      	if (game.isTesting() && talk.startsWith("/animation")) {
        	handleAnimationCommand(gameState, talkCommand);
      	} else if (game.isTesting() && talk.startsWith("/box")) {
        	handleBoxCommand(gameState, talkCommand);
      	} else if (game.isTesting() && talk.startsWith("/injury")) {
          handleInjuryCommand(gameState, talkCommand);
      	} else if (game.isTesting() && talk.startsWith("/options")) {
          handleOptionsCommand(gameState, talkCommand);
      	} else if (game.isTesting() && talk.startsWith("/option")) {
        	handleOptionCommand(gameState, talkCommand); 
        } else if (game.isTesting() && talk.startsWith("/prone")) {
        	handleProneOrStunCommand(gameState, talkCommand, false);
        } else if (game.isTesting() && talk.startsWith("/roll")) {
          handleRollCommand(gameState, talkCommand);
        } else if (game.isTesting() && talk.startsWith("/skill")) {
        	handleSkillCommand(gameState, talkCommand);
        } else if (game.isTesting() && talk.startsWith("/stat")) {
        	handleStatCommand(gameState, talkCommand);
        } else if (game.isTesting() && talk.startsWith("/stun")) {
        	handleProneOrStunCommand(gameState, talkCommand, true);
        } else if (game.isTesting() && talk.startsWith("/turn")) {
          handleTurnCommand(gameState, talkCommand);
        } else {
          communication.sendPlayerTalk(gameState, coach, talk);
        }
      
      } else {
	      if (talk.startsWith("/aah")) {
	        playSoundAfterCooldown(gameState, coach, Sound.SPEC_AAH);
	      } else	if (talk.startsWith("/boo")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_BOO);
        } else if (talk.startsWith("/cheer")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_CHEER);
        } else if (talk.startsWith("/clap")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_CLAP);
        } else if (talk.startsWith("/crickets")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_CRICKETS);
        } else if (talk.startsWith("/laugh")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_LAUGH);
        } else if (talk.startsWith("/ooh")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_OOH);
        } else if (talk.startsWith("/shock")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_SHOCK);
        } else if (talk.startsWith("/stomp")) {
          playSoundAfterCooldown(gameState, coach, Sound.SPEC_STOMP);
        } else if (talk.startsWith("/spectators") || talk.startsWith("/specs")) {
          handleSpectatorsCommand(gameState, talkCommand);
        } else {
          getServer().getCommunication().sendSpectatorTalk(gameState, coach, talk);
        }
      }
      
    }
    
  }
  
  private String[] findSpectators(GameState pGameState) {
    List<String> spectatorList = new ArrayList<String>();
    ChannelManager channelManager = getServer().getChannelManager();
    SocketChannel[] spectatorChannels = channelManager.getChannelsOfSpectators(pGameState);
    for (SocketChannel spectatorChannel : spectatorChannels) {
      String spectator = channelManager.getCoachForChannel(spectatorChannel);
      if (spectator != null) {
        spectatorList.add(spectator);
      }
    }
    String[] spectatorArray = spectatorList.toArray(new String[spectatorList.size()]);
    Arrays.sort(spectatorArray);
    return spectatorArray;
  }
  
  private void playSoundAfterCooldown(GameState pGameState, String pCoach, Sound pSound) {
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
    AnimationType animationType = AnimationType.fromName(commands[1]);
    if ((animationType == null)
    	|| (animationType == AnimationType.PASS)
    	|| (animationType == AnimationType.KICK)
    	|| (animationType == AnimationType.THROW_TEAM_MATE)
    ) {
    	return;
    }
    FieldCoordinate animationCoordinate = null;
    if (commands.length > 3) {
    	try {
    		animationCoordinate = new FieldCoordinate(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
    	} catch (NumberFormatException nfe) {
    		animationCoordinate = null;
    	}
    }
    StringBuilder info = new StringBuilder();
    info.append("Playing Animation ").append(animationType.getName());
    if (animationCoordinate != null) {
    	info.append(" at ").append(animationCoordinate.toString());
    }
    info.append(".");
    getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    Animation animation = new Animation(animationType, animationCoordinate);
  	UtilGame.syncGameModel(pGameState, null, animation, null);
  }

  private void handleOptionCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
  	String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 2)) {
    	GameOption optionName = GameOption.forName(commands[1]);
    	if (optionName == null) {
    		return;
    	}
    	int value = 0;
    	try {
    		value = Integer.parseInt(commands[2]);
    	} catch (NumberFormatException pNfe) {
    		return;
    	}
    	game.getOptions().addOption(new GameOptionValue(optionName, value));
      StringBuilder info = new StringBuilder();
      info.append("Setting game option ").append(optionName.getName()).append(" to value ").append(value).append(".");
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    	UtilGame.syncGameModel(pGameState, null, null, null);
    }
  }

  private void handleOptionsCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
  	GameOption[] optionNames = GameOption.values();
  	Arrays.sort(optionNames, new Comparator<GameOption>() {
  		public int compare(GameOption pO1, GameOption pO2) {
  			return pO1.getName().compareTo(pO2.getName());
  		}
		});
  	for (GameOption optionName : optionNames) {
      StringBuilder info = new StringBuilder();
      info.append("Option ").append(optionName.getName()).append(" = ").append(game.getOptions().getOptionValue(optionName).getValue());
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
  	}
  }
  
  private void handleBoxCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
    ChannelManager channelManager = getServer().getChannelManager();
  	String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 2)) {
    	return;
    }
    Team team = (channelManager.getChannelOfHomeCoach(pGameState) == pTalkCommand.getSender()) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 2)) {
    	if ("rsv".equalsIgnoreCase(commands[1])) {
      	putPlayerIntoBox(pGameState, player, new PlayerState(PlayerState.RESERVE), "Reserve", null);
      } else if ("ko".equalsIgnoreCase(commands[1])) {
      	putPlayerIntoBox(pGameState, player, new PlayerState(PlayerState.KNOCKED_OUT), "Knocked Out", null);
      } else if ("bh".equalsIgnoreCase(commands[1])) {
      	putPlayerIntoBox(pGameState, player, new PlayerState(PlayerState.BADLY_HURT), "Badly Hurt", null);
      } else if ("si".equalsIgnoreCase(commands[1])) {
      	int[] roll = { pGameState.getServer().getFortuna().getDieRoll(6), pGameState.getServer().getFortuna().getDieRoll(6) }; 
      	SeriousInjury seriousInjury = DiceInterpreter.getInstance().interpretRollSeriousInjury(roll);
      	putPlayerIntoBox(pGameState, player, new PlayerState(PlayerState.SERIOUS_INJURY), "Serious Injury", seriousInjury);
      } else if ("rip".equalsIgnoreCase(commands[1])) {
      	putPlayerIntoBox(pGameState, player, new PlayerState(PlayerState.RIP), "RIP", SeriousInjury.DEAD);
      } else if ("ban".equalsIgnoreCase(commands[1])) {
      	putPlayerIntoBox(pGameState, player, new PlayerState(PlayerState.BANNED), "Banned", null);
      } else {
      	break;
      }
    }
    UtilGame.syncGameModel(pGameState, null, null, null);
  }

  private void handleProneOrStunCommand(GameState pGameState, ClientCommandTalk pTalkCommand, boolean pStun) {
  	Game game = pGameState.getGame();
    ChannelManager channelManager = getServer().getChannelManager();
  	String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 1)) {
    	return;
    }
    Team team = (channelManager.getChannelOfHomeCoach(pGameState) == pTalkCommand.getSender()) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 1)) {
    	FieldCoordinate playerCoordinate = game.getFieldModel().getPlayerCoordinate(player);
    	if (!playerCoordinate.isBoxCoordinate()) {
        StringBuilder info = new StringBuilder();
        info.append("Player ").append(player.getName());
    		if (pStun) {
    			info.append(" stunned.");
    			game.getFieldModel().setPlayerState(player, new PlayerState(PlayerState.STUNNED).changeActive(true));
    		} else {
      		info.append(" placed prone.");
    			game.getFieldModel().setPlayerState(player, new PlayerState(PlayerState.PRONE).changeActive(true));
    		}
        getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    	}
    }
    UtilGame.syncGameModel(pGameState, null, null, null);
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
  
  private void putPlayerIntoBox(GameState pGameState, Player pPlayer, PlayerState pPlayerState, String pBoxName, SeriousInjury pSeriousInjury) {
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
  
  private void handleSkillCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
    ChannelManager channelManager = getServer().getChannelManager();
  	String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 3)) {
    	return;
    }
    Skill skill = Skill.fromName(commands[2].replace('_', ' '));
    if (skill == null) {
    	return;
    }
    Team team = (channelManager.getChannelOfHomeCoach(pGameState) == pTalkCommand.getSender()) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 3)) {
      if ("add".equals(commands[1])) {
        player.addSkill(skill);
        getServer().getCommunication().sendAddPlayer(pGameState, team.getId(), player, game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
        StringBuilder info = new StringBuilder();
        info.append("Added skill ").append(skill.getName()).append(" to player ").append(player.getName()).append(".");
        getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
      }
      if ("remove".equals(commands[1])) {
        player.removeSkill(skill);
        getServer().getCommunication().sendAddPlayer(pGameState, team.getId(), player, game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
        StringBuilder info = new StringBuilder();
        info.append("Removed skill ").append(skill.getName()).append(" from player ").append(player.getName()).append(".");
        getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
      }
    }
  }

  private void handleInjuryCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
    ChannelManager channelManager = getServer().getChannelManager();
  	String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands == null) || (commands.length <= 2)) {
    	return;
    }
    Team team = (channelManager.getChannelOfHomeCoach(pGameState) == pTalkCommand.getSender()) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 2)) {
	    SeriousInjury lastingInjury;
	    if ("ni".equalsIgnoreCase(commands[1])) {
	    	lastingInjury = (pGameState.getServer().getFortuna().getDieRoll(6) > 3) ? SeriousInjury.DAMAGED_BACK : SeriousInjury.SMASHED_KNEE;
	    } else if ("-ma".equalsIgnoreCase(commands[1])) {
	    	lastingInjury = (pGameState.getServer().getFortuna().getDieRoll(6) > 3) ? SeriousInjury.SMASHED_HIP : SeriousInjury.SMASHED_ANKLE;
	    } else if ("-av".equalsIgnoreCase(commands[1])) {
	    	lastingInjury = (pGameState.getServer().getFortuna().getDieRoll(6) > 3) ? SeriousInjury.SERIOUS_CONCUSSION : SeriousInjury.FRACTURED_SKULL;
	    } else if ("-ag".equalsIgnoreCase(commands[1])) {
	    	lastingInjury = SeriousInjury.BROKEN_NECK;
	    } else if ("-st".equalsIgnoreCase(commands[1])) {
	    	lastingInjury = SeriousInjury.SMASHED_COLLAR_BONE;
	    } else {
	    	lastingInjury = null;
	    }
	    if ((player != null) && (lastingInjury != null)) {
	    	player.addLastingInjury(lastingInjury);
	    	getServer().getCommunication().sendAddPlayer(pGameState, team.getId(), player, game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
	      StringBuilder info = new StringBuilder();
	      info.append("Player ").append(player.getName()).append(" suffers injury ").append(lastingInjury.getName()).append(".");
	      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
	    }
    }
  }

  private void handleStatCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
    ChannelManager channelManager = getServer().getChannelManager();
  	String talk = pTalkCommand.getTalk();
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
    Team team = (channelManager.getChannelOfHomeCoach(pGameState) == pTalkCommand.getSender()) ? game.getTeamHome() : game.getTeamAway();
    for (Player player : findPlayersInCommand(team, commands, 3)) {
      if ((player != null) && (stat >= 0)) {
        if ("ma".equalsIgnoreCase(commands[1])) {
          player.setMovement(stat);
          reportStatChange(pGameState, player, "MA", stat);
        }
        if ("st".equalsIgnoreCase(commands[1])) {
          player.setStrength(stat);
          reportStatChange(pGameState, player, "ST", stat);
        }
        if ("ag".equalsIgnoreCase(commands[1])) {
          player.setAgility(stat);
          reportStatChange(pGameState, player, "AG", stat);
        }
        if ("av".equalsIgnoreCase(commands[1])) {
          player.setArmour(stat);
          reportStatChange(pGameState, player, "AV", stat);
        }
      }
    }
  }

  private void reportStatChange(GameState pGameState, Player pPlayer, String pStat, int pValue) {
    if ((pGameState != null) && (pPlayer != null)) {
      Game game = pGameState.getGame();
      Team team = game.getTeamHome().hasPlayer(pPlayer) ? game.getTeamHome() : game.getTeamAway();
      getServer().getCommunication().sendAddPlayer(pGameState, team.getId(), pPlayer, game.getFieldModel().getPlayerState(pPlayer), game.getGameResult().getPlayerResult(pPlayer));
      StringBuilder info = new StringBuilder();
      info.append("Set ").append(pStat).append(" stat of player ").append(pPlayer.getName()).append(" to ").append(pValue).append(".");
      getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
    }
  }

  private void handleTurnCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
  	Game game = pGameState.getGame();
    ChannelManager channelManager = getServer().getChannelManager();
  	String talk = pTalkCommand.getTalk();
    String[] commands = talk.split(" +");
    if ((commands != null) && (commands.length > 1)) {
      int newTurnNr = -1;
      try {
        newTurnNr = Integer.parseInt(commands[1]);
      } catch (NumberFormatException doNothing) {
      }
      if (newTurnNr >= 0) {
        int turnDiff = 0;
        if (channelManager.getChannelOfHomeCoach(pGameState) == pTalkCommand.getSender()) {
          turnDiff = newTurnNr - game.getTurnDataHome().getTurnNr();
        } else {
          turnDiff = newTurnNr - game.getTurnDataAway().getTurnNr();
        }
        game.getTurnDataHome().setTurnNr(game.getTurnDataHome().getTurnNr() + turnDiff);
        game.getTurnDataAway().setTurnNr(game.getTurnDataAway().getTurnNr() + turnDiff);
        StringBuilder info = new StringBuilder();
        info.append("Jumping to turn ").append(newTurnNr).append(".");
        getServer().getCommunication().sendPlayerTalk(pGameState, null, info.toString());
        UtilGame.syncGameModel(pGameState, null, null, null);
      }
    }
  }
  
  private void handleSpectatorsCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
    String[] spectators = findSpectators(pGameState);
    String[] spectatorTalk = null;
    StringBuilder spectatorMessage = new StringBuilder();
    if (spectators.length == 1) {
      spectatorTalk = new String[1];
      spectatorTalk[0] = "You are the only spectator of this game.";
    } else {
      spectatorTalk = new String[spectators.length + 1];
      spectatorMessage.append(spectators.length).append(" spectators are watching this game:");
      spectatorTalk[0] = spectatorMessage.toString();
      for (int i = 0; i < spectators.length; i++) {
        spectatorTalk[i + 1] = spectators[i];
      }
    }
    getServer().getCommunication().sendTalk(pTalkCommand.getSender(), pGameState, null, spectatorTalk);
  }
  
}
