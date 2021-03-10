package com.balancedbytes.games.ffb.server.step.game.start;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.GameResult;

public class UtilInducementSequence {

	public static int calculateInducementGold(Game pGame, boolean pHome) {
		if (pGame == null) {
			return 0;
		}
		GameResult gameResult = pGame.getGameResult();
		int inducementGoldHome = gameResult.getTeamResultHome().getPettyCashAvailable();
		int inducementGoldAway = gameResult.getTeamResultAway().getPettyCashAvailable();
		int homeTV = gameResult.getTeamResultHome().getTeamValue();
		int awayTV = gameResult.getTeamResultAway().getTeamValue();
		if ((awayTV > homeTV) && ((awayTV - homeTV) > inducementGoldHome)) {
			inducementGoldHome = (awayTV - homeTV);
		}
		if ((homeTV > awayTV) && ((homeTV - awayTV) > inducementGoldAway)) {
			inducementGoldAway = (homeTV - awayTV);
		}
		inducementGoldHome = Math.max(0, inducementGoldHome - gameResult.getTeamResultHome().getPettyCashUsed());
		inducementGoldAway = Math.max(0, inducementGoldAway - gameResult.getTeamResultAway().getPettyCashUsed());
		return (pHome ? inducementGoldHome : inducementGoldAway);
	}

}
