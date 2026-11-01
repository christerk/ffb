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
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;
import com.fumbbl.ffb.util.UtilPlayer;

import java.util.Arrays;
import java.util.HashSet;

public class LoneFoulerChainModification extends RerollArmourChainModification {

	private final StallingExtension stallingExtension;

	public LoneFoulerChainModification() {
		this(new StallingExtension(), new InjuryEvaluationService(), new InjuryContextCopyService());
	}

	LoneFoulerChainModification(StallingExtension stallingExtension, InjuryEvaluationService evaluationService,
		InjuryContextCopyService copyService) {
		super(new HashSet<Class<? extends InjuryType>>(Arrays.asList(Foul.class, FoulForSpp.class,
			FoulWithChainsaw.class, FoulForSppWithChainsaw.class)), false, evaluationService, copyService);
		this.stallingExtension = stallingExtension;
	}

	@Override
	protected boolean isApplicableToArmour(GameState gameState, InjuryResolutionState state) {
		Game game = gameState.getGame();
		InjuryContext injuryContext = state.getInjuryContext();

		Player<?> attacker = game.getActingPlayer().getPlayer();
		Player<?> defender = game.getPlayerById(injuryContext.getDefenderId());

		SkillMechanic mechanic =
			(SkillMechanic) game.getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.SKILL.name());

		int offensiveAssists = UtilPlayer.findOffensiveFoulAssists(game, attacker, defender, mechanic);
		int defensiveAssists = UtilPlayer.findDefensiveFoulAssists(game, attacker, defender);
		if (offensiveAssists > 0 || defensiveAssists > 0) {
			return false;
		}

		int[] armourRoll = injuryContext.getArmorRoll();
		// a broken armour roll showing a double gets the fouler sent off, so the coach might prefer to re-roll it
		boolean spottedDouble = armourRoll[0] == armourRoll[1];

		return !injuryContext.isArmorBroken() || spottedDouble
			|| stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker);
	}
}
