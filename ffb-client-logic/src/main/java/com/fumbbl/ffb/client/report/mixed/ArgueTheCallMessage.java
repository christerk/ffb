package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportArgueTheCallRoll;

@ReportMessageType(ReportId.ARGUE_THE_CALL)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class ArgueTheCallMessage extends ReportMessageBase<ReportArgueTheCallRoll> {

	@Override
	protected void render(ReportArgueTheCallRoll report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		StringBuilder status = new StringBuilder();
		status.append("Argue the Call Roll [ ").append(report.getRoll()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int minimumRoll = 6;
		if (report.isFriendsWithRef()) {
			println(getIndent() + 1, TextStyle.EXPLANATION, "Being friends with the ref allows argue to succeed on 5+.");
			minimumRoll = 5;
		}

		int target = minimumRoll;
		int biasedRefs = report.getBiasedRefs();
		StringBuilder builder = new StringBuilder();
		if (biasedRefs > 0) {

			builder.append(" + ").append(biasedRefs).append(" Biased Referee");

			if (biasedRefs > 1) {
				builder.append("s");
			}
			minimumRoll -= biasedRefs;
		}

		String modifiers = builder.toString();

		if (report.isSuccessful()) {
			print(getIndent() + 1, TextStyle.NONE, "The ref refrains from banning ");
			print(getIndent() + 1, false, player);
			status = new StringBuilder();
			if (!game.getFieldModel().getPlayerCoordinate(player).isBoxCoordinate()) {
				status.append(" and ").append(player.getPlayerGender().getNominative());
				if (report.isStaysOnPitch()) {
					status.append(" stays on the pitch");
				} else {
					status.append(" is sent to the reserve instead");
				}
			}
			status.append(".");
			println(getIndent() + 1, TextStyle.NONE, status.toString());
			println(getIndent() + 1, TextStyle.NEEDED_ROLL, "Succeeded on a roll of " + minimumRoll + " (Roll" + modifiers + " >= " + target + ")");
		} else {
			print(getIndent() + 1, TextStyle.NONE, "The ref bans ");
			print(getIndent() + 1, false, player);
			println(getIndent() + 1, TextStyle.NONE, " from the game.");
			println(getIndent() + 1, TextStyle.NEEDED_ROLL, "Would have succeeded on a roll of " + minimumRoll + " (Roll" + modifiers + " >= " + target + ")");
		}
		if (report.isCoachBanned()) {
			print(getIndent() + 1, TextStyle.NONE, "Coach ");
			if (game.getTeamHome().hasPlayer(player)) {
				print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getCoach());
			} else {
				print(getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getCoach());
			}
			println(getIndent() + 1, TextStyle.NONE, " is also banned from the game.");
		}
	}
}
