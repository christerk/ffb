package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Roster;
import com.fumbbl.ffb.model.TurnData;

public abstract class GameMechanic implements Mechanic {
	@Override
	public Type getType() {
		return Type.GAME;
	}

	/**
	 * @return true if the re-roll used was only available in the current drive
	 */
	public abstract boolean updateTurnDataAfterReRollUsage(TurnData turnData);

	public abstract int minimumLonerRoll(Player<?> player);

	public abstract int minimumProRoll();

	public abstract boolean eligibleForPro(Game game, Player<?> player);

	public abstract SendToBoxReason raisedByNurgleReason();

	public abstract String raisedByNurgleMessage();

	public abstract boolean allowsTeamReRoll(TurnMode turnMode);

	public abstract int mvpSpp();

	public abstract String[] concessionDialogMessages(boolean legalConcession);

	public abstract boolean isValidAssist(boolean usingMultiBlock, FieldModel fieldModel, Player<?> player);

	public abstract boolean isValidPushbackSquare(FieldModel fieldModel, FieldCoordinate coordinate);

	public abstract boolean canRaiseDead(Roster roster);

	public abstract boolean canPreventStripBall(PlayerState playerState);

	public abstract boolean isFoulActionAllowed(TurnMode turnMode);

	public abstract boolean isBombActionAllowed(TurnMode turnMode);

	public abstract boolean isGazeActionAllowed(TurnMode turnMode);

	public abstract boolean isKickTeamMateActionAllowed(TurnMode turnMode);

	public abstract boolean areSpecialBlockActionsAllowed(TurnMode turnMode);

	public abstract boolean allowesCancellingGuard(TurnMode turnMode);

	public abstract boolean isBlockActionAllowed(TurnMode turnMode);
}
