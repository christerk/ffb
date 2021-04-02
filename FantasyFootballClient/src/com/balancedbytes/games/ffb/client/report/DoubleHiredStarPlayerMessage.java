package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportDoubleHiredStarPlayer;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.DOUBLE_HIRED_STAR_PLAYER)
@RulesCollection(Rules.COMMON)
public class DoubleHiredStarPlayerMessage extends ReportMessageBase<ReportDoubleHiredStarPlayer> {

    @Override
    protected void render(ReportDoubleHiredStarPlayer report) {
  		String status = "Star Player " + report.getStarPlayerName() +
  				" takes money from both teams and plays for neither.";
  			println(getIndent(), TextStyle.BOLD, status);
    }
}
