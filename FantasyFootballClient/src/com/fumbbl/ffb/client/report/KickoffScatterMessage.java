package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportKickoffScatter;

@ReportMessageType(ReportId.KICKOFF_SCATTER)
@RulesCollection(Rules.COMMON)
public class KickoffScatterMessage extends ReportMessageBase<ReportKickoffScatter> {

    @Override
    protected void render(ReportKickoffScatter report) {
  		setIndent(0);
  		StringBuilder status = new StringBuilder();
  		status.append("Kick-off Scatter Roll [ ").append(report.getRollScatterDirection()).append(" ]");
  		status.append("[ ").append(report.getRollScatterDistance()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		status.append("The kick will land ");
  		status.append(report.getRollScatterDistance())
  			.append((report.getRollScatterDistance() == 1) ? " square " : " squares ");
  		status.append(report.getScatterDirection().getName().toLowerCase()).append(" of where it was aimed.");
  		println(getIndent() + 1, status.toString());
  		setIndent(1);
    }
}
