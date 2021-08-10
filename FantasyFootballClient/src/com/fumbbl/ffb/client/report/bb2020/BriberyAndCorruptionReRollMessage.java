package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportBriberyAndCorruptionReRoll;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.BRIBERY_AND_CORRUPTION_RE_ROLL)
public class BriberyAndCorruptionReRollMessage extends ReportMessageBase<ReportBriberyAndCorruptionReRoll> {
	@Override
	protected void render(ReportBriberyAndCorruptionReRoll report) {
		Team team = game.getTeamById(report.getTeamId());
		TextStyle teamStyle = game.getTeamHome() == team ? TextStyle.HOME_BOLD : TextStyle.AWAY_BOLD;
		print(getIndent(), teamStyle, team.getName());
		switch (report.getAction()) {
			case USED:
				println(getIndent(), " use Bribery and Corruption to re-roll their Argue the Call roll.");
				break;
			case ADDED:
				println(getIndent(), " may re-roll a natural 1 on an Argue the Call roll once in this game due to Bribery and Corruption.");
				break;
			case WASTED:
				println(getIndent(), " have no use for their Bribery and Corruption as the coach was banned for more than one argue.");
				break;
		}
	}
}
