package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;

import java.util.Collections;

public class KrumpAndSmashModification extends RerollArmourModification {
	public KrumpAndSmashModification() {
		super(Collections.singleton(Block.class));
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		Game game = params.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();

		return actingPlayer != null
			&& actingPlayer.getPlayerId().equals(params.getNewContext().fAttackerId)
			&& super.tryArmourRollModification(params);
	}
}

