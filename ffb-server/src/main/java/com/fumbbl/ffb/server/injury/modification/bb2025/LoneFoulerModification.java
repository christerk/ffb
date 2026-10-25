package com.fumbbl.ffb.server.injury.modification.bb2025;

import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.FoulForSpp;
import com.fumbbl.ffb.injury.FoulForSppWithChainsaw;
import com.fumbbl.ffb.injury.FoulWithChainsaw;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.ModifiedInjuryContext;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.injury.modification.ModificationParams;
import com.fumbbl.ffb.server.step.bb2025.shared.StallingExtension;

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

		if (game.isActive(NamedProperties.foulBreaksArmourWithoutRoll) || context.fArmorRoll == null) {
			return false;
		}

		// a broken armour roll showing a double gets the fouler sent off, so the coach might prefer to re-roll it
		boolean spottedDouble = context.fArmorRoll[0] == context.fArmorRoll[1];
		Player<?> attacker = game.getPlayerById(context.fAttackerId);

		return !context.isArmorBroken() || spottedDouble
			|| stallingExtension.wouldEndOfTurnTriggerStallingRoll(game, attacker);
	}

	@Override
	protected boolean modifyArmourInternal(ModificationParams params) {
		// the re-roll is evaluated from scratch without any assists, so modifiers copied from the first roll must go
		params.getNewContext().clearArmorModifiers();
		return super.modifyArmourInternal(params);
	}

	@Override
	public boolean ignoresFoulAssists() {
		return true;
	}
}
