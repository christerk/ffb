package com.fumbbl.ffb.client.report.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.mechanics.InjuryMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportRaiseDead;

@ReportMessageType(ReportId.RAISE_DEAD)
@RulesCollection(Rules.BB2025)
public class RaiseDeadMessage extends ReportMessageBase<ReportRaiseDead> {

	@Override
	protected void render(ReportRaiseDead report) {
		Player<?> raisedPlayer = game.getPlayerById(report.getPlayerId());
		print(getIndent(), false, raisedPlayer);
		if (report.isNurglesRot()) {
			InjuryMechanic mechanic = (InjuryMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.INJURY.name());
			print(getIndent(), mechanic.raisedByNurgleMessage());
		} else {
			print(getIndent(), " is raised from the dead to join team ");
		}

		if (game.getTeamHome().hasPlayer(raisedPlayer)) {
			print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
		} else {
			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
		}

		println(getIndent(), TextStyle.NONE, " as a " + report.getPosition() + ".");
	}
}
