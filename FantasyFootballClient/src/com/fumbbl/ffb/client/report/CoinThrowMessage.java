package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportCoinThrow;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.COIN_THROW)
@RulesCollection(Rules.COMMON)
public class CoinThrowMessage extends ReportMessageBase<ReportCoinThrow> {

    @Override
    protected void render(ReportCoinThrow report) {
  		setIndent(0);
  		println(getIndent(), TextStyle.BOLD, "The referee throws the coin.");
  		print(getIndent() + 1, "Coach ");
  		if (game.getTeamHome().getCoach().equals(report.getCoach())) {
  			print(getIndent() + 1, TextStyle.HOME, report.getCoach());
  		} else {
  			print(getIndent() + 1, TextStyle.AWAY, report.getCoach());
  		}
  		StringBuilder status = new StringBuilder();
  		status.append(" chooses ").append(report.isCoinChoiceHeads() ? "HEADS." : "TAILS.");
  		println(getIndent() + 1, status.toString());
  		status = new StringBuilder();
  		status.append("Coin throw is ");
  		status.append(report.isCoinThrowHeads() ? "HEADS." : "TAILS.");
  		println(getIndent() + 1, status.toString());
    }
}
