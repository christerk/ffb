package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FactoryType;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.mechanics.Mechanic;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.property.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.mechanic.RollMechanic;
import com.balancedbytes.games.ffb.server.step.IStep;

public abstract class InjuryTypeServer<T extends InjuryType> implements INamedObject {

	T injuryType;
	InjuryContext injuryContext;

	InjuryTypeServer(T injuryType) {
		this.injuryType = injuryType;
		this.injuryContext = injuryType.injuryContext();
	}

	@Override
	public String getName() {
		return injuryType.getName();
	}

	public InjuryContext injuryContext() {
		return injuryContext;
	}

	public InjuryType injuryType() {
		return injuryType;
	}

	public boolean canUseApo() {
		return injuryType.canUseApo();
	}

	public SendToBoxReason sendToBoxReason() {
		return injuryType.sendToBoxReason();
	}

	public boolean fallingDownCausesTurnover() {
		return injuryType.fallingDownCausesTurnover();
	}

	public boolean isStab() {
		return injuryType.isStab();
	}

	public boolean isFoul() {
		return injuryType.isFoul();
	}

	public abstract InjuryContext handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
			Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext,
			ApothecaryMode pApothecaryMode);

	void setInjury(Player<?> pDefender, GameState gameState, DiceRoller diceRoller) {
		RollMechanic mechanic = ((RollMechanic) gameState.getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
		injuryContext
				.setInjury(interpretInjury(gameState));

		if (injuryContext.getPlayerState() == null) {
			injuryContext.setCasualtyRoll(mechanic.rollCasualty(diceRoller));
			injuryContext.setInjury(mechanic.interpretCasualtyRoll(gameState.getGame(), injuryContext.getCasualtyRoll(), pDefender));
			if (pDefender.hasSkillProperty(NamedProperties.requiresSecondCasualtyRoll)) {
				injuryContext.setCasualtyRollDecay(mechanic.rollCasualty(diceRoller));
				injuryContext.setInjuryDecay(mechanic.interpretCasualtyRoll(gameState.getGame(), injuryContext.getCasualtyRollDecay(), pDefender));
			}
		}
	}

	PlayerState interpretInjury(GameState gameState) {

		RollMechanic rollMechanic = (RollMechanic) gameState.getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());

		return rollMechanic.interpretInjuryRoll(gameState.getGame(), injuryContext);
	}

}
