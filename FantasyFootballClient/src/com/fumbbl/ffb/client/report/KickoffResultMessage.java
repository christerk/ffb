package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportKickoffResult;
import com.fumbbl.ffb.util.ArrayTool;

@ReportMessageType(ReportId.KICKOFF_RESULT)
@RulesCollection(Rules.COMMON)
public class KickoffResultMessage extends ReportMessageBase<ReportKickoffResult> {

    @Override
    protected void render(ReportKickoffResult report) {
			setIndent(0);
			StringBuilder status = new StringBuilder();
			int[] kickoffRoll = report.getKickoffRoll();
			if (ArrayTool.isProvided(kickoffRoll)) {
				status.append("Kick-off Event Roll [ ").append(kickoffRoll[0]).append(" ][ ").append(kickoffRoll[1]).append(" ]");
			} else {
				status.append("Chosen kick-off event");
			}

			println(getIndent(), TextStyle.ROLL, status.toString());
			status = new StringBuilder();
			status.append("Kick-off event is ").append(report.getKickoffResult().getName());
			println(getIndent() + 1, status.toString());
			println(getIndent() + 1, TextStyle.EXPLANATION, report.getKickoffResult().getDescription());
			setIndent(1);
		}
}
