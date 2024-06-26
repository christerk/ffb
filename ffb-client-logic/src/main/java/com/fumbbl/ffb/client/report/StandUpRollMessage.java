package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportStandUpRoll;

@ReportMessageType(ReportId.STAND_UP_ROLL)
@RulesCollection(Rules.COMMON)
public class StandUpRollMessage extends ReportMessageBase<ReportStandUpRoll> {

    @Override
    protected void render(ReportStandUpRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> player = game.getActingPlayer().getPlayer();
  		status.append("Stand Up Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" stands up.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  					.append("+.");
  			}
  		} else {
  			status = new StringBuilder();
  			status.append(" doesn't get to ").append(player.getPlayerGender().getGenitive()).append(" feet.");
  			println(getIndent() + 1, status.toString());
  			status = new StringBuilder();
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed.");
  			}
  		}
  		if (neededRoll != null) {
  			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
    }
}
