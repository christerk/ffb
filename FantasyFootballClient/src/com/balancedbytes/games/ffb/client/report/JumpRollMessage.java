package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportSkillRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.JUMP_ROLL)
@RulesCollection(Rules.COMMON)
public class JumpRollMessage extends ReportMessageBase<ReportSkillRoll> {

    public JumpRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportSkillRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> player = game.getActingPlayer().getPlayer();
  		status.append("Jump Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent(), TextStyle.ROLL, status.toString());
  		print(getIndent() + 1, false, player);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" jumps over ").append(player.getPlayerGender().getGenitive()).append(" opponents.");
  			println(getIndent() + 1, status.toString());
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			println(getIndent() + 1, " trips while jumping.");
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
  			neededRoll.append(mechanic.formatJumpResult(report, player));
  			println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
		}
}
