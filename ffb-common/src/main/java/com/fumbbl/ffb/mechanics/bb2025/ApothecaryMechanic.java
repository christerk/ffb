package com.fumbbl.ffb.mechanics.bb2025;

import com.fumbbl.ffb.ApothecaryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.TurnData;
import com.fumbbl.ffb.model.ZappedPlayer;

import java.util.ArrayList;
import java.util.List;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ApothecaryMechanic extends com.fumbbl.ffb.mechanics.ApothecaryMechanic {

	@Override
	public List<ApothecaryType> apothecaryTypes(Game game, Player<?> defender, PlayerState playerState) {
		List<ApothecaryType> validTypes = new ArrayList<>();
		if (defender instanceof ZappedPlayer) {
			return validTypes;
		}

		TurnData turnData = game.getTeamHome().hasPlayer(defender) ? game.getTurnDataHome() : game.getTurnDataAway();

		boolean teamHasWanderingApo = turnData.getWanderingApothecaries() > 0;

		boolean teamCanUsePlagueDoctor = turnData.getPlagueDoctors() > 0;

		if (turnData.getApothecaries() > turnData.getWanderingApothecaries()) {
			validTypes.add(ApothecaryType.TEAM);
		} else if (teamHasWanderingApo) {
			validTypes.add(ApothecaryType.WANDERING);
		}

		if (teamCanUsePlagueDoctor) {
			validTypes.add(ApothecaryType.PLAGUE);
		}

		return validTypes;
	}

}

