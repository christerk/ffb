package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;

public class RamModification extends InjuryContextModification<ModificationParams> {

	public RamModification() {
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
	protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
		return !injuryContext.isCasualty() && game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer()).hasTacklezones();
	}

	@Override
	protected boolean modifyInjuryInternal(ModifiedInjuryContext injuryContext, GameState gameState) {
		if (!Collections.disjoint(Arrays.asList(injuryContext.getArmorModifiers()), getSkill().getArmorModifiers())) {
			return false;
		}
		injuryContext.setSkillUse(SkillUse.ADD_INJURY_MODIFIER);
		return super.modifyInjuryInternal(injuryContext, gameState);
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}
}
