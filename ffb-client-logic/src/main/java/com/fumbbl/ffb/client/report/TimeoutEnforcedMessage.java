package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportTimeoutEnforced;

@ReportMessageType(ReportId.TIMEOUT_ENFORCED)
@RulesCollection(Rules.COMMON)
public class TimeoutEnforcedMessage extends ReportMessageBase<ReportTimeoutEnforced> {

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
