package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.model.Player;
import com.balancedbytes.games.ffb.model.Skill;

/**
 * 
 * @author Kalimar
 */
public class InjuryModifier implements INamedObject {

	private String fName;
	private int fModifier;
	private boolean fNigglingInjuryModifier;

	InjuryModifier(String pName, int pModifier, boolean pNigglingInjuryModifier) {
		fName = pName;
		fModifier = pModifier;
		fNigglingInjuryModifier = pNigglingInjuryModifier;
	}

	public int getModifier() {
		return fModifier;
	}

	public String getName() {
		return fName;
	}

	public boolean isNigglingInjuryModifier() {
		return fNigglingInjuryModifier;
	}

	public boolean appliesToContext(Skill skill, InjuryContext context) {
		return true;
	}

	public static class InjuryModifierContext {
		public Game game;
		InjuryContext injuryContext;
		public Player<?> attacker;
		public Player<?> defender;
		public boolean isStab;
		public boolean isFoul;

		public InjuryModifierContext(Game game, InjuryContext injuryContext, Player<?> attacker, Player<?> defender,
				boolean isStab, boolean isFoul) {
			this.game = game;
			this.injuryContext = injuryContext;
			this.attacker = attacker;
			this.defender = defender;
			this.isStab = isStab;
			this.isFoul = isFoul;
		}
	}

	public boolean appliesToContext(InjuryModifierContext context) {
		return true;
	}

}
