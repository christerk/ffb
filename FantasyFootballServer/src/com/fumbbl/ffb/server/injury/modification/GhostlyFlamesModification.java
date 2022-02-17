package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import com.fumbbl.ffb.modifiers.StaticArmourModifier;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class GhostlyFlamesModification extends InjuryContextModification<ModificationParams> {

	public GhostlyFlamesModification() {
		super(Collections.singleton(Chainsaw.class));
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		ActingPlayer actingPlayer = params.getGameState().getGame().getActingPlayer();
		return super.tryArmourRollModification(params) && actingPlayer != null && actingPlayer.getPlayerAction().isBlitzing()
			&& actingPlayer.getPlayerId().equals(params.getNewContext().fAttackerId);
	}

	@Override
	protected void prepareArmourParams(ModificationParams params) {
		Set<ArmorModifier> modifiers = Arrays.stream(params.getNewContext().getArmorModifiers())
			.filter(modifier -> !(modifier instanceof StaticArmourModifier) || !((StaticArmourModifier) modifier).isChainsaw())
			.collect(Collectors.toSet());
		params.getNewContext().clearArmorModifiers();
		params.getNewContext().addArmorModifiers(modifiers);
		super.prepareArmourParams(params);
	}

	@Override
	SkillUse skillUse() {
		return SkillUse.INCREASE_CHAINSAW_DAMAGE;
	}
}
