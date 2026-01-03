package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportGettingEvenRoll;

@ReportMessageType(ReportId.GETTING_EVEN_ROLL)
@RulesCollection(Rules.BB2025)
public class GettingEvenRollMessage extends ReportMessageBase<ReportGettingEvenRoll> {

    @Override
    protected void render(ReportGettingEvenRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		status.append("Getting Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
	    status = new StringBuilder();
	    if (report.isSuccessful()) {
  			status.append(" gains hatred towards players of type '").append(report.getKeyword().getName()).append("'.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
				status.append(" remains peaceful towards players of type '").append(report.getKeyword().getName()).append("'.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
		}
}
