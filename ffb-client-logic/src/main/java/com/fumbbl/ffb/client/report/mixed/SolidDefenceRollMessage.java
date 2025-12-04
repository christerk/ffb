package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportSolidDefenceRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.SOLID_DEFENCE_ROLL)
@RulesCollection(RulesCollection.Rules.BB2025)
public class SolidDefenceRollMessage extends ReportMessageBase<ReportSolidDefenceRoll> {
	@Override
	protected void render(ReportSolidDefenceRoll report) {
		println(getIndent(), TextStyle.ROLL, "Solid Defence Roll [" + report.getRoll() + "]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		println(getIndent() + 1, TextStyle.NONE, " may reorganize " + report.getAmount() + " players");
		println(getIndent() + 2, TextStyle.EXPLANATION, "Numbers mark original player positions.");
	}
}
