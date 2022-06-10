package com.fumbbl.ffb.util;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.model.FieldModel;
import com.fumbbl.ffb.model.Player;

public class StateValidator {

	public boolean canBeMovedDuringSetUp(Player<?> player, FieldModel fieldModel) {
		FieldCoordinate coordinate = fieldModel.getPlayerCoordinate(player);
		PlayerState playerState = fieldModel.getPlayerState(player);
		return !coordinate.isBoxCoordinate() || (!playerState.isCasualty() && playerState.getBase() != PlayerState.KNOCKED_OUT && playerState.getBase() != PlayerState.BANNED);
	}

}
