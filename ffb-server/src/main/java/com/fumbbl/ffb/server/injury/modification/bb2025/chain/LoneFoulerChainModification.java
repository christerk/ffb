package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SkillMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;
import java.util.Set;

/**
 * Re-rolls the armour of an unassisted foul. Also offered on broken armour showing a double (the fouler would be sent
 * off) or when the end of the turn would trigger a stalling roll.
 */
public class LoneFoulerChainModification extends ArmourReRollChainModification {

	private final StallingExtension stallingExtension;

	public LoneFoulerChainModification() {
		this(new StallingExtension());
	}

	public LoneFoulerChainModification(StallingExtension stallingExtension) {
		super(validTypes());
		this.stallingExtension = stallingExtension;
	}

	private static Set<Class<? extends InjuryType>> validTypes() {
		Set<Class<? extends InjuryType>> types = new HashSet<>();
		types.add(Foul.class);
		types.add(FoulForSpp.class);
		types.add(FoulWithChainsaw.class);
		types.add(FoulForSppWithChainsaw.class);
		return types;
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
		if (!isArmourRolled(node) || !differentTeams(game, injuryContext)) {
			return false;
		}
		Player<?> attacker = game.getPlayerById(ownerId);
		Player<?> defender = context.getDefender(node);
		SkillMechanic mechanic =
			(SkillMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SKILL.name());
		if (UtilPlayer.findOffensiveFoulAssists(game, attacker, defender, mechanic) > 0
			|| UtilPlayer.findDefensiveFoulAssists(game, attacker, defender) > 0) {
			return false;
		}
		int[] armourRoll = injuryContext.getArmorRoll();
		boolean spottedDouble = armourRoll[0] == armourRoll[1];
		return !injuryContext.isArmorBroken() || spottedDouble
			|| stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker);
	}
}
