package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.BreatheFireResult;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.bb2020.ReportBreatheFire;

@ReportMessageType(ReportId.BREATHE_FIRE)
@RulesCollection(Rules.BB2020)
public class BreatheFireMessage extends ReportMessageBase<ReportBreatheFire> {

	@Override
	protected void render(ReportBreatheFire report) {
		Player<?> player = game.getActingPlayer().getPlayer();
		println(getIndent(), TextStyle.ROLL, "Breathe Fire Roll [ " + report.getRoll() + " ]");
		print(getIndent() + 1, false, player);
		switch (report.getResult()) {
			case KNOCK_DOWN:
				print(getIndent() + 1, TextStyle.NONE, " engulfs ");
				print(getIndent() + 1, false, game.getPlayerById(report.getDefenderId()));
				println(getIndent() + 1, TextStyle.NONE, " in flames.");
				break;
			case PRONE:
				print(getIndent() + 1, TextStyle.NONE, " forces ");
				print(getIndent() + 1, false, game.getPlayerById(report.getDefenderId()));
				println(getIndent() + 1, TextStyle.NONE, " to take cover.");
				printNeededRoll(report.isStrongOpponent(), BreatheFireResult.KNOCK_DOWN);
				break;
			case NO_EFFECT:
				print(getIndent() + 1, TextStyle.NONE, " misses ");
				print(getIndent() + 1, false, game.getPlayerById(report.getDefenderId()));
				println(getIndent() + 1, TextStyle.NONE, ".");
				printNeededRoll(report.isStrongOpponent(), BreatheFireResult.KNOCK_DOWN);
				printNeededRoll(report.isStrongOpponent(), BreatheFireResult.PRONE);
				break;
			case FAILURE:
				println(getIndent() + 1, TextStyle.NONE, " engulfs " + player.getPlayerGender().getSelf() + " in flames.");
				printNeededRoll(report.isStrongOpponent(), BreatheFireResult.KNOCK_DOWN);
				printNeededRoll(report.isStrongOpponent(), BreatheFireResult.PRONE);
				printNeededRoll(report.isStrongOpponent(), BreatheFireResult.NO_EFFECT);
				break;
			default:
				break;
		}
	}

	private void printNeededRoll(boolean modified, BreatheFireResult potentialResult) {
		StringBuilder neededRoll = new StringBuilder();
		neededRoll.append(" (Roll");

		if (modified) {
			neededRoll.append(" - 1 opponent has strength 5 or more");
		}

		neededRoll.append(" >= ");

		switch (potentialResult) {
			case KNOCK_DOWN:
				neededRoll.append("6 to knock down opponent");
				break;
			case PRONE:
				neededRoll.append("4 to place opponent prone");
				break;
			case NO_EFFECT:
				neededRoll.append("2 to avoid knock down");
				break;
			default:
				break;
		}

		neededRoll.append(").");

		println(getIndent() + 1, TextStyle.NEEDED_ROLL, neededRoll.toString());
	}
}
