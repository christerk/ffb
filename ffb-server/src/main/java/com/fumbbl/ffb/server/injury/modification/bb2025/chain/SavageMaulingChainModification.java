package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

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
import com.fumbbl.ffb.model.Game;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Re-rolls the injury roll: to improve it against an opponent, to avoid a spotted foul or to soften the injury of a
 * team-mate.
 */
public class SavageMaulingChainModification extends ChainableInjuryModification {

	public SavageMaulingChainModification() {
		super(validTypes());
	}

	private static Set<Class<? extends InjuryType>> validTypes() {
		Set<Class<? extends InjuryType>> types = new HashSet<>();
		types.add(Block.class);
		types.add(BlockStunned.class);
		types.add(BlockProne.class);
		types.add(Foul.class);
		types.add(FoulForSpp.class);
		types.add(ProjectileVomit.class);
		types.add(Stab.class);
		return types;
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.INJURY);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.RE_ROLL;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		Game game = context.getGame();
		InjuryContext injuryContext = node.getContext();
		if (!injuryContext.isArmorBroken() || injuryContext.getInjuryRoll() == null) {
			return false;
		}
		return !injuryContext.isCasualty() || isSpottedFoul(injuryContext)
			|| (!differentTeams(game, injuryContext) && injuryContext.getApothecaryMode() != ApothecaryMode.ANIMAL_SAVAGERY
			&& injuryContext.getInjury() != null && !injuryContext.getInjury().isStunned()
			&& actingPlayerHasTacklezones(game, mode));
	}

	private boolean isSpottedFoul(InjuryContext injuryContext) {
		int[] injuryRoll = injuryContext.getInjuryRoll();
		return injuryContext.fInjuryType.isFoul() && injuryRoll[0] == injuryRoll[1];
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		child.setInjuryDice(context.getTree().getPool().injuryReRoll(context.getGameState().getDiceRoller()));
		child.setInjuryReRolled(true);
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.RE_ROLL_INJURY;
	}
}
