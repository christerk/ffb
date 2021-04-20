package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.ReRollSources;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportReRoll;

@ReportMessageType(ReportId.RE_ROLL)
@RulesCollection(Rules.COMMON)
public class ReRollMessage extends ReportMessageBase<ReportReRoll> {

    @Override
    protected void render(ReportReRoll report) {
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		StringBuilder status = new StringBuilder();
  		if (ReRollSources.LONER == report.getReRollSource()) {
  			status.append("Loner Roll [ ").append(report.getRoll()).append(" ]");
  			println(getIndent() + 1, TextStyle.ROLL, status.toString());
  			print(getIndent() + 2, false, player);
  			if (report.isSuccessful()) {
  				println(getIndent() + 2, " may use a Team Re-Roll.");
  			} else {
  				println(getIndent() + 2, " wastes a Team Re-Roll.");
  			}
  		} else if (ReRollSources.PRO == report.getReRollSource()) {
  			status.append("Pro Roll [ ").append(report.getRoll()).append(" ]");
  			println(getIndent() + 1, TextStyle.ROLL, status.toString());
  			print(getIndent() + 2, false, player);
  			status = new StringBuilder();
  			if (report.isSuccessful()) {
  				status.append("'s Pro skill allows ").append(player.getPlayerGender().getDative())
  					.append(" to re-roll the action.");
  			} else {
  				status.append("'s Pro skill does not help ").append(player.getPlayerGender().getDative()).append(".");
  			}
  			println(getIndent() + 2, status.toString());
  		} else {
  			status.append("Re-Roll using ").append(report.getReRollSource().getName().toUpperCase());
  			println(getIndent() + 1, status.toString());
  		}
    }
}
