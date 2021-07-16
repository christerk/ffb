package com.fumbbl.ffb;
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
	public String Text(Game game) {
		return DirectionEnums.FindDirectionFromRoll(game, testRoll).getName();
	}
	
	@Override
	public boolean ParseCommand(String command, Game game, Team team) {
		super.testRoll = CommandToDiceRoll(command, game, team);
		return super.testRoll != -1;
	}
	
	static Integer CommandToDiceRoll(String command, Game game, Team team) {
		return DirectionEnums.FindRollFromName(command, game, team);
	}
	
	public static boolean IsCommandValid(String command, Game game, Team team) {
		return CommandToDiceRoll(command, game, team) != -1;
	}	
		
	private enum DirectionEnums {
		
		N(Direction.NORTH, "n", 1),
		NE(Direction.NORTHEAST, "ne", 2),
		E(Direction.EAST, "e", 3),
		SE(Direction.SOUTHEAST, "se", 4),
		S(Direction.SOUTH, "s", 5),
		SW(Direction.SOUTHWEST, "sw", 6),
		W(Direction.WEST, "w", 7),
		NW(Direction.NORTHWEST, "nw", 8);
		
		private final Direction direction;
		private final String text;
		private final int roll;
		
		DirectionEnums(Direction direction, String text, int roll){
			this.direction = direction;
			this.text = text;
			this.roll = roll;
		}
		
		DirectionEnums transpose() {
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
		
		public static Direction FindDirectionFromRoll(Game game, int roll) {
			Direction direction = game.<DirectionFactory>getFactory(Factory.DIRECTION).forRoll(roll);
			for(DirectionEnums e : values()) {
				if(e.direction == direction) {
					return e.direction;
				}
			}
			return Direction.NORTH;
		}
				
		public static int FindRollFromName(String name, Game game, Team team) {
			for(DirectionEnums e : values()) {
				if(e.text.equals(name)) {
					if(team == game.getTeamAway()) {
						return e.transpose().roll;
					}else {
						return e.roll;
					}
				}
			}
			return -1;
		}	
	}
}
