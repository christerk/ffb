package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.FantasyFootballException;
import com.fumbbl.ffb.IKeyedItem;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.commands.ClientCommandTalk;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.ServerCommunication;
import com.fumbbl.ffb.server.net.SessionManager;
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

public abstract class TalkHandler implements IKeyedItem {

	private static final String HOME = "home";
	private static final String AWAY = "away";
	private static final String TEAM_DELIM = "_";

	private final Set<String> commands;
	private final int commandPartsThreshold;
	private final Set<TalkRequirements.Privilege> requiresOnePrivilegeOf = new HashSet<>();
	private final TalkRequirements.Client requiredClientMode;
	private final TalkRequirements.Environment requiredEnvironment;

	public TalkHandler(String command, int commandPartsThreshold, TalkRequirements.Client requiredClientMode, TalkRequirements.Environment requiredEnvironment, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		this(Collections.singleton(command), commandPartsThreshold, requiredClientMode, requiredEnvironment, requiresOnePrivilegeOf);
	}

	public TalkHandler(Set<String> commands, int commandPartsThreshold, TalkRequirements.Client requiredClientMode, TalkRequirements.Environment requiredEnvironment, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		this.commands = commands;
		this.commandPartsThreshold = commandPartsThreshold;
		this.requiredClientMode = requiredClientMode;
		this.requiredEnvironment = requiredEnvironment;
		if (requiresOnePrivilegeOf != null) {
			this.requiresOnePrivilegeOf.addAll(Arrays.asList(requiresOnePrivilegeOf));
		}
	}

	@Override
	public String getKey() {
		return getClass().getSimpleName();
	}

	public boolean handle(FantasyFootballServer server, ClientCommandTalk talkCommand, Session session) {
		SessionManager sessionManager = server.getSessionManager();
		long gameId = sessionManager.getGameIdForSession(session);
		GameState gameState = server.getGameCache().getGameStateById(gameId);

		Game game = gameState.getGame();
		String talk = talkCommand.getTalk();
		String[] commands = talk.split(" +");
		if (commands.length <= commandPartsThreshold) {
			return false;
		}

		if (!handles(server, commands[0], session)) {
			return false;
		}

		try {
			Team team = shouldGetTeamFromCommand() ? getTeamFromCommand(commands[0], game) : getTeamFromSession(session, sessionManager, game);

			handle(server, gameState, commands, team, session);

		} catch (FantasyFootballException e) {
			server.getDebugLog().log(gameId, e);
			return false;
		}

		return true;
	}

	protected boolean shouldGetTeamFromCommand() {
		return false;
	}

	private Team getTeamFromCommand(String command, Game game) throws FantasyFootballException {
		if (!StringTool.isProvided(command)) {
			throw new FantasyFootballException("No command given");
		}

		String[] commandParts = command.split(TEAM_DELIM);

		if (commandParts.length != 2) {
			throw new FantasyFootballException("Unsupported format for command: " + command);
		}

		switch (commandParts[1].toLowerCase()) {
			case HOME:
				return game.getTeamHome();
			case AWAY:
				return game.getTeamAway();
			default:
				throw new FantasyFootballException("Invalid team: " + commandParts[1]);
		}
	}

	private Team getTeamFromSession(Session session, SessionManager sessionManager, Game game) {
		return (sessionManager.getSessionOfHomeCoach(game.getId()) == session) ? game.getTeamHome()
			: game.getTeamAway();
	}

	private boolean handles(FantasyFootballServer server, String command, Session session) {
		SessionManager sessionManager = server.getSessionManager();
		long gameId = sessionManager.getGameIdForSession(session);
		GameState gameState = server.getGameCache().getGameStateById(gameId);

		return gameState != null && this.commands.contains(command)
			&& requiredClientMode.isMet(sessionManager, gameId, session)
			&& requiredEnvironment.isMet(server, gameState)
			&& (requiresOnePrivilegeOf.isEmpty() || requiresOnePrivilegeOf.stream().anyMatch(requirement -> requirement.isMet(sessionManager, session)));
	}

	abstract void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session);

	protected Player<?>[] findPlayersInCommand(Team pTeam, String[] pCommands) {
		Set<Player<?>> players = new HashSet<>();
		if (ArrayTool.isProvided(pCommands) && (commandPartsThreshold < pCommands.length)) {
			if ("all".equalsIgnoreCase(pCommands[commandPartsThreshold])) {
				Collections.addAll(players, pTeam.getPlayers());
			} else {
				for (int i = commandPartsThreshold; i < pCommands.length; i++) {
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

	protected void putPlayerIntoBox(GameState pGameState, ServerCommunication communication, Player<?> pPlayer, PlayerState pPlayerState, String pBoxName,
																	SeriousInjury pSeriousInjury) {
		Game game = pGameState.getGame();
		PlayerResult playerResult = game.getGameResult().getPlayerResult(pPlayer);
		playerResult.setSeriousInjury(pSeriousInjury);
		playerResult.setSeriousInjuryDecay(null);
		game.getFieldModel().setPlayerState(pPlayer, pPlayerState);
		UtilBox.putPlayerIntoBox(game, pPlayer);
		communication.sendPlayerTalk(pGameState, null, "Player " + pPlayer.getName() + " moved into box " + pBoxName + ".");
	}

	protected void handleSpecs(FantasyFootballServer server, GameState gameState, Session session, boolean issuedBySpec) {
		String[] spectators = findSpectators(server, gameState);
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
		server.getCommunication().sendTalk(session, null, info);
	}

	protected void playSoundAfterCooldown(FantasyFootballServer server, GameState pGameState, String pCoach, SoundId pSound) {
		if ((pGameState != null) && (pCoach != null) && (pSound != null)) {
			if (StringTool.isProvided(server.getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN))) {
				long spectatorCooldown = Long.parseLong(server.getProperty(IServerProperty.SERVER_SPECTATOR_COOLDOWN));
				long currentTime = System.currentTimeMillis();
				if (currentTime > (pGameState.getSpectatorCooldownTime(pCoach) + spectatorCooldown)) {
					server.getCommunication().sendSound(pGameState, pSound);
					pGameState.putSpectatorCooldownTime(pCoach, currentTime);
				}
			} else {
				server.getCommunication().sendSound(pGameState, pSound);
			}
		}
	}

	private String[] findSpectators(FantasyFootballServer server, GameState gameState) {
		List<String> spectatorList = new ArrayList<>();
		SessionManager sessionManager = server.getSessionManager();
		Session[] sessions = sessionManager.getSessionsOfSpectators(gameState.getId());
		for (Session session : sessions) {
			String spectator = sessionManager.getCoachForSession(session);
			if (spectator != null && !sessionManager.isSessionAdmin(session)) {
				spectatorList.add(spectator);
			}
		}
		String[] spectatorArray = spectatorList.toArray(new String[0]);
		Arrays.sort(spectatorArray);
		return spectatorArray;
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
