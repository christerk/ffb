package com.fumbbl.ffb.client.report.mixed;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportSkillWasted;

@ReportMessageType(ReportId.SKILL_WASTED)
@RulesCollection(Rules.BB2020)
@RulesCollection(Rules.BB2025)
public class SkillWastedMessage extends ReportMessageBase<ReportSkillWasted> {

	@Override
	protected void render(ReportSkillWasted report) {
		if (report.getSkill() != null) {
			Player<?> player = game.getPlayerById(report.getPlayerId());
			int indent = getIndent();
			StringBuilder status = new StringBuilder();
			if (player != null) {
				print(indent, false, player);
				status.append(" wastes ").append(report.getSkill().getName());
			} else {
				status.append(report.getSkill().getName()).append(" is wasted");
			}
			status.append(".");
			println(indent, status.toString());
		}
	}
}
