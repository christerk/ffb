package com.fumbbl.ffb.client.report.bb2016;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportInducement;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.INDUCEMENT)
@RulesCollection(Rules.BB2016)
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
			InducementType inducementType = pReport.getInducementType();

			if (inducementType.hasUsage(Usage.REROLL)) {
				print(getIndent(), " use ");
				print(getIndent(), TextStyle.BOLD, "Extra Team Training");
				status.append(" to add ").append(pReport.getValue())
					.append((pReport.getValue() == 1) ? " Re-Roll." : " Re-Rolls.");
				println(getIndent(), status.toString());
			} else if (inducementType.hasUsage(Usage.APOTHECARY)) {
				print(getIndent(), " use ");
				print(getIndent(), TextStyle.BOLD, "Wandering Apothecaries");
				status.append(" to add ").append(pReport.getValue())
					.append((pReport.getValue() == 1) ? " Apothecary." : " Apothecaries.");
				println(getIndent(), status.toString());
			} else if (inducementType.hasUsage(Usage.REGENERATION)) {
				print(getIndent(), " use ");
				print(getIndent(), TextStyle.BOLD, "Igor");
				println(getIndent(), " to re-roll the failed Regeneration.");
			}
		}		
	}
}
