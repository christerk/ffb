package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.REGENERATION_ROLL)
@RulesCollection(Rules.COMMON)
public class RegenerationRollMessage extends ReportMessageBase<ReportSkillRoll> {

    public RegenerationRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportSkillRoll report) {
  		if (report.getRoll() > 0) {
  			println(getIndent(), TextStyle.ROLL, "Regeneration Roll [ " + report.getRoll() + " ]");
  			Player<?> player = game.getPlayerById(report.getPlayerId());
  			print(getIndent() + 1, false, player);
  			if (report.isSuccessful()) {
  				println(getIndent() + 1, " regenerates.");
  			} else {
  				println(getIndent() + 1, " does not regenerate.");
  			}
  		}
    }
}
