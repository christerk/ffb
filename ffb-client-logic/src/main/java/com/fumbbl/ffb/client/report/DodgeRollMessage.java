package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.DodgeModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillRoll;
import com.fumbbl.ffb.util.UtilCards;

import java.util.Arrays;

@ReportMessageType(ReportId.DODGE_ROLL)
@RulesCollection(Rules.COMMON)
public class DodgeRollMessage extends ReportMessageBase<ReportSkillRoll> {

    @Override
    protected void render(ReportSkillRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
			Player<?> player = game.getPlayerById(report.getPlayerId());
			if (report.getRoll() > 0) {
				status.append("Dodge Roll [ ").append(report.getRoll()).append(" ]");
			} else {
				status.append("New Dodge Result");
			}
			println(getIndent(), TextStyle.ROLL, status.toString());
			if (!report.isReRolled()) {
  			if (UtilCards.hasUncanceledSkillWithProperty(player, NamedProperties.ignoreTacklezonesWhenDodging)) {
  				print(getIndent() + 1, false, player);
  				println(getIndent() + 1, " is Stunty and ignores tacklezones.");
  			}
  			if (Arrays.stream(report.getRollModifiers()).anyMatch(modifier -> modifier instanceof DodgeModifier && ((DodgeModifier) modifier).isUseStrength())) {
  				print(getIndent() + 1, false, player);
  				println(getIndent() + 1, " uses Break Tackle to break free.");
  			}
  		}
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" dodges successfully.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			println(getIndent() + 1, " trips while dodging.");
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
  			neededRoll.append(mechanic.formatDodgeResult(report, player));
  			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
    }

}
