package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportCardDeactivated;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.CARD_DEACTIVATED)
@RulesCollection(Rules.COMMON)
public class CardDeactivatedMessage extends ReportMessageBase<ReportCardDeactivated> {

    public CardDeactivatedMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportCardDeactivated report) {
  		StringBuilder status = new StringBuilder();
  		status.append("Card ").append(report.getCard().getName());
  		status.append(" effect ended.");
  		println(getIndent(), TextStyle.BOLD, status.toString());
    }
}
