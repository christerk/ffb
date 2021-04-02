package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.FactoryType.Factory;
import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.factory.PassModifierFactory;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.mechanics.PassMechanic;
import com.balancedbytes.games.ffb.mechanics.PassResult;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportNervesOfSteel;
import com.balancedbytes.games.ffb.report.ReportPassRoll;

@ReportMessageType(ReportId.PASS_ROLL)
@RulesCollection(Rules.COMMON)
public class PassRollMessage extends ReportMessageBase<ReportPassRoll> {

    @Override
    protected void render(ReportPassRoll report) {
  		StringBuilder status = new StringBuilder();
  		StringBuilder neededRoll = null;
  		PassMechanic mechanic = (PassMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.PASS.name());
  		Player<?> thrower = game.getPlayerById(report.getPlayerId());
  		if (!report.isReRolled()) {
  			print(getIndent(), true, thrower);
  			Player<?> catcher = game.getFieldModel().getPlayer(game.getPassCoordinate());
  			if (report.isHailMaryPass()) {
  				if (report.isBomb()) {
  					println(getIndent(), TextStyle.BOLD, " throws a Hail Mary bomb:");
  				} else {
  					println(getIndent(), TextStyle.BOLD, " throws a Hail Mary pass:");
  				}
  			} else if (catcher != null) {
  				if (report.isBomb()) {
  					print(getIndent(), TextStyle.BOLD, " throws a bomb at ");
  				} else {
  					print(getIndent(), TextStyle.BOLD, " passes the ball to ");
  				}
  				print(getIndent(), true, catcher);
  				println(getIndent(), TextStyle.BOLD, ":");
  			} else {
  				if (report.isBomb()) {
  					println(getIndent(), TextStyle.BOLD, " throws a bomb to an empty field:");
  				} else {
  					println(getIndent(), TextStyle.BOLD, " passes the ball to an empty field:");
  				}
  			}
  		}
  		PassModifierFactory pmf = game.getFactory(Factory.PASS_MODIFIER);
  		if (report.hasRollModifier(pmf.forName("Nerves of Steel"))) {
  			Player<?> player = game.getActingPlayer().getPlayer();
  			statusReport.report(new ReportNervesOfSteel(player.getId(), "pass"));
  		}
  		
  		status.append(mechanic.formatReportRoll(report.getRoll(), thrower));
  		println(getIndent() + 1, TextStyle.ROLL, status.toString());
  		print(getIndent() + 2, false, thrower);
  		PassResult result = report.getResult();
  		if (PassResult.ACCURATE == result) {
  			if (report.isBomb()) {
  				println(getIndent() + 2, " throws the bomb successfully.");
  			} else {
  				println(getIndent() + 2, " passes the ball.");
  			}
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			if (PassResult.SAVED_FUMBLE == result) {
  				println(getIndent() + 2, " holds on to the ball.");
  			} else if (PassResult.FUMBLE == result) {
  				if (report.isBomb()) {
  					println(getIndent() + 2, " fumbles the bomb.");
  				} else {
  					println(getIndent() + 2, " fumbles the ball.");
  				}
  			} else if (PassResult.WILDLY_INACCURATE == result) {
  				println(getIndent() + 2, " lets the throw deviate.");
  			} else {
  				println(getIndent() + 2, " misses the throw.");
  			}
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
  			if (!report.isHailMaryPass()) {
  				neededRoll.append(mechanic.formatRollRequirement(report.getPassingDistance(), statusReport.formatRollModifiers(report.getRollModifiers()), thrower));
  			}
  			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  		}
    }
}
