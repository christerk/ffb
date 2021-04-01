package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportTimeoutEnforced;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.TIMEOUT_ENFORCED)
@RulesCollection(Rules.COMMON)
public class TimeoutEnforcedMessage extends ReportMessageBase<ReportTimeoutEnforced> {

    public TimeoutEnforcedMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportTimeoutEnforced report) {
  		StringBuilder status = new StringBuilder();
  		status.append("Coach ").append(report.getCoach()).append(" forces a Timeout.");
  		if (game.getTeamHome().getCoach().equals(report.getCoach())) {
  			println(ParagraphStyle.SPACE_ABOVE, TextStyle.HOME_BOLD, status.toString());
  		} else {
  			println(ParagraphStyle.SPACE_ABOVE, TextStyle.AWAY_BOLD, status.toString());
  		}
  		println(ParagraphStyle.SPACE_BELOW, TextStyle.NONE,
  			"The turn will end after the Acting Player has finished moving.");
    }
}
