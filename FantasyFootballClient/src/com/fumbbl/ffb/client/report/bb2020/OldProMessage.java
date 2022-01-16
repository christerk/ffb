package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportOldPro;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.OLD_PRO)
public class OldProMessage extends ReportMessageBase<ReportOldPro> {
	@Override
	protected void render(ReportOldPro report) {

		println(getIndent() + 1, TextStyle.ROLL, "Old Pro Roll [ " + report.getNewValue() + " ]");

		Player<?> player = game.getPlayerById(report.getPlayerId());

		int indent = getIndent() + 1;
		print(indent, false, player);

		String action = report.isSelfInflicted() ? "forced the opponent to re-roll a " : " re-rolled a ";

		println(indent, TextStyle.NONE, action + report.getOldValue() + " into a " + report.getNewValue() + ".");
	}
}
