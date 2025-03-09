package com.fumbbl.ffb.server.handler;

import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.marking.MarkerGenerator;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.net.NetCommandId;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.net.ReceivedCommand;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.net.commands.InternalServerCommandApplyAutomatedPlayerMarkings;
import com.fumbbl.ffb.server.util.UtilServerGame;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ServerCommandHandlerApplyAutomatedPlayerMarkings extends ServerCommandHandler {

	private final MarkerGenerator markerGenerator = new MarkerGenerator();

	protected ServerCommandHandlerApplyAutomatedPlayerMarkings(FantasyFootballServer pServer) {
		super(pServer);
	}

	@Override
	public boolean handleCommand(ReceivedCommand receivedCommand) {
		InternalServerCommandApplyAutomatedPlayerMarkings commandUpdatePlayerMarkings = (InternalServerCommandApplyAutomatedPlayerMarkings) receivedCommand.getCommand();

		AutoMarkingConfig config = commandUpdatePlayerMarkings.getAutoMarkingConfig();
		long gameId = commandUpdatePlayerMarkings.getGameId();
		GameState gameState = getServer().getGameCache().getGameStateById(gameId);

		try {
			Game game = gameState.getGame();

			SessionManager sessionManager = getServer().getSessionManager();
			Session session = receivedCommand.getSession();

			if (config.getMarkings().isEmpty()) {
				config.getMarkings().addAll(AutoMarkingConfig.defaults(game.getRules().getSkillFactory()));
			}

			if (sessionManager.getModeForSession(session) == ClientMode.PLAYER) {
				markForPlayer(gameState, sessionManager, game, config, session);
			} else if (sessionManager.getModeForSession(session) == ClientMode.SPECTATOR || sessionManager.getModeForSession(session) == ClientMode.REPLAY) {
				markForSpecOrReplay(gameState, game, config, session);
			}
		} catch (Throwable e) {
			gameState.getServer().getDebugLog().log(gameState.getId(), e);
		}
		return true;
	}

	@Override
	public NetCommandId getId() {
		return NetCommandId.INTERNAL_APPLY_AUTOMATIC_PLAYER_MARKINGS;
	}


	private void markForSpecOrReplay(GameState gameState, Game game, AutoMarkingConfig config, Session session) {
		List<PlayerMarker> markers = Arrays.stream(game.getPlayers()).map(player -> {
			String marking = markerGenerator.generate(game, player, config, false);
			PlayerMarker playerMarker = new PlayerMarker(player.getId());
			playerMarker.setHomeText(marking);
			return playerMarker;
		}).collect(Collectors.toList());

		gameState.getServer().getCommunication().sendUpdateLocalPlayerMarkers(session, markers);

	}

	private void markForPlayer(GameState gameState, SessionManager sessionManager, Game game, AutoMarkingConfig config, Session session) {
		boolean homeCoach = sessionManager.getSessionOfHomeCoach(game.getId()) == session;
		Team team = homeCoach ? game.getTeamHome() : game.getTeamAway();

		Arrays.stream(game.getPlayers()).forEach(player -> {
			String marking = markerGenerator.generate(game, player, config, team.hasPlayer(player));
			PlayerMarker playerMarker = game.getFieldModel().getPlayerMarker(player.getId());
			if (playerMarker == null) {
				playerMarker = new PlayerMarker(player.getId());
			}
			if (homeCoach) {
				playerMarker.setHomeText(marking);
			} else {
				playerMarker.setAwayText(marking);
			}
			game.getFieldModel().add(playerMarker);
		});

		UtilServerGame.syncGameModel(gameState, null, null, null);
	}
}
