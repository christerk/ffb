package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportBrilliantCoachingReRollsLost;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.BRILLIANT_COACHING_REROLLS_LOST)
public class BrilliantCoachingReRollsLostMessage extends ReportMessageBase<ReportBrilliantCoachingReRollsLost> {

	@Override
	protected void render(ReportBrilliantCoachingReRollsLost report) {
		Team team = game.getTeamById(report.getTeamId());
		TextStyle teamStyle = game.getTeamHome() == team ? TextStyle.HOME : TextStyle.AWAY;

		print(getIndent() + 1, teamStyle, team.getName());

		StringBuilder builder = new StringBuilder(" lose ");
		if (report.getAmount() == 1) {
			builder.append("1 Brilliant Coaching Re-Roll as it was");
		} else {
			builder.append(report.getAmount()).append(" Brilliant Coaching Re-Rolls as they were");
		}
		builder.append(" not used in this drive.");

		println(getIndent() + 1, TextStyle.NONE, builder.toString());
	}
}
