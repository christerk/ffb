package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.util.ArrayTool;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class TalkHandlerUsedActions extends TalkHandler {

	private static final Set<PlayerAction> ACTIONS = new HashSet<PlayerAction>() {{
		add(PlayerAction.FOUL);
		add(PlayerAction.BLITZ);
		add(PlayerAction.PASS);
		add(PlayerAction.HAND_OVER);
		add(PlayerAction.THROW_BOMB);
		add(PlayerAction.KICK_TEAM_MATE);
		add(PlayerAction.THROW_TEAM_MATE);
	}};

	public TalkHandlerUsedActions(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/action_used", 2, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		boolean used = Boolean.parseBoolean(commands[1]);

		TurnData turnData = team == game.getTeamHome() ? game.getTurnDataHome() : game.getTurnDataAway();

		for (PlayerAction action : findActions(team, commands)) {
			boolean found = true;

			switch (action) {
				case FOUL:
					turnData.setFoulUsed(used);
					break;
				case BLITZ:
					turnData.setBlitzUsed(used);
					break;
				case PASS:
					turnData.setPassUsed(used);
					break;
				case HAND_OVER:
					turnData.setHandOverUsed(used);
					break;
				case THROW_BOMB:
					turnData.setBombUsed(used);
					break;
				case KICK_TEAM_MATE:
					turnData.setKtmUsed(used);
					break;
				case THROW_TEAM_MATE:
					turnData.setTtmUsed(used);
					break;
				default:
					found = false;
			}

			if (found) {
				String info = "Set " + action.getName() +
					" to " + (used ? "used" : "not used") + " for " + team.getName() + ".";
				server.getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
	}

	protected List<PlayerAction> findActions(Team pTeam, String[] pCommands) {
		List<PlayerAction> actions = new ArrayList<>();
		if (ArrayTool.isProvided(pCommands) && (commandPartsThreshold < pCommands.length)) {
			if ("all".equalsIgnoreCase(pCommands[commandPartsThreshold])) {
				actions.addAll(ACTIONS);
			} else {
				for (int i = commandPartsThreshold; i < pCommands.length; i++) {
					int finalI = i;
					ACTIONS.stream().filter(action -> action.getName().equalsIgnoreCase(pCommands[finalI]))
						.findFirst().ifPresent(actions::add);
				}
			}
		}
		return actions;
	}

}
