package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

import java.util.Collections;

public class CrushingBlowModification extends InjuryContextModification<ModificationParams> {

	public CrushingBlowModification() {
		super(Collections.singleton(Block.class));
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		Game game = params.getGameState().getGame();
		return !params.getNewContext().isArmorBroken()
			&& game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer()).hasTacklezones();
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}
}
