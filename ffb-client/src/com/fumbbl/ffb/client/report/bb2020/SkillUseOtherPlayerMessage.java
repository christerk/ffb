package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportSkillUse;
import com.fumbbl.ffb.report.bb2020.ReportSkillUseOtherPlayer;

@ReportMessageType(ReportId.SKILL_USE_OTHER_PLAYER)
@RulesCollection(Rules.BB2020)
public class SkillUseOtherPlayerMessage extends ReportMessageBase<ReportSkillUseOtherPlayer> {

	@Override
	protected void render(ReportSkillUseOtherPlayer report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		Player<?> otherPlayer = game.getPlayerById(report.getOtherPlayerId());
		int indent = getIndent();
		StringBuilder status = new StringBuilder();

		print(indent, false, player);
		status.append(" uses ")
			.append(report.getSkill().getName())
			.append(" of ");
		print(indent, status.toString());
		print(indent, false, otherPlayer);

		status = new StringBuilder();
		status.append(" ").append(report.getSkillUse().getDescription(player));
		status.append(".");
		println(indent, status.toString());

	}
}
