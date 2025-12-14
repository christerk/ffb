package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.mechanics.PassResult;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportThrowTeamMateRoll;

@ReportMessageType(ReportId.THROW_TEAM_MATE_ROLL)
@RulesCollection(Rules.BB2025)
public class ThrowTeamMateRollMessage extends ReportMessageBase<ReportThrowTeamMateRoll> {

	@Override
	protected void render(ReportThrowTeamMateRoll report) {
		StringBuilder status = new StringBuilder();
		StringBuilder neededRoll = null;
		Player<?> thrower = game.getActingPlayer().getPlayer();
		Player<?> thrownPlayer = game.getPlayerById(report.getThrownPlayerId());
		boolean canThrow = thrower.getPassing() > 0;
		if (!report.isReRolled()) {
			print(getIndent(), true, thrower);
			if (report.isKick()) {
				print(getIndent(), TextStyle.BOLD, " tries to kick ");
			} else {
				print(getIndent(), TextStyle.BOLD, " tries to throw ");
			}
			print(getIndent(), true, thrownPlayer);
			println(getIndent(), TextStyle.BOLD, ":");
		}

		if (report.isKick()) {
			status.append("Kick Team-Mate Roll [ ").append(report.getRoll()).append(" ]");
		} else {
			status.append("Throw Team-Mate Roll [ ").append(report.getRoll()).append(" ]");
		}
		println(getIndent() + 1, TextStyle.ROLL, status.toString());
		print(getIndent() + 2, false, thrower);
		if (report.isSuccessful()) {
			if (report.isKick()) {
				print(getIndent() + 2, " kicks ");
			} else {
				print(getIndent() + 2, " throws ");
			}
			print(getIndent() + 2, false, thrownPlayer);
			status = new StringBuilder(" ");
			if (report.getPassResult() == PassResult.ACCURATE) {
				status.append("superbly");
			} else {
				status.append("with a subpar result");
			}
			status.append(".");
			println(getIndent() + 2, status.toString());
		} else {
			print(getIndent() + 2, " fumbles ");
			print(getIndent() + 2, false, thrownPlayer);
			println(getIndent() + 2, ".");
		}
		if (report.isSuccessful() && !report.isReRolled() && canThrow) {
			neededRoll = new StringBuilder().append("Succeeded on a roll of ").append(report.getMinimumRoll());
			if (report.isKick()) {
				neededRoll.append("+ to avoid a Fumbled Kick");
			} else {
				neededRoll.append("+ to avoid a Fumbled Throw");
			}
		}
		if (!report.isSuccessful() && !report.isReRolled() && canThrow) {
			neededRoll = new StringBuilder().append("Roll a ").append(report.getMinimumRoll());
			if (report.isKick()) {
				neededRoll.append("+ to make at least a Subpar Kick");

			} else {
				neededRoll.append("+ to make at least a Subpar Throw");
			}
		}
		if (neededRoll != null) {
			neededRoll.append(" (Roll ");
			PassingDistance passingDistance = report.getPassingDistance();
			neededRoll.append(" - ");
			neededRoll.append(passingDistance.getModifier2020()).append(" ").append(passingDistance.getName());
			neededRoll.append(statusReport.formatRollModifiers(report.getRollModifiers())).append(" > 1).");
			println(getIndent() + 2, TextStyle.NEEDED_ROLL, neededRoll.toString());
		}
		setIndent(getIndent() + 1);
	}
}
