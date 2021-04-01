package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.ParagraphStyle;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportStartHalf;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.START_HALF)
@RulesCollection(Rules.COMMON)
public class StartHalfMessage extends ReportMessageBase<ReportStartHalf> {

    public StartHalfMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportStartHalf report) {
  		StringBuilder status = new StringBuilder();
  		status.append("Starting ");
  		if (report.getHalf() > 2) {
  			status.append("Overtime");
  		} else if (report.getHalf() > 1) {
  			status.append("2nd half");
  		} else {
  			status.append("1st half");
  		}
  		println(ParagraphStyle.SPACE_ABOVE_BELOW, TextStyle.TURN, status.toString());
    }
}
