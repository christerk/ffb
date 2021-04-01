package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportReceiveChoice;

@ReportMessageType(ReportId.RECEIVE_CHOICE)
@RulesCollection(Rules.COMMON)
public class ReceiveChoiceMessage extends ReportMessageBase<ReportReceiveChoice> {

    public ReceiveChoiceMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportReceiveChoice report) {
  		print(getIndent() + 1, "Team ");
  		printTeamName(game, false, report.getTeamId());
  		println(getIndent() + 1, " is " + (report.isReceiveChoice() ? "receiving." : "kicking."));
    }
}
