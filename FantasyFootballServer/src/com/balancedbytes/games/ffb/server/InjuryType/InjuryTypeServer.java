package com.balancedbytes.games.ffb.server.InjuryType;

import com.balancedbytes.games.ffb.ApothecaryMode;
import com.balancedbytes.games.ffb.FieldCoordinate;
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

abstract class InjuryTypeServer extends InjuryType {

		protected GameState gameState;
		protected Game game;
		protected DiceRoller diceRoller;
		protected DiceInterpreter diceInterpreter;
		
		public InjuryTypeServer(IStep step, String pName, boolean pWorthSpps, SendToBoxReason pSendToBoxReason) {
			super(pName, pWorthSpps, pSendToBoxReason);
			
			gameState = step.getGameState();
			gameState.getGame();
			diceRoller = gameState.getDiceRoller();
			diceInterpreter = DiceInterpreter.getInstance();
		}
		
		  protected void setInjury(Player<?> pDefender)
		  {
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
		  
		  @Override
		  public abstract InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
					FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode);	
		  

			protected PlayerState interpretInjury(DiceInterpreter diceInterpreter, GameState gameState, InjuryContext injuryResult, boolean isZapped) {
				if (isZapped) {
					return new PlayerState(PlayerState.BADLY_HURT);
				}
				return diceInterpreter.interpretRollInjury(gameState, injuryContext);
			}
		
	}