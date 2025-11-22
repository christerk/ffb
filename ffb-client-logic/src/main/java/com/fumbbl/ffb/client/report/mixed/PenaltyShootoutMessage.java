package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportPenaltyShootout;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.PENALTY_SHOOTOUT)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class PenaltyShootoutMessage extends ReportMessageBase<ReportPenaltyShootout> {

    @Override
    protected void render(ReportPenaltyShootout report) {
	    println(getIndent(), TextStyle.ROLL, report.getRollCount() + " Penalty Shootout Rolls: Home [" + report.getRollHome() + "] Away [" + report.getRollAway() + "]");
	    if (report.getHomeTeamWonPenalty() != null) {
		    TextStyle coachStyle = report.getHomeTeamWonPenalty() ? TextStyle.HOME : TextStyle.AWAY;
		    String teamName = (report.getHomeTeamWonPenalty() ? game.getTeamHome() : game.getTeamAway()).getName();
		    print(getIndent() + 1, coachStyle, teamName);
		    println(getIndent() + 1, TextStyle.NONE, " win this penalty");
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
		    println(getIndent(), TextStyle.NONE, " win sudden death");
	    }
    }
}
