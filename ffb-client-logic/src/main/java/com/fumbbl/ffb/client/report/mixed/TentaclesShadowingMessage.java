package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportTentaclesShadowingRoll;

@ReportMessageType(ReportId.TENTACLES_SHADOWING_ROLL)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class TentaclesShadowingMessage extends ReportMessageBase<ReportTentaclesShadowingRoll> {

    @Override
    protected void render(ReportTentaclesShadowingRoll report) {
  		StringBuilder status;
  		StringBuilder neededRoll = null;
  		ActingPlayer actingPlayer = game.getActingPlayer();
  		Player<?> defender = game.getPlayerById(report.getDefenderId());
  		if (!report.isReRolled()) {
  			if (report.getSkill().hasSkillProperty(NamedProperties.canFollowPlayerLeavingTacklezones)) {
  				print(getIndent(), true, defender);
  				print(getIndent(), TextStyle.BOLD, " tries to shadow ");
  				print(getIndent(), true, actingPlayer.getPlayer());
  				println(getIndent(), TextStyle.BOLD, ":");
  			}
  			if (report.getSkill().hasSkillProperty(NamedProperties.canHoldPlayersLeavingTacklezones)) {
  				status = new StringBuilder();
  				print(getIndent(), true, defender);
  				print(getIndent(), TextStyle.BOLD, " tries to hold ");
  				print(getIndent(), true, actingPlayer.getPlayer());
  				status.append(" with ").append(defender.getPlayerGender().getGenitive()).append(" tentacles:");
  				println(getIndent(), TextStyle.BOLD, status.toString());
  			}
  		}
  		if (report.getSkill().hasSkillProperty(NamedProperties.canFollowPlayerLeavingTacklezones)) {
  			status = new StringBuilder();
  			status.append("Shadowing Roll [ ").append(report.getRoll()).append(" ]");
  			println(getIndent() + 1, TextStyle.ROLL, status.toString());
  			status = new StringBuilder();
  			if (report.isSuccessful()) {
  				print(getIndent() + 2, false, defender);
  				status.append(" shadows ").append(defender.getPlayerGender().getGenitive()).append(" opponent successfully.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  						.append("+");
  				}
  			} else {
  				print(getIndent() + 2, false, defender);
  				status.append(" fails to shadow ").append(defender.getPlayerGender().getGenitive())
  					.append(" opponent.");
  				println(getIndent() + 2, status.toString());

  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  				}
  			}
  			if (neededRoll != null) {
  				neededRoll.append(" (Roll + MA ").append(defender.getMovementWithModifiers());
  				neededRoll.append(" - MA ").append(actingPlayer.getPlayer().getMovementWithModifiers());
  				neededRoll.append(" >= 6).");
  				println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  			}
  		}
  		if (report.getSkill().hasSkillProperty(NamedProperties.canHoldPlayersLeavingTacklezones)) {
  			status = new StringBuilder();
  			status.append("Tentacles Roll [ ").append(report.getRoll()).append(" ]");
  			println(getIndent() + 1, TextStyle.ROLL, status.toString());
  			status = new StringBuilder();
  			if (report.isSuccessful()) {
  				print(getIndent() + 2, false, defender);
  				status.append(" holds ").append(defender.getPlayerGender().getGenitive()).append(" opponent successfully.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  						.append("+");
  				}
  			} else {
  				print(getIndent() + 2, false, defender);
  				status.append(" failed to hold ").append(defender.getPlayerGender().getGenitive())
  					.append(" opponent.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  				}
  			}
  			if (neededRoll != null) {
  				neededRoll.append(" (Roll + ST ").append(defender.getStrengthWithModifiers());
  				neededRoll.append(" - ST ").append(actingPlayer.getPlayer().getStrengthWithModifiers());
  				neededRoll.append(" >= 6).");
  				println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  			}
  		}
		}
}
