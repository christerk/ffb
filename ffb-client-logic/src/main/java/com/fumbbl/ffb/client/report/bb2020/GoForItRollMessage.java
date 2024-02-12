package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@ReportMessageType(ReportId.GO_FOR_IT_ROLL)
@RulesCollection(Rules.BB2020)
public class GoForItRollMessage extends ReportMessageBase<ReportSkillRoll> {

	@Override
	protected void render(ReportSkillRoll report) {
		StringBuilder status = new StringBuilder();
		StringBuilder neededRoll = null;
		Player<?> player = game.getActingPlayer().getPlayer();
		status.append("Rush Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		print(getIndent() + 1, false, player);
		if (report.isSuccessful()) {
			println(getIndent() + 1, " rushes!");
			if (!report.isReRolled()) {
				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
			}
		} else {
			println(getIndent() + 1, " trips while rushing.");
			if (!report.isReRolled()) {
				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
			}
		}
		if (neededRoll != null) {
			neededRoll.append(" (Roll").append(statusReport.formatRollModifiers(report.getRollModifiers())).append(" > ")
				.append(report.getMinimumRoll() - 1).append(").");
			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
		}
	}
}
