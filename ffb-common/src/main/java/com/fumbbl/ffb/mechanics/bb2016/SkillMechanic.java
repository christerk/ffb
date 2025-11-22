package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.PlayerResult;

@RulesCollection(RulesCollection.Rules.BB2016)
public class SkillMechanic extends com.fumbbl.ffb.mechanics.SkillMechanic {

	@Override
	public boolean eligibleForPro(Game game, Player<?> player, String originalBomberId) {
		return true;
	}

	@Override
	public boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player) {
		return true;
	}

	@Override
	public boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate) {
		return true;
	}

	@Override
	public boolean canPreventStripBall(PlayerState playerState) {
		return true;
	}

	@Override
	public boolean allowsCancellingGuard(TurnMode turnMode) {
		return false;
	}

	@Override
	public String calculatePlayerLevel(Game game, Player<?> player) {
		PlayerResult playerResult = game.getGameResult().getPlayerResult(player);
		int oldSpps = playerResult.getCurrentSpps();
		if (oldSpps > 175) {
			return "Legend";
		} else if (oldSpps > 75) {
			return "Super Star";
		} else if (oldSpps > 50) {
			return "Star";
		} else if (oldSpps > 30) {
			return "Emerging";
		} else if (oldSpps > 15) {
			return "Veteran";
		} else if (oldSpps > 5) {
			return "Experienced";
		} else {
			return "Rookie";
		}
	}
}
