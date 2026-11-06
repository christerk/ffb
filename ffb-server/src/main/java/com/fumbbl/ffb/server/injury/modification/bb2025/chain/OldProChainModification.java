package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.TurnMode;
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
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.report.mixed.ReportOldPro;
import com.fumbbl.ffb.server.DiceInterpreter;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import com.fumbbl.ffb.util.StringTool;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Re-rolls a single armour die, requires a successful Pro roll. When the injury is self inflicted the highest die is
 * re-rolled to save the armour, otherwise the lowest one to break it. A spotted foul can always be re-rolled.
 */
public class OldProChainModification extends ChainableInjuryModification {

	public OldProChainModification() {
		super(validTypes());
	}

	private static Set<Class<? extends InjuryType>> validTypes() {
		Set<Class<? extends InjuryType>> types = new HashSet<>();
		types.add(Block.class);
		types.add(Chainsaw.class);
		types.add(Foul.class);
		types.add(FoulForSpp.class);
		types.add(FoulForSppWithChainsaw.class);
		types.add(FoulWithChainsaw.class);
		types.add(BlockProne.class);
		types.add(BlockStunned.class);
		types.add(Stab.class);
		types.add(ProjectileVomit.class);
		return types;
	}

	@Override
	public Set<InjuryPhase> phases() {
		return EnumSet.of(InjuryPhase.ARMOUR);
	}

	@Override
	public ModificationEffect effect() {
		return ModificationEffect.RE_ROLL;
	}

	@Override
	public boolean requiresConditionalReRollSkill() {
		return true;
	}

	@Override
	public boolean offeredOnBrokenArmour() {
		return true;
	}

	@Override
	public boolean isApplicable(InjuryChainContext context, InjuryNode node, InjuryPhase phase, String ownerId,
		EligibilityMode mode) {
		Game game = context.getGame();
		InjuryContext injuryContext = node.getContext();
		Player<?> owner = game.getPlayerById(ownerId);
		if (owner == null || !isArmourRolled(node) || !proAvailable(game, owner, mode)) {
			return false;
		}
		boolean selfInflicted = isSelfInflicted(game, injuryContext);
		boolean spotted = isSpottedFoul(injuryContext);
		if (injuryContext.isArmorBroken() != selfInflicted && !spotted) {
			return false;
		}
		return spotted || canChangeResult(context, node, selfInflicted);
	}

	private boolean proAvailable(Game game, Player<?> owner, EligibilityMode mode) {
		if (mode == EligibilityMode.OFFER) {
			return UtilServerReRoll.isProReRollAvailable(owner, game, null);
		}
		return owner.hasSkillProperty(NamedProperties.canRerollOncePerTurn)
			&& game.getActingPlayer().getPlayer() == owner
			&& game.getTurnMode() != TurnMode.DUMP_OFF_PASS_BLOCK
			&& !game.getFieldModel().getPlayerState(owner).hasUsedPro();
	}

	private boolean canChangeResult(InjuryChainContext context, InjuryNode node, boolean selfInflicted) {
		InjuryContext injuryContext = node.getContext();
		int[] armourRoll = injuryContext.getArmorRoll();
		int replaceIndex = replaceIndex(armourRoll, selfInflicted);
		int[] bestCase = armourRoll.clone();
		bestCase[replaceIndex] = selfInflicted ? 1 : 6;
		int[] originalRoll = injuryContext.fArmorRoll;
		boolean originalBroken = injuryContext.isArmorBroken();
		try {
			injuryContext.fArmorRoll = bestCase;
			return DiceInterpreter.getInstance()
				.isArmourBroken(context.getGameState(), injuryContext) != originalBroken;
		} finally {
			injuryContext.fArmorRoll = originalRoll;
			injuryContext.setArmorBroken(originalBroken);
		}
	}

	@Override
	public void apply(InjuryChainContext context, InjuryNode child, InjuryPhase phase, String ownerId) {
		Game game = context.getGame();
		InjuryContext parent = parentContext(context, child);
		boolean selfInflicted = isSelfInflicted(game, parent);
		int[] armourRoll = parent.getArmorRoll().clone();
		int replaceIndex = replaceIndex(armourRoll, selfInflicted);
		int oldValue = armourRoll[replaceIndex];
		int newValue = context.getTree().getPool().singleArmourReRoll(context.getGameState().getDiceRoller());
		armourRoll[replaceIndex] = newValue;
		child.setArmourDice(armourRoll);
		child.setArmourReRolled(true);
		String playerId = selfInflicted ? parent.fDefenderId : parent.fAttackerId;
		child.addEdgeReport(new ReportOldPro(playerId, oldValue, newValue, selfInflicted));
	}

	private int replaceIndex(int[] armourRoll, boolean selfInflicted) {
		return (armourRoll[0] < armourRoll[1]) != selfInflicted ? 0 : 1;
	}

	private boolean isSpottedFoul(InjuryContext injuryContext) {
		int[] armourRoll = injuryContext.getArmorRoll();
		return armourRoll != null && armourRoll[0] == armourRoll[1] && injuryContext.fInjuryType.isFoul();
	}

	private boolean isSelfInflicted(Game game, InjuryContext injuryContext) {
		Player<?> attacker = game.getPlayerById(injuryContext.fAttackerId);
		Player<?> defender = game.getPlayerById(injuryContext.fDefenderId);
		boolean hurtTeamMate = attacker != null && defender != null && attacker.getTeam() == defender.getTeam();
		InjuryType injuryType = injuryContext.fInjuryType;
		return (!StringTool.isProvided(injuryContext.fAttackerId) && (injuryType.isVomitLike() || injuryType.isChainsaw()))
			|| (hurtTeamMate && injuryContext.getApothecaryMode() != ApothecaryMode.ANIMAL_SAVAGERY);
	}

	@Override
	public SkillUse skillUse(InjuryPhase phase) {
		return SkillUse.RE_ROLL_SINGLE_ARMOUR_DIE;
	}
}
