package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportKickoffResult;

@ReportMessageType(ReportId.KICKOFF_RESULT)
@RulesCollection(Rules.COMMON)
public class KickoffResultMessage extends ReportMessageBase<ReportKickoffResult> {

    @Override
    protected void render(ReportKickoffResult report) {
  		setIndent(0);
  		StringBuilder status = new StringBuilder();
  		int[] kickoffRoll = report.getKickoffRoll();
  		status.append("Kick-off Event Roll [ ").append(kickoffRoll[0]).append(" ][ ").append(kickoffRoll[1]).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		status.append("Kick-off event is ").append(report.getKickoffResult().getName());
  		println(getIndent() + 1, status.toString());
  		println(getIndent() + 1, TextStyle.EXPLANATION, report.getKickoffResult().getDescription());
  		setIndent(1);
    }
}
