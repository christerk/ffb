package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.factory.PassModifierFactory;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.PassMechanic;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.StatBasedRollModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportNervesOfSteel;
import com.fumbbl.ffb.report.mixed.ReportPassRoll;
import com.fumbbl.ffb.util.UtilPlayer;

@ReportMessageType(ReportId.PASS_ROLL)
@RulesCollection(Rules.BB2025)
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
				if (report.isBomb()) {
					statusReport.report(new ReportNervesOfSteel(player.getId(), report.isBomb()));
				} else {
					statusReport.report(new ReportNervesOfSteel(player.getId(), "pass"));
				}
  		}
  		
  		status.append(mechanic.formatReportRoll(report.getRoll(), thrower));
  		println(getIndent() + 1, TextStyle.ROLL, status.toString());
  		print(getIndent() + 2, false, thrower);
  		PassResult result = report.getResult();
  		if (PassResult.ACCURATE == result || (PassResult.INACCURATE == result && report.isHailMaryPass())) {
  			if (report.isBomb()) {
  				println(getIndent() + 2, " throws the bomb successfully.");
  			} else {
  				println(getIndent() + 2, " passes the ball.");
  			}
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
  			}
  		} else {
  			if (PassResult.SAVED_FUMBLE == result || PassResult.FUMBLE == result) {
  				if (report.isBomb()) {
  					println(getIndent() + 2, " fumbles the bomb.");
  				} else {
  					println(getIndent() + 2, " fumbles the ball.");
  				}
  			} else {
  				println(getIndent() + 2, " misses the throw.");
  			}
  			if (!report.isReRolled()) {
  				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  			}
  		}
  		if (neededRoll != null) {
				boolean ignoreRange = UtilPlayer.isPassingToPartner(thrower, game.getFieldModel().getPlayer(game.getPassCoordinate()));
				String formattedModifiers = statusReport.formatRollModifiers(report.getRollModifiers());
				StatBasedRollModifier statBasedRollModifier = report.getStatBasedRollModifier();
				if (statBasedRollModifier != null) {
					formattedModifiers += " + " + statBasedRollModifier.getModifier() + " " + statBasedRollModifier.getReportString();
				}
				if (ignoreRange) {
					neededRoll.append(mechanic.formatRollRequirement(report.getPassingDistance(), formattedModifiers, thrower));
				} else {
					neededRoll.append(mechanic.formatRollRequirement(report.getPassingDistance(), formattedModifiers, thrower));
				}
				println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
			}
    }
}
