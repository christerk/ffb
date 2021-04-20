package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;

@RulesCollection(RulesCollection.Rules.BB2016)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
	@Override
	public void updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRollUsed(true);
		turnData.setReRolls(turnData.getReRolls() - 1);
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return 4;
	}

	@Override
	public int minimumProRoll() {
		return 4;
	}

	@Override
	public boolean eligibleForPro(ActingPlayer actingPlayer, Player<?> player) {
		return true;
	}
}
