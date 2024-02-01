package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.ParagraphStyle;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportStartHalf;

@ReportMessageType(ReportId.START_HALF)
@RulesCollection(Rules.COMMON)
public class StartHalfMessage extends ReportMessageBase<ReportStartHalf> {

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
