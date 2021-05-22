package com.fumbbl.ffb.mechanics;

import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
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

	public abstract boolean eligibleForPro(ActingPlayer actingPlayer, Player<?> player);

	public abstract SendToBoxReason raisedByNurgleReason();

	public abstract String raisedByNurgleMessage();

	public abstract boolean allowsTeamReRoll(TurnMode turnMode);

	public abstract int mvpSpp();

	public abstract String[] concessionDialogMessages(boolean legalConcession);
}
