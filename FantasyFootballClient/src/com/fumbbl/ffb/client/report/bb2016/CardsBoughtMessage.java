package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2016.ReportCardsBought;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.CARDS_BOUGHT)
@RulesCollection(Rules.COMMON)
public class CardsBoughtMessage extends ReportMessageBase<ReportCardsBought> {

    @Override
    protected void render(ReportCardsBought report) {
  		if (!statusReport.fCardsBoughtReportReceived) {
  			statusReport.fCardsBoughtReportReceived = true;
  			println(getIndent(), TextStyle.BOLD, "Buy Cards");
  		}
  		print(getIndent() + 1, "Team ");
  		if (game.getTeamHome().getId().equals(report.getTeamId())) {
  			print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
  		} else {
  			print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
  		}
  		StringBuilder status = new StringBuilder();
  		status.append(" buys ");
  		if (report.getNrOfCards() == 0) {
  			status.append("no Cards.");
  		} else {
  			if (report.getNrOfCards() == 1) {
  				status.append("1 Card");
  			} else {
  				status.append(report.getNrOfCards()).append(" Cards");
  			}
  			status.append(" for ").append(StringTool.formatThousands(report.getGold())).append(" gold total.");
  		}
  		println(getIndent() + 1, status.toString());
    }
}
