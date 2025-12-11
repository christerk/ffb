package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportBlitzRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.BLITZ_ROLL)
public class BlitzRollMessage extends ReportMessageBase<ReportBlitzRoll> {
	@Override
	protected void render(ReportBlitzRoll report) {
		println(getIndent(), TextStyle.ROLL, "Blitz Roll [ " + report.getRoll() + " ]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		println(getIndent() + 1, TextStyle.NONE, " may activate " + report.getAmount() + " open players");
	}
}
