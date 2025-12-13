package com.fumbbl.ffb.server.injury.modification;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.BlockProne;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.ProjectileVomit;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;

import java.util.HashSet;

public class SavageMaulingModification extends InjuryContextModification<ModificationParams> {

	public SavageMaulingModification() {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Block.class);
			add(BlockStunned.class);
			add(BlockProne.class);
			add(Foul.class);
			add(FoulForSpp.class);
			add(ProjectileVomit.class);
			add(Stab.class);
		}});
	}

	@Override
	protected ModificationParams params(GameState gameState, ModifiedInjuryContext newContext, InjuryType injuryType) {
		return new ModificationParams(gameState, newContext, injuryType);
	}

	@Override
	protected boolean modifyInjuryInternal(ModifiedInjuryContext injuryContext, GameState gameState) {
		injuryContext.setInjuryRoll(gameState.getDiceRoller().rollInjury());
		injuryContext.setInjury(interpretInjury(gameState, injuryContext));

		return true;
	}

	@Override
	protected boolean tryInjuryModification(Game game, InjuryContext injuryContext, InjuryType injuryType) {
		return (!injuryContext.isCasualty() || isSpottedFoul(injuryContext, injuryType)
			|| (!differentTeams(game, injuryContext) && injuryContext.getApothecaryMode() != ApothecaryMode.ANIMAL_SAVAGERY && !injuryContext.fInjury.isStunned())
			&& game.getFieldModel().getPlayerState(game.getActingPlayer().getPlayer()).hasTacklezones());
	}

	private boolean isSpottedFoul(InjuryContext injuryContext, InjuryType injuryType) {
		return injuryContext.fInjuryRoll != null && injuryContext.fInjuryRoll[0] == injuryContext.fInjuryRoll[1] && injuryType.isFoul();
	}

	@Override
	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return true;
	}

	@Override
	protected SkillUse skillUse() {
		return SkillUse.RE_ROLL_INJURY;
	}
}
