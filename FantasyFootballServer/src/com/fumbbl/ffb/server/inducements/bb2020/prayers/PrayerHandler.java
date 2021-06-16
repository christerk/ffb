package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.server.step.IStep;
import com.sun.istack.internal.Nullable;

public abstract class PrayerHandler implements INamedObject {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public boolean handles(Prayer prayer) {
		return prayer == handledPrayer();
	}

	abstract Prayer handledPrayer();

	public void addEffect(@Nullable IStep step, Game game, String prayingTeamId) {
		Team prayingTeam = game.getTeamById(prayingTeamId);
		InducementSet inducementSet = game.getTeamHome() == prayingTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		inducementSet.addPrayer(handledPrayer());
		add(step, game, prayingTeam);
	}

	abstract void add(@Nullable IStep step, Game game, Team prayingTeam);

	public abstract void removeEffect(Game game);
}
