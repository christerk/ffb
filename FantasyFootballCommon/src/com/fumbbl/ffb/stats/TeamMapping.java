package com.fumbbl.ffb.stats;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public enum TeamMapping {
	TEAM {
		@Override
		public Team team(String id, Game game) {
			return game.getTeamById(id);
		}
	},
	TEAM_FOR_PLAYER {
		@Override
		public Team team(String id, Game game) {
			return game.getPlayerById(id).getTeam();
		}
	},
	OPPONENT_TEAM {
		@Override
		public Team team(String id, Game game) {
			return game.getOtherTeam(game.getTeamById(id));
		}
	},
	OPPONENT_TEAM_FOR_PLAYER {
		@Override
		public Team team(String id, Game game) {
			return game.getOtherTeam(game.getPlayerById(id).getTeam());
		}
	};

	public abstract Team team(String id, Game game);
}
