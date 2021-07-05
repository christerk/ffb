package com.fumbbl.ffb.client.report;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportBombExplodesAfterCatch;
import com.fumbbl.ffb.report.ReportId;

@ReportMessageType(ReportId.BOMB_EXPLODES_AFTER_CATCH)
@RulesCollection(Rules.COMMON)
public class BombExplodesAfterCatchMessage extends ReportMessageBase<ReportBombExplodesAfterCatch> {

    @Override
    protected void render(ReportBombExplodesAfterCatch report) {
	    println(getIndent() + 1, TextStyle.ROLL, "Bomb Roll [ " + report.getRoll() + " ]");
	    Player<?> catcher = game.getPlayerById(report.getCatcherId());
	    TextStyle teamStyle = game.getTeamHome().hasPlayer(catcher) ? TextStyle.HOME : TextStyle.AWAY;
	    print(getIndent() + 2, teamStyle, catcher.getName());
	    print(getIndent() + 2, " caught the bomb");
	    if (report.explodes()) {
		    println(getIndent() + 2, " but it explodes in " + catcher.getPlayerGender().getGenitive() + " hands.");
	    } else {
		    println(getIndent() + 2, " and it does not explode");
	    }
    }
}
