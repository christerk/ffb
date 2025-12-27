package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportReceiveChoice;

@ReportMessageType(ReportId.RECEIVE_CHOICE)
@RulesCollection(Rules.COMMON)
public class ReceiveChoiceMessage extends ReportMessageBase<ReportReceiveChoice> {

    @Override
    protected void render(ReportReceiveChoice report) {
  		print(getIndent() + 1, "Team ");
  		printTeamName(false, report.getTeamId());
  		println(getIndent() + 1, " is " + (report.isReceiveChoice() ? "receiving." : "kicking."));
    }
}
