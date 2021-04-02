package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;

@ReportMessageType(ReportId.ESCAPE_ROLL)
@RulesCollection(Rules.COMMON)
public class EscapeRollMessage extends ReportMessageBase<ReportSkillRoll> {

    @Override
    protected void render(ReportSkillRoll report) {
  		Player<?> thrownPlayer = game.getPlayerById(report.getPlayerId());
  		StringBuilder status = new StringBuilder();
  		status.append("Escape Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		status = new StringBuilder();
  		print(getIndent() + 1, false, thrownPlayer);
  		if (report.isSuccessful()) {
  			status.append(" manages to wriggle free.");
  		} else {
  			status.append(" disappears in ").append(thrownPlayer.getPlayerGender().getGenitive())
  				.append(" team-mate's stomach.");
  		}
  		println(getIndent() + 1, TextStyle.NONE, status.toString());
    }
}
