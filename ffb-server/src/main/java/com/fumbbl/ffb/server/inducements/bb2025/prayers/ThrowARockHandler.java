package com.fumbbl.ffb.server.inducements.bb2025.prayers;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.factory.InducementTypeFactory;
import com.fumbbl.ffb.inducement.Inducement;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.inducement.bb2025.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.GameState;

@RulesCollection(RulesCollection.Rules.BB2025)
public class ThrowARockHandler extends com.fumbbl.ffb.server.inducements.mixed.prayers.ThrowARockHandler {
	@Override
	public Prayer handledPrayer() {
		return Prayer.THROW_A_ROCK;
	}

	@Override
	public boolean initEffect(GameState gameState, Team prayingTeam) {
		Game game = gameState.getGame();
		InducementSet inducementSet = game.getTeamHome() == prayingTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		InducementTypeFactory factory = game.getFactory(FactoryType.Factory.INDUCEMENT_TYPE);
		factory.allTypes().stream().filter(ind -> ind.hasUsage(Usage.THROW_ROCK)).findFirst().ifPresent(ind -> {

			if (inducementSet.getInducementTypes().contains(ind)) {
				Inducement inducement = inducementSet.get(ind);
				inducement.setValue(inducement.getValue() + 1);
			} else {
				inducementSet.addInducement(new Inducement(ind, 1));
			}
		});
		return true;
	}

	@Override
	public void removeEffectInternal(GameState gameState, Team team) {

	}

}
