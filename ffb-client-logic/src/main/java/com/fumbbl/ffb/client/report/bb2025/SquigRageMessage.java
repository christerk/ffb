package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2025.ReportSquigRage;

@ReportMessageType(ReportId.SQUIG_RAGE)
@RulesCollection(Rules.BB2025)
public class SquigRageMessage extends ReportMessageBase<ReportSquigRage> {

	@Override
	protected void render(ReportSquigRage report) {
		if (report.getOutcome() == null) {
			return;
		}
		Player<?> player = game.getPlayerById(report.getPlayerId());
		print(getIndent() + 1, false, player);
		switch (report.getOutcome()) {
			case MUST_BLOCK:
				println(getIndent() + 1, " must block an opponent they are marking.");
				break;
			case KNOCKS_DOWN_TEAM_MATE:
				print(getIndent() + 1, " knocks down team-mate ");
				print(getIndent() + 1, false, game.getPlayerById(report.getTeamMateId()));
				println(getIndent() + 1, ".");
				break;
			case DISTRACTED:
				println(getIndent() + 1, " is distracted.");
				break;
			default:
				break;
		}
	}
}
