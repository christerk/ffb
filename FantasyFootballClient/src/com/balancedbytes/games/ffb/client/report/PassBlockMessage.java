package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportPassBlock;

@ReportMessageType(ReportId.PASS_BLOCK)
@RulesCollection(Rules.COMMON)
public class PassBlockMessage extends ReportMessageBase<ReportPassBlock> {

    public PassBlockMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportPassBlock report) {
  		if (!report.isPassBlockAvailable()) {
  			TextStyle textStyle = game.getTeamHome().getId().equals(report.getTeamId()) ? TextStyle.HOME : TextStyle.AWAY;
  			println(getIndent(), textStyle, "No pass blockers in range to intercept.");
  		}
    }
}
