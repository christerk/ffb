package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerSkillsResetUsed extends TalkHandler {

	public TalkHandlerSkillsResetUsed(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient,
		TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/skills_reset_used", 1, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();

		for (Player<?> player : findPlayersInCommand(team, commands)) {
			for (SkillUsageType type : SkillUsageType.values()) {
				player.resetUsedSkills(type, game);
			}
			server.getCommunication().sendPlayerTalk(gameState, null,
				"Reset used skills for player " + player.getName() + ".");
		}
	}
}
