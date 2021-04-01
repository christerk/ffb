package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportCoinThrow;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.COIN_THROW)
@RulesCollection(Rules.COMMON)
public class CoinThrowMessage extends ReportMessageBase<ReportCoinThrow> {

    public CoinThrowMessage(StatusReport statusReport) {
        super(statusReport);
    }

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
