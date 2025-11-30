package com.fumbbl.ffb.server.inducements.mixed.prayers;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Animation;
import com.fumbbl.ffb.model.AnimationType;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.util.UtilServerGame;

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

	abstract AnimationType animationType();

	public final void initEffect(IStep step, GameState gameState, String prayingTeamId) {
		if (step != null) {
			step.getResult().setAnimation(new Animation(animationType()));
			UtilServerGame.syncGameModel(step);
		}
		Game game = gameState.getGame();
		Team prayingTeam = game.getTeamById(prayingTeamId);
		InducementSet inducementSet = game.getTeamHome() == prayingTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		inducementSet.addPrayer(handledPrayer());
		if (initEffect(gameState, prayingTeam) && step != null) {
			step.getResult().setNextAction(StepAction.NEXT_STEP);
			reports.forEach(report -> step.getResult().addReport(report));
			reports.clear();
			if (handledPrayer().isChangingPlayer()) {
				gameState.updatePlayerMarkings();
			}
		}
	}

	public final void applySelection(IStep step, GameState gameState, PrayerDialogSelection selection) {
		applySelection(gameState.getGame(), selection);
		if (step != null) {
			reports.forEach(report -> step.getResult().addReport(report));
			reports.clear();
		}
		if (handledPrayer().isChangingPlayer()) {
			gameState.updatePlayerMarkings();
		}
	}

	void applySelection(Game game, PrayerDialogSelection selection) {
	}

	/**
	 * @return true if handler logic is complete
	 */
	abstract boolean initEffect(GameState gameState, Team prayingTeam);

	abstract void removeEffectInternal(GameState gameState, Team team);

	public void removeEffect(GameState gameState, Team team) {
		removeEffectInternal(gameState, team);
		if (handledPrayer().isChangingPlayer()) {
			gameState.updatePlayerMarkings();
		}
	}

}
