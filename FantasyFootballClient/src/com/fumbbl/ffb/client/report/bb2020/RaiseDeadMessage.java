package com.fumbbl.ffb.client.report.bb2020;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.client.TextStyle;
import com.fumbbl.ffb.client.report.ReportMessageBase;
import com.fumbbl.ffb.client.report.ReportMessageType;
import com.fumbbl.ffb.mechanics.GameMechanic;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.RosterPosition;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.ReportId;
import com.fumbbl.ffb.report.ReportRaiseDead;
import com.fumbbl.ffb.util.StringTool;

@ReportMessageType(ReportId.RAISE_DEAD)
@RulesCollection(Rules.BB2020)
public class RaiseDeadMessage extends ReportMessageBase<ReportRaiseDead> {

	@Override
	protected void render(ReportRaiseDead report) {
		Player<?> raisedPlayer = game.getPlayerById(report.getPlayerId());
		print(getIndent(), false, raisedPlayer);
		if (report.isNurglesRot()) {
			GameMechanic gameMechanic = (GameMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.GAME.name());
			print(getIndent(), gameMechanic.raisedByNurgleMessage());
		} else {
			print(getIndent(), " is raised from the dead to join team ");
		}
		Team team;
		if (game.getTeamHome().hasPlayer(raisedPlayer)) {
			team = game.getTeamHome();
			print(getIndent(), TextStyle.HOME, game.getTeamHome().getName());
		} else {
			team = game.getTeamAway();
			print(getIndent(), TextStyle.AWAY, game.getTeamAway().getName());
		}
		String positionName = report.isNurglesRot() ? "Rotter" : "Zombie";

		RosterPosition position = team.getRoster().getRaisedRosterPosition();
		if (position != null) {
			if (StringTool.isProvided(position.getDisplayName())) {
				positionName = position.getDisplayName();
			} else {
				positionName = position.getName();
			}
		}

		println(getIndent(), TextStyle.NONE, " as a " + positionName + ".");
	}
}
