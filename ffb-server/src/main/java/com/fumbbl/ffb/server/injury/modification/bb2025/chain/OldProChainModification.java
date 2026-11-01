package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.BlockProne;
import com.fumbbl.ffb.injury.BlockStunned;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.ProjectileVomit;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.report.IReport;
import com.fumbbl.ffb.report.mixed.ReportOldPro;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Re-rolls a single armour die: the lower one to break the armour or, if the injury is self inflicted, the higher
 * one to keep the armour intact. The die is only rolled when the modification is applied.
 */
public class OldProChainModification extends ChainableInjuryModification {

	public OldProChainModification() {
		this(new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	OldProChainModification(InjuryEvaluationService evaluationService, InjuryContextCopyService copyService) {
		super(new HashSet<Class<? extends InjuryType>>(Arrays.asList(Block.class, Chainsaw.class, Foul.class,
			FoulForSpp.class, FoulForSppWithChainsaw.class, FoulWithChainsaw.class, BlockProne.class, BlockStunned.class,
			Stab.class, ProjectileVomit.class)), false, evaluationService, copyService);
	}

	@Override
	public Set<InjuryPhase> phases() {
		return Collections.singleton(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.RE_ROLL;
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.RE_ROLL_SINGLE_ARMOUR_DIE;
	}

	@Override
	public boolean requiresConditionalReRollSkill() {
		return true;
	}

	@Override
	protected boolean allowedForAttackerAndDefenderTeams(Game game, InjuryContext injuryContext) {
		return true;
	}

	@Override
	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		Game game = gameState.getGame();
		InjuryContext injuryContext = state.getInjuryContext();
		boolean selfInflicted = isSelfInflicted(game, injuryContext);

		if (!hasPrerequisite(game, injuryContext)
			|| (injuryContext.isArmorBroken() != selfInflicted && !isSpottedFoul(injuryContext, injuryContext.getArmorRoll()))) {
			return false;
		}

		InjuryContext bestCase = copyService.copy(injuryContext);
		int[] armourRoll = bestCase.getArmorRoll();
		armourRoll[replaceIndex(armourRoll, selfInflicted)] = selfInflicted ? 1 : 6;
		return isSpottedFoul(bestCase, armourRoll) || evaluationService.isArmourBroken(gameState, bestCase) != selfInflicted;
	}

	@Override
	protected void applyToArmour(GameState gameState, InjuryResolutionState state, List<IReport> reports) {
		Game game = gameState.getGame();
		InjuryContext injuryContext = state.getInjuryContext();
		boolean selfInflicted = isSelfInflicted(game, injuryContext);

		int[] armourRoll = injuryContext.getArmorRoll().clone();
		int replaceIndex = replaceIndex(armourRoll, selfInflicted);
		int oldValue = armourRoll[replaceIndex];
		int newValue = gameState.getDiceRoller().rollDice(6);
		armourRoll[replaceIndex] = newValue;
		injuryContext.setArmorRoll(armourRoll);
		injuryContext.setArmorBroken(evaluationService.isArmourBroken(gameState, injuryContext));

		String playerId = selfInflicted ? injuryContext.getDefenderId() : injuryContext.getAttackerId();
		reports.add(new ReportOldPro(playerId, oldValue, newValue, selfInflicted));
	}

	private int replaceIndex(int[] armourRoll, boolean selfInflicted) {
		return armourRoll[0] < armourRoll[1] != selfInflicted ? 0 : 1;
	}

	private boolean hasPrerequisite(Game game, InjuryContext injuryContext) {
		String playerId = StringTool.isProvided(injuryContext.getAttackerId()) ? injuryContext.getAttackerId()
			: injuryContext.getDefenderId();
		return UtilServerReRoll.isProReRollAvailable(game.getPlayerById(playerId), game, null);
	}

	private boolean isSpottedFoul(InjuryContext injuryContext, int[] armourRoll) {
		return armourRoll[0] == armourRoll[1] && injuryContext.getInjuryType().isFoul();
	}

	private boolean isSelfInflicted(Game game, InjuryContext injuryContext) {
		Player<?> attacker = game.getPlayerById(injuryContext.getAttackerId());
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());
		InjuryType injuryType = injuryContext.getInjuryType();

		boolean hurtTeamMate = attacker != null && defender != null && attacker.getTeam() == defender.getTeam();

		return (!StringTool.isProvided(injuryContext.getAttackerId()) && (injuryType.isVomitLike() || injuryType.isChainsaw()))
			|| (hurtTeamMate && injuryContext.getApothecaryMode() != ApothecaryMode.ANIMAL_SAVAGERY);
	}
}
