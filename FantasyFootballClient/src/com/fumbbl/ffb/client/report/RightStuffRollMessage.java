package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;

@ReportMessageType(ReportId.RIGHT_STUFF_ROLL)
@RulesCollection(Rules.COMMON)
public class RightStuffRollMessage extends ReportMessageBase<ReportSkillRoll> {

    @Override
    protected void render(ReportSkillRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		status.append("Right Stuff Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		Player<?> thrownPlayer = game.getPlayerById(report.getPlayerId());
  		print(getIndent() + 1, false, thrownPlayer);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" lands on ").append(thrownPlayer.getPlayerGender().getGenitive()).append(" feet.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			println(getIndent() + 1, " crashes to the ground.");
  			status = new StringBuilder();
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
  			neededRoll.append(mechanic.formatRightStuffResult(report, thrownPlayer));
  			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
    }
}
