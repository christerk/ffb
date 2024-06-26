package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.report.ReportConfusionRoll;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.CONFUSION_ROLL)
@RulesCollection(Rules.COMMON)
public class ConfusionRollMessage extends ReportMessageBase<ReportConfusionRoll> {

    @Override
    protected void render(ReportConfusionRoll report) {
  		if (report.getConfusionSkill() != null) {
  			StringBuilder status = new StringBuilder();
  			StringBuilder neededRoll = null;
  			Player<?> player = game.getActingPlayer().getPlayer();
  			status.append(report.getConfusionSkill().getName()).append(" Roll [ ").append(report.getRoll()).append(" ]");
  			println(getIndent(), TextStyle.ROLL, status.toString());
  			print(getIndent() + 1, false, player);
  			if (report.isSuccessful()) {
  				println(getIndent() + 1, " is able to act normally.");
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  						.append("+");
  				}
  			} else {
  				Skill confusionSkill = report.getConfusionSkill();
  				String confusionMessage = confusionSkill.getConfusionMessage();
  				println(getIndent() + 1, " " + confusionMessage + ".");

  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  				}
  			}
  			if (neededRoll != null) {
  				if (report.getConfusionSkill().hasSkillProperty(NamedProperties.needsToRollForActionButKeepsTacklezone)) {
  					if (report.getMinimumRoll() > 2) {
  						neededRoll.append(" (Player does not attack)");
  					} else {
  						neededRoll.append(" (Player does attack)");
  					}
  				}
  				if (report.getConfusionSkill().hasSkillProperty(NamedProperties.needsToRollHighToAvoidConfusion)) {
  					if (report.getMinimumRoll() > 2) {
  						neededRoll.append(" (Really Stupid player without assistance)");
  					} else {
  						neededRoll.append(" (Really Stupid player gets help from team-mates)");
  					}
  				}
  				neededRoll.append(".");
  				println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  			}
  		}
		}

}
