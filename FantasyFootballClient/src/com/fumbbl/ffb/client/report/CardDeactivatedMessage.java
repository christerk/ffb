package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportCardDeactivated;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.CARD_DEACTIVATED)
@RulesCollection(Rules.COMMON)
public class CardDeactivatedMessage extends ReportMessageBase<ReportCardDeactivated> {

    @Override
    protected void render(ReportCardDeactivated report) {
  		StringBuilder status = new StringBuilder();
  		status.append("Card ").append(report.getCard().getName());
  		status.append(" effect ended.");
  		println(getIndent(), TextStyle.BOLD, status.toString());
    }
}
