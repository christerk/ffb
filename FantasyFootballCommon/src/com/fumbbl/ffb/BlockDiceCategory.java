package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class BlockDiceCategory extends DiceCategory {
	
	public BlockDiceCategory(){
		super.name = "Block";
		super.diceType = 6;
	}
	
	@Override
	public String Text(Game game) {
		return BlockEnums.FindNameFromRoll(testRoll);
	}
	
	
	static Integer CommandToDiceRoll(String command, Game game, Team team) {
		return BlockEnums.FindRollFromName(command);
	}
	
	public static boolean IsCommandValid(String command, Game game, Team team) {
		return CommandToDiceRoll(command, game, team) != -1;
	}	
	
	@Override
	public boolean ParseCommand(String command, Game game, Team team) {
		super.testRoll = CommandToDiceRoll(command, game, team);
		return super.testRoll != -1;
	}
	
	private enum BlockEnums {
		
		PLAYERDOWN(1, "playerdown"),
		SKULL(1, "skull"),
		DOWN(1, "down"),
		
		BOTHDOWN(2, "bothdown"),
		BD(2, "bd"),
		BOTH(2, "both"),
		
		PUSHBACK1(3, "pushback"),
		PUSHBACK2(4, "pushback"),
		PB(4, "pb"),
		PUSH(4, "push"),
	
		STUMBLE(5, "stumble"),
		PBP(5, "pbp"),
		PBPOW(5, "pbpow"),
		
		POW(6, "pow");
		
		private final int roll;
		private final String text;
		
		BlockEnums(Integer roll, String text){
			this.roll = roll;
			this.text = text;
		}
		
		public static String FindNameFromRoll(int roll) {
			for(BlockEnums e : values()) {
				if(e.roll == roll) {
					return e.text;
				}
			}
			return "Invalid";
		}
		
		public static int FindRollFromName(String name) {
			for(BlockEnums e : values()) {
				if(e.text.equals(name)) {
					return e.roll;
				}
			}
			return -1;	
		}	
	}
}
