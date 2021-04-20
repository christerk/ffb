package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPassDeviate;

@ReportMessageType(ReportId.PASS_DEVIATE)
@RulesCollection(Rules.COMMON)
public class PassDeviateMessage extends ReportMessageBase<ReportPassDeviate> {

    @Override
    protected void render(ReportPassDeviate report) {
  		setIndent(0);
  		StringBuilder status = new StringBuilder();
  		status.append("Pass Deviates [ ").append(report.getRollScatterDirection()).append(" ]");
  		status.append("[ ").append(report.getRollScatterDistance()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		status.append("The ball will land ");
  		status.append(report.getRollScatterDistance())
  			.append((report.getRollScatterDistance() == 1) ? " square " : " squares ");
  		status.append(report.getScatterDirection().getName().toLowerCase()).append(" from the passer.");
  		println(getIndent() + 1, status.toString());
  		setIndent(1);
    }
}
