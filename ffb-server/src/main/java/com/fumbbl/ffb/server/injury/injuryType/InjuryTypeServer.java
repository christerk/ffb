package com.fumbbl.ffb.server.injury.injuryType;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SendToBoxReason;
import com.fumbbl.ffb.injury.InjuryType;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.mechanics.Mechanic;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.server.DiceRoller;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.mechanic.RollMechanic;
import com.fumbbl.ffb.server.step.IStep;

public abstract class InjuryTypeServer<T extends InjuryType> implements INamedObject {

	T injuryType;
	protected InjuryContext injuryContext;

	protected InjuryTypeServer(T injuryType) {
		this.injuryType = injuryType;
		this.injuryContext = new InjuryContext();
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

	public void setFailedArmourPlacesProne(boolean flag) {
		injuryType.setFailedArmourPlacesProne(flag);
	}

	public boolean isStab() {
		return injuryType.isStab();
	}

	public boolean isFoul() {
		return injuryType.isFoul();
	}

	public boolean isVomitLike() {
		return injuryType.isVomitLike();
	}

	public boolean stunIsTreatedAsKo() {
		return false;
	}

	public abstract void handleInjury(IStep step, Game game, GameState gameState, DiceRoller diceRoller,
	                                  Player<?> pAttacker, Player<?> pDefender, FieldCoordinate pDefenderCoordinate,
	                                  FieldCoordinate fromCoordinate, InjuryContext pOldInjuryContext,
	                                  ApothecaryMode pApothecaryMode);

	protected void setInjury(Player<?> pDefender, GameState gameState, DiceRoller diceRoller) {
		setInjury(pDefender, gameState, diceRoller, injuryContext);
	}

	void setInjury(Player<?> pDefender, GameState gameState, DiceRoller diceRoller, InjuryContext injuryContext) {
		RollMechanic mechanic = ((RollMechanic) gameState.getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name()));
		injuryContext
			.setInjury(interpretInjury(gameState, injuryContext));

		if (injuryContext.getPlayerState() == null) {
			injuryContext.setCasualtyRoll(mechanic.rollCasualty(diceRoller));
			injuryContext.setInjury(mechanic.interpretCasualtyRollAndAddModifiers(gameState.getGame(), injuryContext, pDefender, false));
			if (pDefender.hasSkillProperty(NamedProperties.requiresSecondCasualtyRoll)) {
				injuryContext.setCasualtyRollDecay(mechanic.rollCasualty(diceRoller));
				injuryContext.setInjuryDecay(mechanic.interpretCasualtyRollAndAddModifiers(gameState.getGame(), injuryContext, pDefender, true));
			}
		}
	}

	PlayerState interpretInjury(GameState gameState, InjuryContext injuryContext) {

		RollMechanic rollMechanic = (RollMechanic) gameState.getGame().getFactory(FactoryType.Factory.MECHANIC).forName(Mechanic.Type.ROLL.name());

		return rollMechanic.interpretInjuryRoll(gameState.getGame(), injuryContext);
	}

}
