package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SeriousInjury;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.modifiers.bb2020.CasualtyModifier;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportApothecaryRoll;
import com.fumbbl.ffb.util.ArrayTool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ReportMessageType(ReportId.APOTHECARY_ROLL)
@RulesCollection(Rules.BB2020)
public class ApothecaryRollMessage extends ReportMessageBase<ReportApothecaryRoll> {

	@Override
	protected void render(ReportApothecaryRoll report) {
		int[] casualtyRoll = report.getCasualtyRoll();
		if (ArrayTool.isProvided(casualtyRoll)) {
			println(getIndent(), TextStyle.BOLD, "Apothecary used.");
			Player<?> player = game.getPlayerById(report.getPlayerId());
			SeriousInjury seriousInjury = report.getSeriousInjury();
			StringBuilder status = new StringBuilder();
			status.append("Casualty Roll [ ").append(casualtyRoll[0]);
			if (seriousInjury != null && seriousInjury.showSiRoll()) {
				status.append(" ][ ").append(casualtyRoll[1]);
			}
			status.append(" ]");
			println(getIndent(), TextStyle.ROLL, status.toString());

			if (!report.getCasualtyModifiers().isEmpty()) {
				int modifiers = 0;
				status = new StringBuilder("Rolled ").append(casualtyRoll[0]);
				List<String> reportStrings = new ArrayList<>();
				for (CasualtyModifier modifier : report.getCasualtyModifiers()) {
					reportStrings.add(modifier.reportString());
					modifiers += modifier.getModifier();
				}
				reportStrings.sort(Comparator.naturalOrder());
				for (String reportString : reportStrings) {
					status.append(" + ");
					status.append(reportString);
				}
				status.append(" = ").append(casualtyRoll[0] + modifiers);
				println(getIndent() + 1, TextStyle.NONE, status.toString());
			}

			PlayerState injury = report.getPlayerState();
			print(getIndent() + 1, false, player);
			status = new StringBuilder();
			status.append(" ").append(injury.getDescription()).append(".");
			println(getIndent() + 1, status.toString());
			if (seriousInjury != null) {
				if (report.getOriginalInjury() != null) {
					status = new StringBuilder().append(player.getName()).append(" would have ")
						.append(report.getOriginalInjury().getDescription())
						.append(" but that stat cannot be reduced any further. So a different injury has been chosen randomly.");
					println(getIndent() + 1, TextStyle.EXPLANATION, status.toString());
				}
				print(getIndent() + 1, false, player);
				status = new StringBuilder();
				status.append(" ").append(seriousInjury.getDescription()).append(".");
				println(getIndent() + 1, status.toString());
			}
		}
	}
}
