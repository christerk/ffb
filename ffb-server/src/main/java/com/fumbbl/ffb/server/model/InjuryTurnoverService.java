package com.fumbbl.ffb.server.model;

import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionString;
import com.fumbbl.ffb.util.UtilPlayer;

public class InjuryTurnoverService {

	public boolean endsTurn(Game game, DropPlayerContext dropPlayerContext, InjuryContext injuryContext) {
		InjuryTurnoverRule rule = dropPlayerContext.getInjuryTurnoverRule();
		if (rule == null || rule == InjuryTurnoverRule.FIXED) {
			return dropPlayerContext.isEndTurn();
		}
		String chainsawOption = game.getOptions().getOptionWithDefault(GameOptionId.CHAINSAW_TURNOVER).getValueAsString();
		Player<?> player = game.getPlayerById(dropPlayerContext.getPlayerId());
		boolean hasBall = player != null && UtilPlayer.hasBall(game, player);
		boolean kickBack = GameOptionString.CHAINSAW_TURNOVER_KICKBACK.equals(chainsawOption);
		boolean never = GameOptionString.CHAINSAW_TURNOVER_NEVER.equalsIgnoreCase(chainsawOption);
		if (injuryContext.isArmorBroken()) {
			return hasBall || !never;
		}
		return kickBack || (rule == InjuryTurnoverRule.CHAINSAW_FOUL && hasBall);
	}
}
