package com.fumbbl.ffb.mechanics.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.property.NamedProperties;

@RulesCollection(RulesCollection.Rules.BB2020)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
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

	@Override
	public boolean eligibleForPro(ActingPlayer actingPlayer, Player<?> player) {
		return actingPlayer.getPlayer() == player;
	}
}
