package com.balancedbytes.games.ffb.client.report.bb2016;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.StatusReport;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.client.report.ReportMessageBase;
import com.balancedbytes.games.ffb.client.report.ReportMessageType;
import com.balancedbytes.games.ffb.model.ActingPlayer;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.report.ReportTentaclesShadowingRoll;
import com.balancedbytes.games.ffb.util.ArrayTool;
import com.balancedbytes.games.ffb.report.ReportId;

@ReportMessageType(ReportId.TENTACLES_SHADOWING_ROLL)
@RulesCollection(Rules.BB2016)
public class FumbblResultUploadMessage extends ReportMessageBase<ReportTentaclesShadowingRoll> {

    public FumbblResultUploadMessage(StatusReport statusReport) {
        super(statusReport);
    }

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
  		int rolledTotal = 0;
  		if (ArrayTool.isProvided(report.getRoll())) {
  			rolledTotal = report.getRoll()[0] + report.getRoll()[1];
  		}
  		if (report.getSkill().hasSkillProperty(NamedProperties.canFollowPlayerLeavingTacklezones)) {
  			if (rolledTotal > 0) {
  				status = new StringBuilder();
  				status.append("Shadowing Escape Roll [ ").append(report.getRoll()[0]).append(" ][ ")
  					.append(report.getRoll()[1]).append(" ] = ").append(rolledTotal);
  				println(getIndent() + 1, TextStyle.ROLL, status.toString());
  			}
  			status = new StringBuilder();
  			if (report.isSuccessful()) {
  				print(getIndent() + 2, false, actingPlayer.getPlayer());
  				status.append(" escapes ").append(actingPlayer.getPlayer().getPlayerGender().getGenitive())
  					.append(" opponent.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  						.append("+");
  				}
  			} else {
  				print(getIndent() + 2, false, defender);
  				status.append(" shadows ").append(defender.getPlayerGender().getGenitive()).append(" opponent successfully.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  				}
  			}
  			if (neededRoll != null) {
  				neededRoll.append(" (MA ").append(actingPlayer.getPlayer().getMovementWithModifiers());
  				neededRoll.append(" - MA ").append(defender.getMovementWithModifiers());
  				neededRoll.append(" + Roll > 7).");
  				println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  			}
  		}
  		if (report.getSkill().hasSkillProperty(NamedProperties.canHoldPlayersLeavingTacklezones)) {
  			if (rolledTotal > 0) {
  				status = new StringBuilder();
  				status.append("Tentacles Escape Roll [ ").append(report.getRoll()[0]).append(" ][ ")
  					.append(report.getRoll()[1]).append(" ] = ").append(rolledTotal);
  				println(getIndent() + 1, TextStyle.ROLL, status.toString());
  			}
  			status = new StringBuilder();
  			if (report.isSuccessful()) {
  				print(getIndent() + 2, false, actingPlayer.getPlayer());
  				status.append(" escapes ").append(actingPlayer.getPlayer().getPlayerGender().getGenitive())
  					.append(" opponent.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll())
  						.append("+");
  				}
  			} else {
  				print(getIndent() + 2, false, defender);
  				status.append(" holds ").append(defender.getPlayerGender().getGenitive()).append(" opponent successfully.");
  				println(getIndent() + 2, status.toString());
  				if (!report.isReRolled()) {
  					neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
  				}
  			}
  			if (neededRoll != null) {
  				neededRoll.append(" (ST ").append(actingPlayer.getStrength());
  				neededRoll.append(" - ST ").append(defender.getStrengthWithModifiers());
  				neededRoll.append(" + Roll > 5).");
  				println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
  			}
  		}
    }
}
