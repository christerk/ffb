package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.mechanics.SkillMechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.HashSet;

public class LoneFoulerModification extends RerollArmourModification {

	private final StallingExtension stallingExtension;

	public LoneFoulerModification() {
		this(new StallingExtension());
	}

	LoneFoulerModification(StallingExtension stallingExtension) {
		super(new HashSet<Class<? extends InjuryType>>() {{
			add(Foul.class);
			add(FoulForSpp.class);
			add(FoulWithChainsaw.class);
			add(FoulForSppWithChainsaw.class);
		}});
		this.stallingExtension = stallingExtension;
	}

	@Override
	protected boolean tryArmourRollModification(ModificationParams params) {
		Game game = params.getGameState().getGame();
		ModifiedInjuryContext context = params.getNewContext();

		if (context.fArmorRoll == null) {
			return false;
		}

		Player<?> attacker = game.getActingPlayer().getPlayer();
		Player<?> defender = game.getPlayerById(context.fDefenderId);

		SkillMechanic mechanic =
			(SkillMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SKILL.name());

		int offensiveAssists = UtilPlayer.findOffensiveFoulAssists(game, attacker, defender, mechanic);
		int defensiveAssists = UtilPlayer.findDefensiveFoulAssists(game, attacker, defender);
		if (offensiveAssists > 0 || defensiveAssists > 0) {
			return false;
		}

		// a broken armour roll showing a double gets the fouler sent off, so the coach might prefer to re-roll it
		boolean spottedDouble = context.fArmorRoll[0] == context.fArmorRoll[1];

		return !context.isArmorBroken() || spottedDouble
			|| stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker);
	}

}
