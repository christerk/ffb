package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportPenaltyShootout;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.PENALTY_SHOOTOUT)
@RulesCollection(Rules.BB2020)
public class PenaltyShootoutMessage extends ReportMessageBase<ReportPenaltyShootout> {

    @Override
    protected void render(ReportPenaltyShootout report) {
	    println(getIndent(), TextStyle.ROLL, report.getRollCount() + " Penalty Shootout Rolls: Home [" + report.getRollHome() + "] Away [" + report.getRollAway() + "]");
	    if (StringTool.isProvided(report.getWinningCoach())) {
		    TextStyle coachStyle = report.getWinningCoach().equals(game.getTeamHome().getCoach()) ? TextStyle.HOME : TextStyle.AWAY;
		    print(getIndent() + 1, coachStyle, report.getWinningCoach());
		    println(getIndent() + 1, TextStyle.NONE, " wins this penalty");
		    print(getIndent() + 1, TextStyle.NONE, "Current score: ");
		    print(getIndent() + 1, TextStyle.HOME, game.getTeamHome().getName());
		    print(getIndent() + 1, TextStyle.NONE, " " + report.getScoreHome() + " - " + report.getScoreAway() + " ");
		    println( getIndent() + 1, TextStyle.AWAY, game.getTeamAway().getName());
	    } else {
		    println(getIndent() + 1, TextStyle.NONE, "Penalty is rerolled");
	    }

	    if (StringTool.isProvided(report.getWinningTeam())) {
		    Team team = game.getTeamById(report.getWinningTeam());
		    TextStyle teamStyle = game.getTeamHome().equals(team) ? TextStyle.HOME : TextStyle.AWAY;

		    print(getIndent(), teamStyle, team.getName());
		    println(getIndent(), TextStyle.NONE, " win the game");
	    }
    }
}
