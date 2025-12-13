package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportKickoffExtraReRoll;

@ReportMessageType(ReportId.KICKOFF_EXTRA_RE_ROLL)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class KickoffExtraReRollMessage extends ReportMessageBase<ReportKickoffExtraReRoll> {

	@Override
	protected void render(ReportKickoffExtraReRoll report) {
		StringBuilder status = new StringBuilder();
		boolean homeBanned = game.getTurnDataHome().isCoachBanned();
		boolean awayBanned = game.getTurnDataAway().isCoachBanned();

		int homePart = game.getTurnDataHome().getInducementSet().value(Usage.ADD_COACH);
		int awayPart = game.getTurnDataAway().getInducementSet().value(Usage.ADD_COACH);

		status.append("Brilliant Coaching Roll Home Team [ ").append(report.getRollHome()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalHome = report.getRollHome()
			+ game.getTeamHome().getAssistantCoaches() - (homeBanned ? 1 : 0) + homePart;
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollHome());
		status.append(" + ").append(game.getTeamHome().getAssistantCoaches()).append(" Assistant Coaches");
		if (homePart > 0) {
			status.append(" + ").append(homePart).append(" Part-time Assistant Coaches");
		}
		status.append(" ").append(homeBanned ? "- 1 Banned" : " + 0 Head").append(" Coach");
		status.append(" = ").append(totalHome).append(".");
		println(getIndent() + 1, status.toString());
		status = new StringBuilder();
		status.append("Brilliant Coaching Roll Away Team [ ").append(report.getRollAway()).append(" ]");
		println(getIndent(), TextStyle.ROLL, status.toString());
		int totalAway = report.getRollAway()
			+ game.getTeamAway().getAssistantCoaches() - (awayBanned ? 1 : 0) + awayPart;
		status = new StringBuilder();
		status.append("Rolled ").append(report.getRollAway());
		status.append(" + ").append(game.getTeamAway().getAssistantCoaches()).append(" Assistant Coaches");
		if (awayPart > 0) {
			status.append(" + ").append(awayPart).append(" Part-time Assistant Coaches");
		}
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
