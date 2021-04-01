package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.PassingDistance;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportThrowTeamMateRoll;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportNervesOfSteel;

@ReportMessageType(ReportId.THROW_TEAM_MATE_ROLL)
@RulesCollection(Rules.COMMON)
public class ThrowTeamMateRollMessage extends ReportMessageBase<ReportThrowTeamMateRoll> {

    public ThrowTeamMateRollMessage(StatusReport statusReport) {
        super(statusReport);
    }

    @Override
    protected void render(ReportThrowTeamMateRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		Player<?> thrower = game.getActingPlayer().getPlayer();
  		Player<?> thrownPlayer = game.getPlayerById(report.getThrownPlayerId());
  		if (!report.isReRolled()) {
  			print(getIndent(), true, thrower);
  			print(getIndent(), TextStyle.BOLD, " tries to throw ");
  			print(getIndent(), true, thrownPlayer);
  			println(getIndent(), TextStyle.BOLD, ":");
  		}
  		PassModifierFactory pmf = game.getFactory(Factory.PASS_MODIFIER);
  		if (report.hasRollModifier(pmf.forName("Nerves of Steel"))) {
  			Player<?> player = game.getActingPlayer().getPlayer();
  			statusReport.report(new ReportNervesOfSteel(player.getId(), "pass"));
  		}
  		status.append("Throw Team-Mate Roll [ ").append(report.getRoll()).append(" ]");
  		println(getIndent() + 1, TextStyle.ROLL, status.toString());
  		print(getIndent() + 2, false, thrower);
  		if (report.isSuccessful()) {
  			status = new StringBuilder();
  			status.append(" throws ").append(thrower.getPlayerGender().getGenitive()).append(" team-mate successfully.");
  			println(getIndent() + 2, status.toString());
  		} else {
  			println(getIndent() + 2, " fumbles the throw.");
  		}
  		if (report.isSuccessful() && !report.isReRolled()) {
  			neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  				.append("+ to avoid a fumble");
  		}
  		if (!report.isSuccessful() && !report.isReRolled()) {
  			neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to avoid a fumble");
  		}
  		if (neededRoll != null) {
  			neededRoll.append(" (Roll ");
  			PassingDistance passingDistance = report.getPassingDistance();
  			if (passingDistance.getModifier2016() >= 0) {
  				neededRoll.append(" + ");
  			} else {
  				neededRoll.append(" - ");
  			}
  			neededRoll.append(Math.abs(passingDistance.getModifier2016())).append(" ").append(passingDistance.getName());
  			neededRoll.append(statusReport.formatRollModifiers(report.getRollModifiers())).append(" > 1).");
  			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
  		setIndent(getIndent() + 1);
    }
}
