package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportKickoffPitchInvasion;

@ReportMessageType(ReportId.KICKOFF_PITCH_INVASION)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class KickoffPitchInvasionMessage extends ReportMessageBase<ReportKickoffPitchInvasion> {

	@Override
	protected void render(ReportKickoffPitchInvasion report) {
		GameResult gameResult = game.getGameResult();

		StringBuilder status = new StringBuilder();
		if (report.getAmount() > 0) {
			status.append("Pitch Invasion Roll [ ").append(report.getAmount()).append(" ]");
			println(getIndent(), TextStyle.ROLL, status.toString());
			status = new StringBuilder();
			status.append("Affected Teams will have ").append(report.getAmount()).append(" player");
			if (report.getAmount() > 1) {
				status.append("s");
			}
			status.append(" stunned.");
			println(getIndent() + 1, TextStyle.EXPLANATION, status.toString());
			status = new StringBuilder();
		}
		status.append("Pitch Invasion Roll Home Team [ ").append(report.getRollHome()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalHome = report.getRollHome() + gameResult.getTeamResultHome().getFanFactor();
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollHome());
		status.append(" + ").append(gameResult.getTeamResultHome().getFanFactor()).append(" Fan Factor");
		status.append(" = ").append(totalHome).append(".");
		println(getIndent() + 1, status.toString());
		status = new StringBuilder();
		status.append("Pitch Invasion Roll Away Team [ ").append(report.getRollAway()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalAway = report.getRollAway() + gameResult.getTeamResultAway().getFanFactor();
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollAway());
		status.append(" + ").append(gameResult.getTeamResultAway().getFanFactor()).append(" Fan Factor");
		status.append(" = ").append(totalAway).append(".");
		println(getIndent() + 1, status.toString());

		report.getAffectedPlayers()
			.forEach(player -> {
					print(getIndent() + 1, false, game.getPlayerById(player));
					println(getIndent() + 1, TextStyle.NONE, " is stunned");
				}
			);
	}
}
