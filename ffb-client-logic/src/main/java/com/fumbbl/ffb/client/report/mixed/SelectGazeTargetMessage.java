package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportSelectGazeTarget;

@ReportMessageType(ReportId.SELECT_GAZE_TARGET)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class SelectGazeTargetMessage extends ReportMessageBase<ReportSelectGazeTarget> {

	@Override
	protected void render(ReportSelectGazeTarget report) {
		Player<?> attacker = game.getPlayerById(report.getAttacker());
		Player<?> defender = game.getPlayerById(report.getDefender());

		print(getIndent(), teamStyleForPlayer(attacker), attacker.getName());
		print(getIndent(), TextStyle.NONE, " targets ");
		print(getIndent(), teamStyleForPlayer(defender), defender.getName());
		println(getIndent(), TextStyle.NONE, ".");
	}

	private TextStyle teamStyleForPlayer(Player<?> player) {
		return game.getTeamHome().hasPlayer(player) ? TextStyle.HOME : TextStyle.AWAY;
	}
}
