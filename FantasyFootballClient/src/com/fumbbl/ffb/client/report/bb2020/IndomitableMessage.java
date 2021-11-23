package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportIndomitable;

@ReportMessageType(ReportId.INDOMITABLE)
@RulesCollection(Rules.BB2020)
public class IndomitableMessage extends ReportMessageBase<ReportIndomitable> {

	@Override
	protected void render(ReportIndomitable report) {
		Player<?> player = game.getPlayerById(report.getPlayerId());
		print(getIndent() + 1, false, player);

		print(getIndent() + 1, " uses Indomitable to push ");
		print(getIndent() + 1, player.getPlayerGender().getGenitive());
		print(getIndent() + 1, " strength to the double of ");
		print(getIndent(), false, game.getPlayerById(report.getDefenderId()));
		print(getIndent() + 1, ".");
	}
}
