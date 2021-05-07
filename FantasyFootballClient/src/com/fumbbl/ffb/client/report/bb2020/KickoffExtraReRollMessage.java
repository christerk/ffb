package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.GameResult;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportKickoffExtraReRoll;

@ReportMessageType(ReportId.KICKOFF_EXTRA_REROLL)
@RulesCollection(Rules.BB2020)
public class KickoffExtraReRollMessage extends ReportMessageBase<ReportKickoffExtraReRoll> {

	@Override
	protected void render(ReportKickoffExtraReRoll report) {
		GameResult gameResult = game.getGameResult();

		StringBuilder status = new StringBuilder();
		boolean homeBanned = game.getTurnDataHome().isCoachBanned();
		boolean awayBanned = game.getTurnDataAway().isCoachBanned();

		status.append("Brilliant Coaching Roll Home Team [ ").append(report.getRollHome()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalHome = report.getRollHome() + gameResult.getTeamResultHome().getFanFactor()
			+ game.getTeamHome().getAssistantCoaches() - (homeBanned ? 1 : 0);
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollHome());
		status.append(" + ").append(gameResult.getTeamResultHome().getFanFactor()).append(" Fan Factor");
		status.append(" + ").append(game.getTeamHome().getAssistantCoaches()).append(" Assistant Coaches");
		status.append(" ").append(homeBanned ? "- 1 Banned" : " + 0 Head").append(" Coach");
		status.append(" = ").append(totalHome).append(".");
		println(getIndent() + 1, status.toString());
		status = new StringBuilder();
		status.append("Brilliant Coaching Roll Away Team [ ").append(report.getRollAway()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalAway = report.getRollAway() + gameResult.getTeamResultAway().getFanFactor()
			+ game.getTeamAway().getAssistantCoaches() - (awayBanned ? 1 : 0);
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollAway());
		status.append(" + ").append(gameResult.getTeamResultAway().getFanFactor()).append(" Fan Factor");
		status.append(" + ").append(game.getTeamAway().getAssistantCoaches()).append(" Assistant Coaches");
		status.append(" ").append(awayBanned ? "- 1 Banned" : " + 0 Head").append(" Coach");
		status.append(" = ").append(totalAway).append(".");
		println(getIndent() + 1, status.toString());

		if (report.getTeamId() == null) {
			println(getIndent(), "Neither team gains a Re-Roll.");
		} else {
			if (report.getTeamId().equals(game.getTeamHome().getId())) {
				print(getIndent(), "Team ");
				print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
			} else {
				print(getIndent(), "Team ");
				print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
			}
			println(getIndent(), " gains a Re-Roll only available for this drive.");
		}
	}
}
