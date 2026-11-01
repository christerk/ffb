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
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.server.GameState;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavageMaulingChainModification extends ChainableInjuryModification {

	public SavageMaulingChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	SavageMaulingChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(new HashSet<Class<? extends InjuryType>>(Arrays.asList(Block.class, BlockStunned.class, BlockProne.class,
			Foul.class, FoulForSpp.class, ProjectileVomit.class, Stab.class)), false, evaluationService, copyService);
	}

	@Override
	public Set<InjuryPhase> phases() {
		return Collections.singleton(InjuryPhase.INJURY);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.RE_ROLL;
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.RE_ROLL_INJURY;
	}

	@Override
	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return true;
	}

	@Override
	protected boolean isApplicableToInjury(GameState gameState, InjuryResolutionState state) {
		Game game = gameState.getGame();
		InjuryContext injuryContext = state.getInjuryContext();
		boolean stunned = injuryContext.getInjury() != null && injuryContext.getInjury().isStunned();
		// same precedence as the legacy SavageMaulingModification: tackle zones are only required for team mates
		return !isCasualty(injuryContext) || isSpottedFoul(injuryContext)
			|| (!differentTeams(game, injuryContext) && injuryContext.getApothecaryMode() != ApothecaryMode.ANIMAL_SAVAGERY
			&& !stunned) && actingPlayerHasTacklezones(game);
	}

	private boolean isSpottedFoul(InjuryContext injuryContext) {
		int[] injuryRoll = injuryContext.getInjuryRoll();
		return injuryRoll[0] == injuryRoll[1] && injuryContext.getInjuryType().isFoul();
	}

	@Override
	protected void applyToInjury(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		InjuryContext injuryContext = state.getInjuryContext();
		injuryContext.setInjuryRoll(gameState.getDiceRoller().rollInjury());
		injuryContext.setInjury(evaluationService.interpretInjury(gameState, injuryContext));
	}
}
