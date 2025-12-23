package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportMascotUsed;

@ReportMessageType(ReportId.MASCOT_USED)
@RulesCollection(RulesCollection.Rules.BB2025)
public class MascotUsedMessage extends ReportMessageBase<ReportMascotUsed> {
	@Override
	protected void render(ReportMascotUsed report) {
		println(getIndent(), TextStyle.ROLL, "Mascot Roll [ " + report.getRoll() + " ]");
		printTeamName(game, false, report.getTeamId());
		StringBuilder builder = new StringBuilder(" used their Team Mascot");
		if (report.isSuccessful()) {
			builder.append(" successfully.");
		} else if (report.isFallback()) {
			builder.append(" but it failed so they used a regular re-roll instead.");
		} else {
			builder.append(" but it failed.");
		}
		println(getIndent(), builder.toString());

		if (!report.isSuccessful()) {
			println(getIndent() + 1, TextStyle.NONE, "(Roll >= " + report.getMinimumRoll() + " to succceed)");
		}
	}
}
