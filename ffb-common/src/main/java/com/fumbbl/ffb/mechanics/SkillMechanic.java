package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;

public abstract class SkillMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.SKILL;
	}

	public abstract boolean eligibleForPro(Game game, Player<?> player, String originalBomberId);

	public abstract boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player);

	public abstract boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate);

	public abstract boolean canPreventStripBall(PlayerState playerState);

	public abstract boolean allowsCancellingGuard(TurnMode turnMode);

	public abstract String calculatePlayerLevel(Game game, Player<?> player);
}
