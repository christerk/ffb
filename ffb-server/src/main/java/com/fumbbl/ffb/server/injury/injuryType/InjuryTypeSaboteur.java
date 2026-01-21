package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SoundId;
import com.fumbbl.ffb.injury.Saboteur;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;

public class InjuryTypeSaboteur extends InjuryTypeServer<Saboteur> {

	public InjuryTypeSaboteur() {
		super(new Saboteur());
	}

	@Override
	public void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                         Player<?> attacker, Player<?> defender, FieldCoordinate defenderCoordinate,
	                         FieldCoordinate fromCoordinate, InjuryContext oldContext, ApothecaryMode apothecaryMode) {

		injuryContext.setArmorBroken(true);
		injuryContext.setArmorRoll(null);
		injuryContext.setInjuryRoll(null);
		injuryContext.setInjury(new PlayerState(PlayerState.KNOCKED_OUT));
		injuryContext.setSound(SoundId.KO);
	}

}
