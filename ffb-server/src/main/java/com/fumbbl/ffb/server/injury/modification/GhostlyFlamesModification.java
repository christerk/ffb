package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
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
		Game game = params.getGameState().getGame();
		ActingPlayer actingPlayer = game.getActingPlayer();
		return !params.getNewContext().isArmorBroken() && actingPlayer != null && actingPlayer.getPlayerAction().isBlitzing()
			&& actingPlayer.getPlayerId().equals(params.getNewContext().fAttackerId)
			&& game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer()).hasTacklezones();
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
	protected SkillUse skillUse() {
		return SkillUse.INCREASE_CHAINSAW_DAMAGE;
	}
}
