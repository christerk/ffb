package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class DiceCategory {
	String name = "General";
	Integer diceType = null;
	Integer testRoll = null;
	
	public String name() { return name; }
	public Integer diceType() { return diceType; }
	public Integer testRoll() { return testRoll; }
	
	public DiceCategory() {
	}
	
	public DiceCategory(Integer diceType) {
		this.diceType = diceType;
	}
	
	public String text(Game game) {
		return testRoll.toString();
	}
	
	public static boolean isCommandValid(String command, Game game, Team team) {
		try {
			Integer.parseInt(command);
			return true;
		}
		catch (NumberFormatException ignored) {
			return false;
		}
	}	
	
	public boolean parseCommand(String command, Game game, Team team) {
		try {
				int testRoll = Integer.parseInt(command);
				this.testRoll = testRoll;
				return true;
			}
		catch (NumberFormatException ignored) {
			return false;
			}
	}
}
