package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportDedicatedFans;

@ReportMessageType(ReportId.DEDICATED_FANS)
@RulesCollection(Rules.BB2020)
public class DedicatedFansMessage extends ReportMessageBase<ReportDedicatedFans> {

    @Override
    protected void render(ReportDedicatedFans report) {

    	printTeamRoll(report.getRollHome(), report.getModifierHome(), game.getTeamHome().getName(), TextStyle.HOME_BOLD,
		    report.isConceded() && game.getTeamHome() == game.getTeamById(report.getConcededTeam()));

	    printTeamRoll(report.getRollAway(), report.getModifierAway(), game.getTeamAway().getName(), TextStyle.AWAY_BOLD,
		    report.isConceded() && game.getTeamAway() == game.getTeamById(report.getConcededTeam()));
    }

    private void printTeamRoll(int roll, int modifier, String teamName, TextStyle teamStyle, boolean conceded) {
    	if (roll > 0) {
		    println(getIndent(), TextStyle.ROLL, "Dedicated Fans Roll [ " + roll + " ]");
	    }

	    print(getIndent() + 1, teamStyle, teamName);

	    StringBuilder text = new StringBuilder();

	    if (modifier > 0) {
	    	text.append(" gain ").append(modifier);
	    } else if (modifier < 0) {
	    	text.append(" lose ").append(Math.abs(modifier));
	    } else {
	    	text.append(" keep their");
	    }

	    text.append(" Dedicated Fan");

	    if (Math.abs(modifier) != 1) {
	    	text.append("s");
	    }

	    if (conceded) {
    	  text.append(" due to conceeding");
	    }
	    text.append(".");

	    println(getIndent() + 1, TextStyle.NONE, text.toString());
    }
}
