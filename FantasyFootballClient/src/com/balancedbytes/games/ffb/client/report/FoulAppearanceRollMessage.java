package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.FOUL_APPEARANCE_ROLL)
@RulesCollection(Rules.COMMON)
public class FoulAppearanceRollMessage extends ReportMessageBase<ReportSkillRoll> {

    public FoulAppearanceRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportSkillRoll report) {
  		Player<?> player = game.getActingPlayer().getPlayer();
  		StringBuilder status = new StringBuilder();
  		status.append("Foul Appearance Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			println(getIndent() + 1, " resists the Foul Appearance.");
  		} else {
  			println(getIndent() + 1, " cannot overcome the Foul Appearance.");
  		}
    }
}
