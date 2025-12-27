package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportTeamEvent;

@ReportMessageType(ReportId.TEAM_EVENT)
@RulesCollection(RulesCollection.Rules.BB2025)
public class TeamEventMessage extends ReportMessageBase<ReportTeamEvent> {
	@Override
	protected void render(ReportTeamEvent report) {
		printTeamName(false, report.getTeamId());
		println(getIndent() + 1, TextStyle.NONE, " " + report.getEventMessage());
	}
}
