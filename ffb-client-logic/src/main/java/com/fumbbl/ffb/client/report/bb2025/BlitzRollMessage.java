package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportBlitzRoll;

@ReportMessageType(ReportId.BLITZ_ROLL)
@RulesCollection(RulesCollection.Rules.BB2025)
public class BlitzRollMessage extends ReportMessageBase<ReportBlitzRoll> {
	@Override
	protected void render(ReportBlitzRoll report) {
		println(getIndent(), TextStyle.ROLL, "Charge! Roll [ " + report.getRoll() + " ]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		println(getIndent() + 1, TextStyle.NONE, " may select " + report.getAmount() + " open players to perform actions.");
	}
}
