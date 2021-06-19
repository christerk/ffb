package com.fumbbl.ffb.server.inducements.bb2020.prayers;

import com.fumbbl.ffb.FieldCoordinateBounds;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.TurnMode;
import com.fumbbl.ffb.inducement.bb2020.Prayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.InducementSet;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepAction;
import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PrayerHandler implements INamedObject {

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public boolean handles(Prayer prayer) {
		return prayer == handledPrayer();
	}

	abstract Prayer handledPrayer();

	public void initEffect(@Nullable IStep step, GameState gameState, String prayingTeamId) {
		Game game = gameState.getGame();
		Team prayingTeam = game.getTeamById(prayingTeamId);
		InducementSet inducementSet = game.getTeamHome() == prayingTeam ? game.getTurnDataHome().getInducementSet() : game.getTurnDataAway().getInducementSet();
		inducementSet.addPrayer(handledPrayer());
		if (initEffect(gameState, prayingTeam) && step != null) {
			step.getResult().setNextAction(StepAction.NEXT_STEP);
		}
	}

	protected List<Player<?>> eligiblePlayers(Team team, Game game) {
		return Arrays.stream(team.getPlayers()).filter(player -> {
				if (game.getTurnMode() == TurnMode.KICKOFF) {
					return FieldCoordinateBounds.FIELD.isInBounds(game.getFieldModel().getPlayerCoordinate(player));
				} else {
					return game.getFieldModel().getPlayerState(player).getBase() == PlayerState.RESERVE;
				}
			}
		).filter(player -> !player.hasSkillProperty(NamedProperties.hasToRollToUseTeamReroll)).collect(Collectors.toList());
	}

	/**
	 * @return true if handler logic is complete
	 */
	abstract boolean initEffect(GameState gameState, Team prayingTeam);

	public abstract void removeEffect(GameState gameState, Team team);
}
