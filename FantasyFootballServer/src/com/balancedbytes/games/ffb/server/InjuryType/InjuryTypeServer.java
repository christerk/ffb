package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
import com.balancedbytes.games.ffb.INamedObject;
import com.balancedbytes.games.ffb.InjuryContext;
import com.balancedbytes.games.ffb.InjuryType;
import com.balancedbytes.games.ffb.PlayerState;
import com.balancedbytes.games.ffb.SendToBoxReason;
import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.ZappedPlayer;
import com.balancedbytes.games.ffb.model.modifier.NamedProperties;
import com.balancedbytes.games.ffb.server.DiceInterpreter;
import com.balancedbytes.games.ffb.server.DiceRoller;
import com.balancedbytes.games.ffb.server.GameState;
import com.balancedbytes.games.ffb.server.step.IStep;
import com.balancedbytes.games.ffb.util.UtilCards;

public abstract class InjuryTypeServer<T extends InjuryType> implements INamedObject  {
	
	T injuryType;
	InjuryContext injuryContext;
	
	InjuryTypeServer(T injuryType)
	{
		this.injuryType = injuryType;
		this.injuryContext = injuryType.injuryContext();
	}

	@Override
	public String getName() { return injuryType.getName(); }
	
	public InjuryContext injuryContext() { return injuryContext; }
	public InjuryType injuryType() { return injuryType; }
	public boolean canUseApo() { return injuryType.canUseApo(); }
	public SendToBoxReason sendToBoxReason() { return injuryType.sendToBoxReason(); }
	

	public abstract InjuryContext handleInjury(IStep step, Game game,GameState gameState, DiceRoller diceRoller, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode);
	
	void setInjury(Player<?> pDefender, GameState gameState,DiceRoller diceRoller)
	{
		DiceInterpreter diceInterpreter = DiceInterpreter.getInstance();
		injuryContext.setInjury(interpretInjury(diceInterpreter, gameState, injuryContext, pDefender instanceof ZappedPlayer));

		if (injuryContext.getPlayerState() == null) {
			injuryContext.setCasualtyRoll(diceRoller.rollCasualty());
			injuryContext.setInjury(diceInterpreter.interpretRollCasualty(injuryContext.getCasualtyRoll()));
			if (UtilCards.hasSkillWithProperty(pDefender, NamedProperties.requiresSecondCasualtyRoll)) {
				injuryContext.setCasualtyRollDecay(diceRoller.rollCasualty());
				injuryContext.setInjuryDecay(diceInterpreter.interpretRollCasualty(injuryContext.getCasualtyRollDecay()));
			}
		}
	}
	
	PlayerState interpretInjury(DiceInterpreter diceInterpreter, GameState gameState, InjuryContext injuryResult, boolean isZapped) {
		if (isZapped) {
			return new PlayerState(PlayerState.BADLY_HURT);
		}
		return diceInterpreter.interpretRollInjury(gameState, injuryContext);
	}

}
