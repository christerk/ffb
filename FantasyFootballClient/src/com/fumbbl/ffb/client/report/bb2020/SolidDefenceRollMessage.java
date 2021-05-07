package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportSolidDefenceRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.SOLID_DEFENCE_ROLL)
public class SolidDefenceRollMessage extends ReportMessageBase<ReportSolidDefenceRoll> {
	@Override
	protected void render(ReportSolidDefenceRoll report) {
		println(getIndent(), TextStyle.ROLL, "Solid Defence Roll [" + report.getRoll() + "]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		println(getIndent() + 1, TextStyle.NONE, " may reorganize " + report.getAmount() + " players");
	}
}
