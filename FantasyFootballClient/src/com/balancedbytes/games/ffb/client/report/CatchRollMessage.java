package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.mechanics.AgilityMechanic;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportCatchRoll;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.CATCH_ROLL)
@RulesCollection(Rules.COMMON)
public class CatchRollMessage extends ReportMessageBase<ReportCatchRoll> {

    public CatchRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportCatchRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> player = game.getPlayerById(report.getPlayerId());
  		if (!report.isReRolled()) {
  			print(getIndent(), true, player);
  			if (report.isBomb()) {
  				println(getIndent(), TextStyle.BOLD, " tries to catch the bomb:");
  			} else {
  				println(getIndent(), TextStyle.BOLD, " tries to catch the ball:");
  			}
  		}
  		status.append("Catch Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent() + 1, TextStyle.ROLL, status.toString());
  		print(getIndent() + 2, false, player);
  		if (report.isSuccessful()) {
  			if (report.isBomb()) {
  				println(getIndent() + 2, " catches the bomb.");
  			} else {
  				println(getIndent() + 2, " catches the ball.");
  			}
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			if (report.isBomb()) {
  				println(getIndent() + 2, " drops the bomb.");
  			} else {
  				println(getIndent() + 2, " drops the ball.");
  			}
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
  			neededRoll.append(mechanic.formatCatchResult(report, player));
  			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
		}

}
