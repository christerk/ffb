package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportPickupRoll;

@ReportMessageType(ReportId.PICK_UP_ROLL)
@RulesCollection(Rules.COMMON)
public class PickUpRollMessage extends ReportMessageBase<ReportPickupRoll> {

	@Override
	protected void render(ReportPickupRoll report) {
		StringBuilder status = new StringBuilder();
		StringBuilder neededRoll = null;
		Player<?> player = game.getPlayerById(report.getPlayerId());
		if (!report.isReRolled()) {
			print(getIndent(), true, player);
			println(getIndent(), TextStyle.BOLD, " tries to pick up the ball:");
		}
		status.append("Pickup Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent() + 1, TextStyle.ROLL, status.toString());
		print(getIndent() + 2, false, player);
  		if (report.isSuccessful()) {
  			println(getIndent() + 2, " picks up the ball.");
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			println(getIndent() + 2, " drops the ball.");
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
  			neededRoll.append(mechanic.formatPickupResult(report, player));
  			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
    }
}
