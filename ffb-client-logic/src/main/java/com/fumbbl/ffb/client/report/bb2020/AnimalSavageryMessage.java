package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportAnimalSavagery;
import com.fumbbl.ffb.util.StringTool;

@RulesCollection(RulesCollection.Rules.BB2020)
@ReportMessageType(ReportId.ANIMAL_SAVAGERY)
public class AnimalSavageryMessage extends ReportMessageBase<ReportAnimalSavagery> {
	@Override
	protected void render(ReportAnimalSavagery report) {
		Player<?> attacker = game.getPlayerById(report.getAttackerId());

		int indent = getIndent() + 1;
		print(indent, false, attacker);

		if (StringTool.isProvided(report.getDefenderId())) {
			Player<?> defender = game.getPlayerById(report.getDefenderId());
			print(indent, TextStyle.NONE, " lashes out against ");
			print(indent, false, defender);
			println(indent, TextStyle.NONE, ".");
		} else {
			println(indent, TextStyle.NONE, " has no one to lash out against and thus loses " + attacker.getPlayerGender().getGenitive() + " action.");
		}
	}
}
