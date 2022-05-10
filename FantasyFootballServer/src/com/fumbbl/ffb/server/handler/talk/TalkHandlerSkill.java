package com.fumbbl.ffb.server.handler.talk;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPlayer;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.server.FantasyFootballServer;
import com.fumbbl.ffb.server.GameState;
import org.eclipse.jetty.websocket.api.Session;

public abstract class TalkHandlerSkill extends TalkHandler {
	private static final String _ADD = "add";
	private static final String _REMOVE = "remove";

	public TalkHandlerSkill(CommandAdapter commandAdapter, TalkRequirements.Client requiredClient, TalkRequirements.Environment requiredEnv, TalkRequirements.Privilege... requiresOnePrivilegeOf) {
		super("/skill", 3, commandAdapter, requiredClient, requiredEnv, requiresOnePrivilegeOf);
	}

	@Override
	void handle(FantasyFootballServer server, GameState gameState, String[] commands, Team team, Session session) {
		Game game = gameState.getGame();
		if (commands.length <= 3) {
			return;
		}
		Skill skill = gameState.getGame().getRules().getSkillFactory().forName(commands[2].replace('_', ' '));
		if (skill == null) {
			return;
		}

		for (Player<?> player : findPlayersInCommand(team, commands)) {
			if (!(player instanceof RosterPlayer)) {
				continue;
			}
			if (_ADD.equals(commands[1])) {
				((RosterPlayer) player).addSkill(skill);
				server.getCommunication().sendAddPlayer(gameState, team.getId(), (RosterPlayer) player,
					game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
				server.getCommunication().sendPlayerTalk(gameState, null, "Added skill " + skill.getName() + " to player " + player.getName() + ".");
			}
			if (_REMOVE.equals(commands[1])) {
				((RosterPlayer) player).removeSkill(skill);
				server.getCommunication().sendAddPlayer(gameState, team.getId(), (RosterPlayer) player,
					game.getFieldModel().getPlayerState(player), game.getGameResult().getPlayerResult(player));
				String info = "Removed skill " + skill.getName() + " from player " + player.getName() +
					".";
				server.getCommunication().sendPlayerTalk(gameState, null, info);
			}
		}
	}
}
