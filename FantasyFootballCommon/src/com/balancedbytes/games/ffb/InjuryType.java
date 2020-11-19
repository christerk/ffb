package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;

/**
 * 
 * @author Kalimar
 */
public abstract class InjuryType implements INamedObject {
  
  private String name;
  private boolean worthSpps;
  private SendToBoxReason sendToBoxReason = null;
  
  protected InjuryContext injuryContext;
  
  protected InjuryType(String pName, boolean pWorthSpps, SendToBoxReason pSendToBoxReason) {
	  name = pName;
	  worthSpps = pWorthSpps;
	  sendToBoxReason = pSendToBoxReason;
	  injuryContext = new InjuryContext(); 
  }

  public String getName() {
    return name;
  }
  
  public boolean isWorthSpps() {
		return worthSpps;
	}
  
  public SendToBoxReason sendToBoxReason() {
	  return sendToBoxReason;
  }
  
  public boolean isCausedByOpponent() { return false; }
  public boolean canUseApo() { return true; }

  
  public InjuryContext getInjuryContext() { return injuryContext; }
  
  public abstract InjuryContext handleInjury(Game game, Player<?> pAttacker, Player<?> pDefender,
			FieldCoordinate pDefenderCoordinate, InjuryContext pOldInjuryContext, ApothecaryMode pApothecaryMode);

  public void reportInjury(StringBuilder status, int indent, Player<?> attacker, Player<?> defender)
  {
	  
  }

  
}
