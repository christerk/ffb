package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.report.ReportDoubleHiredStarPlayer;
import com.fumbbl.ffb.report.ReportId;

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
