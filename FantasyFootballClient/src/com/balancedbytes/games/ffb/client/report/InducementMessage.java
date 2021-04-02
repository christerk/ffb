package com.balancedbytes.games.ffb.client.report;

import com.balancedbytes.games.ffb.RulesCollection;
import com.balancedbytes.games.ffb.RulesCollection.Rules;
import com.balancedbytes.games.ffb.client.TextStyle;
import com.balancedbytes.games.ffb.report.ReportId;
import com.balancedbytes.games.ffb.report.ReportInducement;
import com.balancedbytes.games.ffb.util.StringTool;

@ReportMessageType(ReportId.INDUCEMENT)
@RulesCollection(Rules.COMMON)
public class InducementMessage extends ReportMessageBase<ReportInducement> {

	@Override
	protected void render(ReportInducement pReport) {
		if (StringTool.isProvided(pReport.getTeamId()) && (pReport.getInducementType() != null)) {
			if (pReport.getTeamId().equals(game.getTeamHome().getId())) {
				print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
			} else {
				print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
			}
			StringBuilder status = new StringBuilder();
			switch (pReport.getInducementType().getUsage()) {
				case REROLL:
					print(getIndent(), " use ");
					print(getIndent(), TextStyle.BOLD, "Extra Team Training");
					status.append(" to add ").append(pReport.getValue())
						.append((pReport.getValue() == 1) ? " Re-Roll." : " Re-Rolls.");
					println(getIndent(), status.toString());
					break;
				case APOTHECARY:
					print(getIndent(), " use ");
					print(getIndent(), TextStyle.BOLD, "Wandering Apothecaries");
					status.append(" to add ").append(pReport.getValue())
						.append((pReport.getValue() == 1) ? " Apothecary." : " Apothecaries.");
					println(getIndent(), status.toString());
					break;
				case REGENERATION:
					print(getIndent(), " use ");
					print(getIndent(), TextStyle.BOLD, "Igor");
					println(getIndent(), " to re-roll the failed Regeneration.");
					break;
				default:
					break;
			}
		}		
	}
}
