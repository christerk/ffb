package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class DiceCategory {
	String name = "General";
	Integer diceType = null;
	Integer testRoll = null;
	
	public String Name() { return name; }
	public Integer DiceType() { return diceType; }
	public Integer TestRoll() { return testRoll; }
	
	public DiceCategory() {
	}
	
	public DiceCategory(Integer diceType) {
		this.diceType = diceType;
	}
	
	public String Text(Game game) {
		return testRoll.toString();
	}
	
	public static boolean IsCommandValid(String command, Game game, Team team) {
		try {
			Integer.parseInt(command);
			return true;
		}
		catch (NumberFormatException ignored) {
			return false;
		}
	}	
	
	public boolean ParseCommand(String command, Game game, Team team) {
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
