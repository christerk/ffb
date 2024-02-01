package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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
		boolean mbUsed = Arrays.stream(params.getNewContext().getArmorModifiers()).anyMatch(modifier -> modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock));
		return (!params.getNewContext().isArmorBroken() || (params.getNewContext().isArmorBroken() && mbUsed))
			&& game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer()).hasTacklezones();
	}

	@Override
	protected void prepareArmourParams(ModificationParams params) {
		if (params.getNewContext().isArmorBroken()) {
			Set<ArmorModifier> modifiers = Arrays.stream(params.getNewContext().getArmorModifiers())
				.filter(modifier -> !modifier.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock))
				.collect(Collectors.toSet());
			params.getNewContext().clearArmorModifiers();
			params.getNewContext().addArmorModifiers(modifiers);
		}
		super.prepareArmourParams(params);
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.ADD_ARMOUR_MODIFIER;
	}
}
