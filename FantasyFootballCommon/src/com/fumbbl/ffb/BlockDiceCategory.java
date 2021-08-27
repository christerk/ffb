package com.fumbbl.ffb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;

public class BlockDiceCategory extends DiceCategory {
	
	public BlockDiceCategory(){
		super.name = "Block";
		super.diceType = 6;
	}
	
	@Override
	public String text(Game game) {
		return BlockEnums.findNameFromRoll(testRoll);
	}
	
	
	static Integer commandToDiceRoll(String command, Game game, Team team) {
		return BlockEnums.findRollFromName(command);
	}
	
	public static boolean isCommandValid(String command, Game game, Team team) {
		return commandToDiceRoll(command, game, team) != 0;
	}	
	
	@Override
	public boolean parseCommand(String command, Game game, Team team) {
		super.testRoll = commandToDiceRoll(command, game, team);
		return super.testRoll != 0;
	}
	
	private enum BlockEnums {
		
		PLAYERDOWN(1, new ArrayList<>(Arrays.asList("playerdown", "skull", "down"))),
		BOTHDOWN(2, new ArrayList<>(Arrays.asList("bothdown", "bd", "both"))),
		PUSHBACK1(3, new ArrayList<>(Arrays.asList("pushback", "pb", "push"))),
		PUSHBACK2(4, new ArrayList<>(Arrays.asList("pushback"))),
		STUMBLE(5,  new ArrayList<>(Arrays.asList("stumble", "pbp", "pbpow"))),
		POW(6, new ArrayList<>(Arrays.asList("pow")));
		
		private final int roll;
		private final List<String> text;
		
		BlockEnums(Integer roll, List<String> text){
			this.roll = roll;
			this.text = text;
		}
		
		public static String findNameFromRoll(int roll) {
			for(BlockEnums e : values()) {
				if(e.roll == roll) {
					return e.text.get(0);
				}
			}
			return "Invalid";
		}
		
		public static int findRollFromName(String name) {
			for(BlockEnums e : values()) {
				if(e.text.contains(name.toLowerCase())) {
					return e.roll;
				}
			}
			return 0;	
		}	
	}
}
