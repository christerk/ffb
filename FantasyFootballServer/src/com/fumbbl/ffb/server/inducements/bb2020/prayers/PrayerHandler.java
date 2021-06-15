package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.step.IStep;

public abstract class PrayerHandler {

	public boolean handles(Prayer prayer) {
		return prayer == handledPrayer();
	}

	abstract Prayer handledPrayer();

	public void addEffect(IStep step, Game game, Team prayingTeam) {
		InducementSet inducementSet = game.getTeamHome() == prayingTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		inducementSet.addPrayer(handledPrayer());
		addEffect(step, game, prayingTeam);
	}

	abstract void add(IStep step, Game game, Team prayingTeam);

	public abstract void removeEffect(Game game);
}
