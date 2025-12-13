package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportSolidDefenceRoll;

@ReportMessageType(ReportId.SOLID_DEFENCE_ROLL)
@RulesCollection(RulesCollection.Rules.BB2025)
public class SolidDefenceRollMessage extends ReportMessageBase<ReportSolidDefenceRoll> {
	@Override
	protected void render(ReportSolidDefenceRoll report) {
		println(getIndent(), TextStyle.ROLL, "Solid Defence Roll [" + report.getRoll() + "]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		if (report.getAmount() > 1) {
			println(getIndent() + 1, TextStyle.NONE, " may select up to " + report.getAmount() + " players to setup again");
		} else if (report.getAmount() == 1) {
			println(getIndent() + 1, TextStyle.NONE, " may select " + report.getAmount() + " player to setup again");
		} else {
			println(getIndent() + 1, TextStyle.NONE, " have no eligible players, moving on to kick-off");
		}
	}
}
