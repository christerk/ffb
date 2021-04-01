package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.ALWAYS_HUNGRY_ROLL)
@RulesCollection(Rules.COMMON)
public class AlwaysHungryMessage extends ReportMessageBase<ReportSkillRoll> {

    public AlwaysHungryMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportSkillRoll report) {
  		Player<?> thrower = game.getActingPlayer().getPlayer();
  		StringBuilder status = new StringBuilder();
  		status.append("Always Hungry Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, thrower);
  		status = new StringBuilder();
  		if (report.isSuccessful()) {
  			status.append(" resists the hunger.");
  		} else {
  			status.append(" tries to eat ").append(thrower.getPlayerGender().getGenitive()).append(" team-mate.");
  		}
  		println(getIndent() + 1, TextStyle.NONE, status.toString());    }

}
