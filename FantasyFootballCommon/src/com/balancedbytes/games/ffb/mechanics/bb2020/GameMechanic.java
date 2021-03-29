package com.balancedbytes.games.ffb.mechanics.bb2020;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.TurnData;
import com.balancedbytes.games.ffb.model.property.NamedProperties;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GameMechanic extends com.balancedbytes.games.ffb.mechanics.GameMechanic {
	@Override
	public void updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRolls(turnData.getReRolls() - 1);
	}

	@Override
	public int minimumLonerRoll(Player<?> player) {
		return player.getSkillIntValue(NamedProperties.hasToRollToUseTeamReroll);
	}

	@Override
	public int minimumProRoll() {
		return 3;
	}
}
