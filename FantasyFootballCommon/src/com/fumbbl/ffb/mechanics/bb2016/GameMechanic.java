package com.fumbbl.ffb.mechanics.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;

import java.util.HashSet;
import java.util.Set;

@RulesCollection(RulesCollection.Rules.BB2016)
public class GameMechanic extends com.fumbbl.ffb.mechanics.GameMechanic {
	private static final Set<TurnMode> modesProhibitingReRolls = new HashSet<TurnMode>() {{
		add(TurnMode.KICKOFF);
		add(TurnMode.PASS_BLOCK);
		add(TurnMode.DUMP_OFF);
	}};

	@Override
	public boolean updateTurnDataAfterReRollUsage(TurnData turnData) {
		turnData.setReRollUsed(true);
		turnData.setReRolls(turnData.getReRolls() - 1);
		return false;
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

	@Override
	public SendToBoxReason raisedByNurgleReason() {
		return SendToBoxReason.NURGLES_ROT;
	}

	@Override
	public String raisedByNurgleMessage() {
		return " has been infected with Nurgle's Rot and will join team ";
	}

	@Override
	public boolean allowsTeamReRoll(TurnMode turnMode) {
		return !modesProhibitingReRolls.contains(turnMode);
	}

	@Override
	public int mvpSpp() {
		return 5;
	}
}
