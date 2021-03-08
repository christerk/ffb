package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.TurnData;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GameMechanic extends com.balancedbytes.games.ffb.mechanics.GameMechanic {
	@Override
	public void updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRolls(turnData.getReRolls() - 1);
	}
}
