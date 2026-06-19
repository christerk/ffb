package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportDwarfenWisdomRoll;

@ReportMessageType(ReportId.DWARFEN_WISDOM_ROLL)
@RulesCollection(RulesCollection.Rules.BB2025)
public class DwarfenWisdomRollMessage extends ReportMessageBase<ReportDwarfenWisdomRoll> {

	@Override
	protected void render(ReportDwarfenWisdomRoll report) {
		println(getIndent(), TextStyle.ROLL, "Dwarfen Wisdom Roll [" + report.getRoll() + "]");
		TextStyle teamStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
		print(getIndent() + 1, teamStyle, game.getTeamById(report.getTeamId()).getName());
		if (report.getAmount() > 1) {
			println(getIndent() + 1, TextStyle.NONE, " may select up to " + report.getAmount() + " players to setup again");
		} else if (report.getAmount() == 1) {
			println(getIndent() + 1, TextStyle.NONE, " may select 1 player to setup again");
		} else {
			println(getIndent() + 1, TextStyle.NONE, " have no eligible players, moving on to kick-off");
		}
	}
}
