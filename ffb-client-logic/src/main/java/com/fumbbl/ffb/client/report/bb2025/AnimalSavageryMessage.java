package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.mixed.ReportAnimalSavagery;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.ANIMAL_SAVAGERY)
@RulesCollection(RulesCollection.Rules.BB2025)
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
			if (UtilGameOption.isOptionEnabled(game, GameOptionId.ANIMAL_SAVAGERY_LASH_OUT_ENDS_ACTIVATION)) {
				print(indent, TextStyle.NONE, " but still loses " + attacker.getPlayerGender().getGenitive() + " action");
			}
			println(indent, TextStyle.NONE, ".");
		} else {
			println(indent, TextStyle.NONE, " has no one to lash out against and thus loses " + attacker.getPlayerGender().getGenitive() + " action.");
		}
	}
}
