package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportTeamCaptain;

@ReportMessageType(ReportId.TEAM_CAPTAIN)
@RulesCollection(RulesCollection.Rules.BB2025)
public class TeamCaptainMessage extends ReportMessageBase<ReportTeamCaptain> {
	@Override
	protected void render(ReportTeamCaptain report) {
		println(getIndent(), TextStyle.ROLL, "Team Captain Roll [ " + report.getRoll() + " ]");
		printTeamName(false, report.getTeamId());
		StringBuilder builder = new StringBuilder(" look to their Team Captain for guidance");
		if (report.isSuccessful()) {
			builder.append(" and save the re-roll.");
		} else {
			builder.append(" but nothing happens.");
		}
		println(getIndent(), builder.toString());

		if (!report.isSuccessful()) {
			println(getIndent() + 1, TextStyle.NONE, "(Roll >= " + report.getMinimumRoll() + " to succeed)");
		}
	}
}
