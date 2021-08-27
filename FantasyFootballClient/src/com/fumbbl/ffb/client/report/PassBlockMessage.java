package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPassBlock;

@ReportMessageType(ReportId.PASS_BLOCK)
@RulesCollection(Rules.COMMON)
public class PassBlockMessage extends ReportMessageBase<ReportPassBlock> {

    @Override
    protected void render(ReportPassBlock report) {
  		if (!report.isPassBlockAvailable()) {
  			TextStyle textStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
  			println(getIndent(), textStyle, "No pass blockers in range to intercept.");
  		}
    }
}
