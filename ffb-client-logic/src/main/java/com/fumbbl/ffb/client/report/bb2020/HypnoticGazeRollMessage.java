package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.mechanics.AgilityMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportHypnoticGazeRoll;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.HYPNOTIC_GAZE_ROLL)
@RulesCollection(Rules.BB2020)
public class HypnoticGazeRollMessage extends ReportMessageBase<ReportHypnoticGazeRoll> {

	@Override
	protected void render(ReportHypnoticGazeRoll report) {
		StringBuilder status = new StringBuilder();
		StringBuilder neededRoll = null;
		Player<?> player = game.getActingPlayer().getPlayer();
		if (!report.isReRolled()) {
			Player<?> defender;
			if (StringTool.isProvided(report.getDefenderId())) {
				defender = game.getPlayerById(report.getDefenderId());
			} else {
				defender = game.getDefender();
			}

			print(getIndent(), true, player);
			print(getIndent(), TextStyle.BOLD, " gazes upon ");
			print(getIndent(), true, defender);
			println(getIndent(), TextStyle.BOLD, ":");
		}
		status.append("Hypnotic Gaze Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent() + 1, TextStyle.ROLL, status.toString());
		print(getIndent() + 2, false, player);
		status = new StringBuilder();
		if (report.isSuccessful()) {
			status.append(" hypnotizes ").append(player.getPlayerGender().getGenitive()).append(" victim.");
			println(getIndent() + 2, status.toString());
			if (!report.isReRolled()) {
				neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll()).append("+");
			}
		} else {
			status.append(" fails to affect ").append(player.getPlayerGender().getGenitive()).append(" victim.");
			println(getIndent() + 2, status.toString());
			if (!report.isReRolled()) {
				neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll()).append("+ to succeed");
			}
		}
		if (neededRoll != null) {
			AgilityMechanic mechanic = (AgilityMechanic) game.getRules().getFactory(Factory.MECHANIC).forName(Mechanic.Type.AGILITY.name());
			neededRoll.append(mechanic.formatHypnoticGazeResult(report, player));
			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
		}
	}
}
