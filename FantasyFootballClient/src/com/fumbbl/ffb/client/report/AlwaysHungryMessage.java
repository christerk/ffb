package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@ReportMessageType(ReportId.ALWAYS_HUNGRY_ROLL)
@RulesCollection(Rules.COMMON)
public class AlwaysHungryMessage extends ReportMessageBase<ReportSkillRoll> {

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
