package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportQuickSnapRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.QUICK_SNAP_ROLL)
public class QuickSnapRollMessage extends ReportMessageBase<ReportQuickSnapRoll> {
	@Override
	protected void render(ReportQuickSnapRoll report) {
		println(getIndent(), TextStyle.ROLL, "Quick Snap Roll [" + report.getRoll() + "]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		println(getIndent() + 1, TextStyle.NONE, " may move " + report.getAmount() + " open players 1 square each");
	}
}
