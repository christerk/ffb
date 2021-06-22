package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class PrayerHandler implements INamedObject {

	protected List<IReport> reports = new ArrayList<>();

	protected EnhancementRemover enhancementRemover = new EnhancementRemover();

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public boolean handles(Prayer prayer) {
		return prayer == handledPrayer();
	}

	abstract Prayer handledPrayer();

	public final void initEffect(@Nullable IStep step, GameState gameState, String prayingTeamId) {
		Game game = gameState.getGame();
		Team prayingTeam = game.getTeamById(prayingTeamId);
		InducementSet inducementSet = game.getTeamHome() == prayingTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		inducementSet.addPrayer(handledPrayer());
		if (initEffect(gameState, prayingTeam) && step != null) {
			step.getResult().setNextAction(StepAction.NEXT_STEP);
			reports.forEach(report -> step.getResult().addReport(report));
		}
	}

	public final void applySelection(@Nullable IStep step, Game game, PrayerDialogSelection selection) {
		applySelection(game, selection);
		if (step != null) {
			reports.forEach(report -> step.getResult().addReport(report));
		}
	}

	void applySelection(Game game, PrayerDialogSelection selection) {
	}

	/**
	 * @return true if handler logic is complete
	 */
	abstract boolean initEffect(GameState gameState, Team prayingTeam);

	public abstract void removeEffect(GameState gameState, Team team);
}
