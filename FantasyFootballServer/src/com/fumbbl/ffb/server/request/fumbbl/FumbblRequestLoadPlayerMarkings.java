package com.fumbbl.ffb.server.request.fumbbl;

import com.eclipsesource.json.JsonValue;
import com.fumbbl.ffb.ClientMode;
import com.fumbbl.ffb.marking.AutoMarkingConfig;
import com.fumbbl.ffb.marking.MarkerGenerator;
import com.fumbbl.ffb.marking.PlayerMarker;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.IServerProperty;
import com.fumbbl.ffb.server.net.SessionManager;
import com.fumbbl.ffb.server.request.ServerRequest;
import com.fumbbl.ffb.server.request.ServerRequestProcessor;
import com.fumbbl.ffb.server.util.UtilServerGame;
import com.fumbbl.ffb.server.util.UtilServerHttpClient;
import com.fumbbl.ffb.util.StringTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FumbblRequestLoadPlayerMarkings extends ServerRequest {


	private final GameState gameState;
	private final Session session;

	private final MarkerGenerator markerGenerator = new MarkerGenerator();

	public FumbblRequestLoadPlayerMarkings(GameState gameState, Session session) {
		this.gameState = gameState;
		this.session = session;
	}

	@Override
	public void process(ServerRequestProcessor processor) {
		SessionManager sessionManager = processor.getServer().getSessionManager();

		Game game = gameState.getGame();
		AutoMarkingConfig config = new AutoMarkingConfig();

		setRequestUrl(StringTool.bind(processor.getServer().getProperty(IServerProperty.FUMBBL_PLAYER_MARKINGS),
			sessionManager.getCoachForSession(session)));

		try {
 			String response = UtilServerHttpClient.fetchPage(getRequestUrl());
			JsonValue jsonValue = JsonValue.readFrom(response);
			if (jsonValue != null && !jsonValue.isNull()) {
				config.initFrom(game.getRules(), jsonValue);
			}
		} catch (Throwable e) {
			processor.getServer().getDebugLog().log(game.getId(), e);
		}

		try {
			if (config.getMarkings().isEmpty()) {
				config.getMarkings().addAll(AutoMarkingConfig.defaults(game.getRules().getSkillFactory()));
			}

			if (sessionManager.getModeForSession(session) == ClientMode.PLAYER) {
				markForPlayer(sessionManager, game, config);
			} else if (sessionManager.getModeForSession(session) == ClientMode.SPECTATOR || sessionManager.getModeForSession(session) == ClientMode.REPLAY) {
				markForSpecOrReplay(game, config);
			}
		} catch (Throwable e) {
			processor.getServer().getDebugLog().log(game.getId(), e);
		}
	}


	private void markForSpecOrReplay(Game game, AutoMarkingConfig config) {
		List<PlayerMarker> markers = Arrays.stream(game.getPlayers()).map(player -> {
			String marking = markerGenerator.generate(player, config, false);
			PlayerMarker playerMarker = new PlayerMarker(player.getId());
			playerMarker.setHomeText(marking);
			return playerMarker;
		}).collect(Collectors.toList());

		gameState.getServer().getCommunication().sendUpdateLocalPlayerMarkers(session, markers);

	}

	private void markForPlayer(SessionManager sessionManager, Game game, AutoMarkingConfig config) {
		boolean homeCoach = sessionManager.getSessionOfHomeCoach(game.getId()) == session;
		Team team = homeCoach ? game.getTeamHome() : game.getTeamAway();

		Arrays.stream(game.getPlayers()).forEach(player -> {
			String marking = markerGenerator.generate(player, config, team.hasPlayer(player));
			synchronized (game.getFieldModel()) {
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
			}
		});

		UtilServerGame.syncGameModel(gameState, null, null, null);
	}
}
