package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.InjuryAttribute;
import com.fumbbl.ffb.InjuryContext;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.Weather;
import com.fumbbl.ffb.dialog.DialogPlayerChoiceParameter;
import com.fumbbl.ffb.dialog.DialogSelectSkillParameter;
import com.fumbbl.ffb.factory.AnimationTypeFactory;
import com.fumbbl.ffb.factory.CardFactory;
import com.fumbbl.ffb.factory.GameOptionFactory;
import com.fumbbl.ffb.factory.GameOptionIdFactory;
import com.fumbbl.ffb.factory.SeriousInjuryFactory;
import com.fumbbl.ffb.factory.SoundIdFactory;
import com.fumbbl.ffb.factory.WeatherFactory;
import com.fumbbl.ffb.factory.bb2020.PrayerFactory;
import com.fumbbl.ffb.inducement.Card;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.IGameOption;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.factory.PrayerHandlerFactory;
import com.fumbbl.ffb.server.inducements.bb2020.prayers.PrayerDialogSelection;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.util.UtilServerDialog;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.util.ArrayTool;
import com.fumbbl.ffb.util.StringTool;
import com.fumbbl.ffb.util.UtilBox;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
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
					handleOptionsCommand(gameState);
				} else if (isTestMode(gameState) && talk.startsWith("/option")) {
					handleOptionCommand(gameState, talkCommand);
				} else if (isTestMode(gameState) && talk.startsWith("/pitches")) {
					handlePitchesCommand(gameState, talkCommand, receivedCommand.getSession());
				} else if (isTestMode(gameState) && talk.startsWith("/pitch")) {
					handlePitchCommand(gameState, talkCommand);
				} else if (isTestMode(gameState) && talk.startsWith("/prayer")) {
					handlePrayerCommand(gameState, talkCommand, receivedCommand.getSession());
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
					handleSpectatorsCommand(gameState, receivedCommand.getSession(), false);
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
				} else if (talk.startsWith("/hurt")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_HURT);
				} else if (talk.startsWith("/laugh")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_LAUGH);
				} else if (talk.startsWith("/ooh")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_OOH);
				} else if (talk.startsWith("/shock")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_SHOCK);
				} else if (talk.startsWith("/stomp")) {
					playSoundAfterCooldown(gameState, coach, SoundId.SPEC_STOMP);
				} else if (talk.startsWith("/spectators") || talk.startsWith("/specs")) {
					handleSpectatorsCommand(gameState, receivedCommand.getSession(), true);
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
		return (pGameState.getGame().isTesting()
			|| (StringTool.isProvided(testSetting) && Boolean.parseBoolean(testSetting)));
	}

	private String[] findSpectators(GameState gameState) {
		List<String> spectatorList = new ArrayList<>();
		SessionManager sessionManager = getServer().getSessionManager();
		Session[] sessions = sessionManager.getSessionsOfSpectators(gameState.getId());
		for (Session session : sessions) {
			String spectator = sessionManager.getCoachForSession(session);
			if (spectator != null) {
				spectatorList.add(spectator);
			}
		}
		String[] spectatorArray = spectatorList.toArray(new String[0]);
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
		if (commands.length <= 1) {
			return;
		}
		AnimationType animationType = pGameState.getGame().getRules().<AnimationTypeFactory>getFactory(Factory.ANIMATION_TYPE).forName(commands[1]);
		if ((animationType == null) || (animationType == AnimationType.PASS) || (animationType == AnimationType.KICK)
			|| (animationType == AnimationType.THROW_TEAM_MATE)) {
			return;
		}
		Card card = null;
		Animation animation;
		FieldCoordinate animationCoordinate = null;
		if ((commands.length > 2) && (animationType == AnimationType.CARD) && StringTool.isProvided(commands[2])) {
			card = pGameState.getGame().<CardFactory>getFactory(Factory.CARD).forShortName(commands[2].replaceAll("_", " "));
		}
		if (commands.length > 3) {
			try {
				animationCoordinate = new FieldCoordinate(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
			} catch (NumberFormatException ignored) {
			}
		}
		StringBuilder info = new StringBuilder();
		info.append("Playing Animation ").append(animationType.getName());
		if (card != null) {
			animation = new Animation(card);
			info.append(" ").append(card.getShortName());
		} else if (animationCoordinate != null) {
			animation = new Animation(animationType, animationCoordinate);
			info.append(" at ").append(animationCoordinate);
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
		if (commands.length > 0) {
			List<String> animationNames = new ArrayList<>();
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
		if (commands.length > 2) {
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
			String info = "Setting game option " + gameOption.getId().getName() + " to value " +
				gameOption.getValueAsString() + ".";
			getServer().getCommunication().sendPlayerTalk(pGameState, null, info);
			if (game.getStarted() != null) {
				UtilServerGame.syncGameModel(pGameState, null, null, null);
			}
		}
	}

	private void handlePitchCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
		Game game = pGameState.getGame();
		String talk = pTalkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length > 1) {
			String pitchName = commands[1];
			if (!StringTool.isProvided(pitchName)) {
				return;
			}
			GameOptionFactory gameOptionFactory = new GameOptionFactory();
			String propertyKey = "pitch." + commands[1];
			String pitchUrl = getServer().getProperty(propertyKey);
			if (StringTool.isProvided(pitchUrl)) {
				game.getOptions().addOption(gameOptionFactory.createGameOption(GameOptionId.PITCH_URL).setValue(pitchUrl));
				getServer().getCommunication().sendPlayerTalk(pGameState, null, "Setting pitch to " + pitchName);
				UtilServerGame.syncGameModel(pGameState, null, null, null);
			}
		}
	}

	private void handlePitchesCommand(GameState pGameState, ClientCommandTalk pTalkCommand, Session pSession) {
		String talk = pTalkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length > 0) {
			List<String> pitchNames = new ArrayList<>();
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
		if (commands.length > 1) {
			SoundId soundId = new SoundIdFactory().forName(commands[1]);
			if (soundId == null) {
				return;
			}
			getServer().getCommunication().sendPlayerTalk(pGameState, null, "Playing sound " + soundId.getName());
			getServer().getCommunication().sendSound(pGameState, soundId);
		}
	}

	private void handleSoundsCommand(GameState pGameState, ClientCommandTalk pTalkCommand, Session pSession) {
		String talk = pTalkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length > 0) {
			List<String> soundNames = new ArrayList<>();
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
		if (commands.length > 1) {
			Weather weather = new WeatherFactory().forShortName(commands[1]);
			if (weather != null) {
				game.getFieldModel().setWeather(weather);
				getServer().getCommunication().sendPlayerTalk(pGameState, null, "Setting weather to " + game.getFieldModel().getWeather().getName() + ".");
				UtilServerGame.syncGameModel(pGameState, null, null, null);
			}
		}
	}

	private void handleOptionsCommand(GameState pGameState) {
		Game game = pGameState.getGame();
		List<IGameOption> optionList = new ArrayList<>();
		for (GameOptionId optionId : GameOptionId.values()) {
			optionList.add(game.getOptions().getOptionWithDefault(optionId));
		}
		optionList.sort(Comparator.comparing(pO -> pO.getId().getName()));
		for (IGameOption option : optionList) {
			getServer().getCommunication().sendPlayerTalk(pGameState, null, "Option " + option.getId().getName() + " = " + option.getValueAsString());
		}
	}

	private void handleBoxCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
		Game game = gameState.getGame();
		SessionManager sessionManager = getServer().getSessionManager();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length <= 2) {
			return;
		}
		Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
		RollMechanic mechanic = ((RollMechanic) game.getFactory(Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
		for (Player<?> player : findPlayersInCommand(team, commands, 2)) {
			if ("rsv".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.RESERVE), "Reserve", null);
			} else if ("ko".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.KNOCKED_OUT), "Knocked Out", null);
			} else if ("bh".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.BADLY_HURT), "Badly Hurt", null);
			} else if ("si".equalsIgnoreCase(commands[1])) {
				int[] roll = mechanic.rollCasualty(gameState.getDiceRoller());
				SeriousInjury seriousInjury = mechanic.interpretSeriousInjuryRoll(game, new InjuryContext(), roll);
				putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.SERIOUS_INJURY), "Serious Injury",
					seriousInjury);
			} else if ("rip".equalsIgnoreCase(commands[1])) {
				putPlayerIntoBox(gameState, player, new PlayerState(PlayerState.RIP), "RIP", ((SeriousInjuryFactory) game.getFactory(FactoryType.Factory.SERIOUS_INJURY)).dead());
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
		if (commands.length <= 2) {
			return;
		}
		Card card = gameState.getGame().<CardFactory>getFactory(Factory.CARD).forShortName(commands[2].replace('_', ' '));
		if (card == null) {
			return;
		}
		boolean homeCoach = (sessionManager.getSessionOfHomeCoach(game.getId()) == session);
		TurnData turnData = homeCoach ? game.getTurnDataHome() : game.getTurnDataAway();
		if (_ADD.equals(commands[1])) {
			turnData.getInducementSet().addAvailableCard(card);
			String info = "Added card " + card.getName() + " for coach " +
				(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()) + ".";
			getServer().getCommunication().sendPlayerTalk(gameState, null, info);
		}
		if (_REMOVE.equals(commands[1])) {
			turnData.getInducementSet().removeAvailableCard(card);
			String info = "Removed card " + card.getName() + " for coach " +
				(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()) + ".";
			getServer().getCommunication().sendPlayerTalk(gameState, null, info);
		}
		UtilServerGame.syncGameModel(gameState, null, null, null);
	}

	private void handlePrayerCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
		Game game = gameState.getGame();
		SessionManager sessionManager = getServer().getSessionManager();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length < 2) {
			return;
		}

		int roll;
		try {
			roll = Integer.parseInt(commands[1]);
		} catch (NumberFormatException ex) {
			return;
		}

		Prayer prayer = gameState.getGame().<PrayerFactory>getFactory(Factory.PRAYER).forRoll(roll);
		if (prayer == null) {
			return;
		}
		boolean homeCoach = (sessionManager.getSessionOfHomeCoach(game.getId()) == session);

		PrayerHandlerFactory handlerFactory = game.getFactory(FactoryType.Factory.PRAYER_HANDLER);

		handlerFactory.forPrayer(prayer).ifPresent(handler -> {
			handler.initEffect(null, gameState, homeCoach ? game.getTeamHome().getId() : game.getTeamAway().getId());
			if (commands.length == 3) {
				int index;
				try {
					index = Integer.parseInt(commands[2]);
					Skill skill = null;
					String playerId = null;
					if (game.getDialogParameter() instanceof DialogSelectSkillParameter) {
						DialogSelectSkillParameter dialogParameter = (DialogSelectSkillParameter) game.getDialogParameter();
						skill = dialogParameter.getSkills().get(index);
						playerId = dialogParameter.getPlayerId();
					} else if (
						game.getDialogParameter() instanceof DialogPlayerChoiceParameter
					) {
						DialogPlayerChoiceParameter dialogPlayerChoiceParameter = (DialogPlayerChoiceParameter) game.getDialogParameter();
						playerId = dialogPlayerChoiceParameter.getPlayerIds()[index];
					}

					handler.applySelection(null, game, new PrayerDialogSelection(playerId, skill));

				} catch (NumberFormatException ignored) {
				}

			}
			UtilServerDialog.hideDialog(gameState);

		});


		String info = "Added prayer " + prayer.getName() + " for coach " +
			(homeCoach ? game.getTeamHome().getCoach() : game.getTeamAway().getCoach()) + ".";
		getServer().getCommunication().sendPlayerTalk(gameState, null, info);

		UtilServerGame.syncGameModel(gameState, null, null, null);
	}

	private void handleProneOrStunCommand(GameState gameState, ClientCommandTalk talkCommand, boolean stun,
	                                      Session session) {
		Game game = gameState.getGame();
		SessionManager sessionManager = getServer().getSessionManager();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length <= 1) {
			return;
		}
		Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
		for (Player<?> player : findPlayersInCommand(team, commands, 1)) {
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

	private Player<?>[] findPlayersInCommand(Team pTeam, String[] pCommands, int pIndex) {
		Set<Player<?>> players = new HashSet<>();
		if (ArrayTool.isProvided(pCommands) && (pIndex < pCommands.length)) {
			if ("all".equalsIgnoreCase(pCommands[pIndex])) {
				Collections.addAll(players, pTeam.getPlayers());
			} else {
				for (int i = pIndex; i < pCommands.length; i++) {
					try {
						Player<?> player = pTeam.getPlayerByNr(Integer.parseInt(pCommands[i]));
						if (player != null) {
							players.add(player);
						}
					} catch (NumberFormatException ignored) {
					}
				}
			}
		}
		return players.toArray(new Player[0]);
	}

	private void putPlayerIntoBox(GameState pGameState, Player<?> pPlayer, PlayerState pPlayerState, String pBoxName,
	                              SeriousInjury pSeriousInjury) {
		Game game = pGameState.getGame();
		PlayerResult playerResult = game.getGameResult().getPlayerResult(pPlayer);
		playerResult.setSeriousInjury(pSeriousInjury);
		playerResult.setSeriousInjuryDecay(null);
		game.getFieldModel().setPlayerState(pPlayer, pPlayerState);
		UtilBox.putPlayerIntoBox(game, pPlayer);
		getServer().getCommunication().sendPlayerTalk(pGameState, null, "Player " + pPlayer.getName() + " moved into box " + pBoxName + ".");
	}

	private void handleRollCommand(GameState pGameState, ClientCommandTalk pTalkCommand) {
		String talk = pTalkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length > 1) {
			if ("clear".equals(commands[1])) {
				pGameState.getDiceRoller().clearTestRolls();
			} else {
				for (int i = 1; i < commands.length; i++) {
					try {
						int testRoll = Integer.parseInt(commands[i]);
						pGameState.getDiceRoller().addTestRoll(testRoll);
					} catch (NumberFormatException ignored) {
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
		if (commands.length <= 3) {
			return;
		}
		Skill skill = gameState.getGame().getRules().getSkillFactory().forName(commands[2].replace('_', ' '));
		if (skill == null) {
			return;
		}
		Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
		for (Player<?> player : findPlayersInCommand(team, commands, 3)) {
			if (!(player instanceof RosterPlayer)) {
				continue;
			}
			if (_ADD.equals(commands[1])) {
				((RosterPlayer) player).addSkill(skill);
				getServer().getCommunication().sendAddPlayer(gameState, team.getId(), (RosterPlayer) player,
					game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
				getServer().getCommunication().sendPlayerTalk(gameState, null, "Added skill " + skill.getName() + " to player " + player.getName() + ".");
			}
			if (_REMOVE.equals(commands[1])) {
				((RosterPlayer) player).removeSkill(skill);
				getServer().getCommunication().sendAddPlayer(gameState, team.getId(), (RosterPlayer) player,
					game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
				String info = "Removed skill " + skill.getName() + " from player " + player.getName() +
					".";
				getServer().getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
	}

	private void handleInjuryCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
		Game game = gameState.getGame();
		SessionManager sessionManager = getServer().getSessionManager();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length <= 2) {
			return;
		}
		Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
		SeriousInjuryFactory factory = game.getFactory(Factory.SERIOUS_INJURY);
		for (Player<?> player : findPlayersInCommand(team, commands, 2)) {
			SeriousInjury lastingInjury;
			if ("ni".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.NI);
			} else if ("-ma".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.MA);
			} else if ("-av".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.AV);
			} else if ("-ag".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.AG);
			} else if ("-st".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.ST);
			} else if ("-pa".equalsIgnoreCase(commands[1])) {
				lastingInjury = factory.forAttribute(InjuryAttribute.PA);
			} else {
				lastingInjury = null;
			}
			if ((player instanceof RosterPlayer) && (lastingInjury != null)) {
				((RosterPlayer) player).addLastingInjury(lastingInjury);
				getServer().getCommunication().sendAddPlayer(gameState, team.getId(), (RosterPlayer) player,
					game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
				String info = "Player " + player.getName() + " suffers injury " + lastingInjury.getName() +
					".";
				getServer().getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
	}

	private void handleStatCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
		Game game = gameState.getGame();
		SessionManager sessionManager = getServer().getSessionManager();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length <= 2) {
			return;
		}
		int stat;
		try {
			stat = Integer.parseInt(commands[2]);
		} catch (NumberFormatException nfe) {
			return;
		}
		Team team = (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
		for (Player<?> genericPlayer : findPlayersInCommand(team, commands, 3)) {
			if ((genericPlayer instanceof RosterPlayer) && (stat >= 0)) {
				RosterPlayer player = (RosterPlayer) genericPlayer;
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
				if ("pa".equalsIgnoreCase(commands[1])) {
					player.setPassing(stat);
					reportStatChange(gameState, player, "PA", stat);
				}
				if ("av".equalsIgnoreCase(commands[1])) {
					player.setArmour(stat);
					reportStatChange(gameState, player, "AV", stat);
				}
			}
		}
	}

	private void reportStatChange(GameState pGameState, RosterPlayer pPlayer, String pStat, int pValue) {
		if ((pGameState != null) && (pPlayer != null)) {
			Game game = pGameState.getGame();
			Team team = game.getTeamHome().hasPlayer(pPlayer) ? game.getTeamHome() : game.getTeamAway();
			getServer().getCommunication().sendAddPlayer(pGameState, team.getId(), pPlayer,
				game.getFieldModel().getPlayerState(pPlayer), game.getGameResult().getPlayerResult(pPlayer));
			String info = "Set " + pStat + " stat of player " + pPlayer.getName() + " to " +
				pValue + ".";
			getServer().getCommunication().sendPlayerTalk(pGameState, null, info);
		}
	}

	private void handleTurnCommand(GameState gameState, ClientCommandTalk talkCommand, Session session) {
		Game game = gameState.getGame();
		SessionManager sessionManager = getServer().getSessionManager();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length > 1) {
			int newTurnNr = -1;
			try {
				newTurnNr = Integer.parseInt(commands[1]);
			} catch (NumberFormatException ignored) {
			}
			if (newTurnNr >= 0) {
				int turnDiff;
				if (sessionManager.getSessionOfHomeCoach(game.getId()) == session) {
					turnDiff = newTurnNr - game.getTurnDataHome().getTurnNr();
				} else {
					turnDiff = newTurnNr - game.getTurnDataAway().getTurnNr();
				}
				game.getTurnDataHome().setTurnNr(game.getTurnDataHome().getTurnNr() + turnDiff);
				game.getTurnDataAway().setTurnNr(game.getTurnDataAway().getTurnNr() + turnDiff);
				getServer().getCommunication().sendPlayerTalk(gameState, null, "Jumping to turn " + newTurnNr + ".");
				UtilServerGame.syncGameModel(gameState, null, null, null);
			}
		}
	}

	private void handleSpectatorsCommand(GameState pGameState, Session pSession, boolean issuedBySpec) {
		String[] spectators = findSpectators(pGameState);
		Arrays.sort(spectators, new SpecsComparator());
		String[] info;
		StringBuilder spectatorMessage = new StringBuilder();
		if (spectators.length == 0) {
			info = new String[1];
			info[0] = "There are no spectators.";
		} else if (issuedBySpec && spectators.length == 1) {
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
