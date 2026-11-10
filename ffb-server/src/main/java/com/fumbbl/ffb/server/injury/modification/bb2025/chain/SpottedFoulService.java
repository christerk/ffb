package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.UtilGameOption;

/**
 * BB2025 referee rule: whether the dice of a foul are spotted by the referee. Shared by {@code StepReferee} (via the
 * Sneaky Git behaviour) and the injury chain nodes so both cannot diverge.
 */
public class SpottedFoulService {

	public boolean spottedByDice(Game game, InjuryContext injuryContext, boolean attackerHasSneakyGit) {
		boolean spotted = false;
		int[] armorRoll = injuryContext.getArmorRoll();
		if (!game.isActive(NamedProperties.foulBreaksArmourWithoutRoll) && armorRoll != null
			&& (!attackerHasSneakyGit || injuryContext.isArmorBroken()
			|| UtilGameOption.isOptionEnabled(game, GameOptionId.SNEAKY_GIT_BAN_TO_KO))) {
			spotted = armorRoll[0] == armorRoll[1];
		}
		if (!spotted && injuryContext.isArmorBroken()) {
			int[] injuryRoll = injuryContext.getInjuryRoll();
			spotted = injuryRoll != null && injuryRoll[0] == injuryRoll[1];
		}
		return spotted;
	}
}
