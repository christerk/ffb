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

@ReportMessageType(ReportId.JUMP_UP_ROLL)
@RulesCollection(Rules.COMMON)
public class JumpUpRollMessage extends ReportMessageBase<ReportSkillRoll> {

    @Override
    protected void render(ReportSkillRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> player = game.getActingPlayer().getPlayer();
  		status.append("Jump Up Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" jumps up to block ").append(player.getPlayerGender().getGenitive()).append(" opponent.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			status = new StringBuilder();
  			status.append(" doesn't get to ").append(player.getPlayerGender().getGenitive()).append(" feet.");
  			println(getIndent() + 1, status.toString());
  			status = new StringBuilder();
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
  			neededRoll.append(mechanic.formatJumpUpResult(report, player));
  			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
    }
}
