package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportKickoffTimeout;

@ReportMessageType(ReportId.KICKOFF_TIMEOUT)
@RulesCollection(Rules.BB2020)
public class KickoffTimeoutMessage extends ReportMessageBase<ReportKickoffTimeout> {

	@Override
	protected void render(ReportKickoffTimeout report) {
		StringBuilder status = new StringBuilder();
		status.append("Timeout in turn ").append(report.getTurnNumber()).append(" of ");
		print(getIndent(), TextStyle.NONE, status.toString());
		if (game.isHomePlaying()) {
			println(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
		} else {
			println(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
		}
		if (report.getTurnModifier() < 0) {
			println(getIndent() + 1, "The referee adjusts the clock back.");
			status = new StringBuilder();
			status.append("Turn Counter is moved ").append(Math.abs(report.getTurnModifier()));
			status.append(" step backward.");
			println(getIndent() + 1, status.toString());
		} else {
			println(getIndent() + 1, "The referee does not stop the clock.");
			status = new StringBuilder();
			status.append("Turn Counter is moved ").append(Math.abs(report.getTurnModifier()));
			status.append(" step forward.");
			println(getIndent() + 1, status.toString());
		}
	}
}
