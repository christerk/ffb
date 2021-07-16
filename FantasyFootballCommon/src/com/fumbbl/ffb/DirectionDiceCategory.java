package com.fumbbl.ffb;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fumbbl.ffb.FactoryType.Factory;
import com.fumbbl.ffb.factory.DirectionFactory;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.model.Team;


public class DirectionDiceCategory extends DiceCategory {
		
	public DirectionDiceCategory(){
		super.name = "Direction";
		super.diceType = 8;
	}
	
	@Override
	public String text(Game game) {
		return DirectionEnums.findDirectionFromRoll(game, testRoll).getName();
	}
	
	@Override
	public boolean parseCommand(String command, Game game, Team team) {
		super.testRoll = commandToDieRoll(command, game, team);
		return super.testRoll != -1;
	}
	
	static Integer commandToDieRoll(String command, Game game, Team team) {
		return DirectionEnums.findRollFromName(command, game, team);
	}
	
	public static boolean isCommandValid(String command, Game game, Team team) {
		return commandToDieRoll(command, game, team) != 0;
	}	
		
	private enum DirectionEnums {
		
		N(Direction.NORTH, new ArrayList<>(Arrays.asList("n", "north")), 1),
		NE(Direction.NORTHEAST, new ArrayList<>(Arrays.asList("ne", "northeast")), 2),
		E(Direction.EAST, new ArrayList<>(Arrays.asList("e", "east")), 3),
		SE(Direction.SOUTHEAST, new ArrayList<>(Arrays.asList("se", "southeast")), 4),
		S(Direction.SOUTH, new ArrayList<>(Arrays.asList("s", "south")), 5),
		SW(Direction.SOUTHWEST, new ArrayList<>(Arrays.asList("sw", "southwest")), 6),
		W(Direction.WEST, new ArrayList<>(Arrays.asList("w", "west")), 7),
		NW(Direction.NORTHWEST, new ArrayList<>(Arrays.asList("nw", "northwest")), 8);
		
		private final Direction direction;
		private final List<String> text;
		private final int roll;
		
		DirectionEnums(Direction direction, List<String> text, int roll){
			this.direction = direction;
			this.text = text;
			this.roll = roll;
		}
		
		DirectionEnums transform() {
			if(this.direction == Direction.NORTHEAST) { return fromDirection(Direction.NORTHWEST);}
			else if(this.direction == Direction.SOUTHEAST) { return fromDirection(Direction.SOUTHWEST);}
			else if(this.direction == Direction.NORTHWEST) { return fromDirection(Direction.NORTHEAST);}
			else if(this.direction == Direction.SOUTHWEST) { return fromDirection(Direction.SOUTHEAST);}
			return this;
		}
		
		private static DirectionEnums fromDirection(Direction direction) {
			for(DirectionEnums e : values()) {
				if(e.direction ==direction) {
					return e;
				}
			}
			return N;
		}
		
		public static Direction findDirectionFromRoll(Game game, int roll) {
			Direction direction = game.<DirectionFactory>getFactory(Factory.DIRECTION).forRoll(roll);
			for(DirectionEnums e : values()) {
				if(e.direction == direction) {
					return e.direction;
				}
			}
			return Direction.NORTH;
		}
				
		public static int findRollFromName(String name, Game game, Team team) {
			for(DirectionEnums e : values()) {
				if(e.text.contains(name.toLowerCase())) {
					if(team == game.getTeamAway()) {
						return e.transform().roll;
					}else {
						return e.roll;
					}
				}
			}
			return 0;
		}	
	}
}
