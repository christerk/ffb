package com.balancedbytes.games.ffb;

public enum KickoffResult implements IEnumWithId, IEnumWithName {
  
  GET_THE_REF(2, "Get the Ref", "Get the Ref", "Each coach receives a free bribe."),
	RIOT(3, "Riot", "Riot", "The referee adjusts the clock after the riot clears."),
	PERFECT_DEFENCE(4, "Perfect Defence", "Perfect Defence", "The kicking team may reorganize its players."),
	HIGH_KICK(5, "High Kick", "High Kick", "A player on the receiving team may try to catch the ball directly."),
	CHEERING_FANS(6, "Cheering Fans", "Cheering Fans", "The team with the most enthusiastic fans gains a re-roll."),
	WEATHER_CHANGE(7, "Weather Change", "Weather Change", "The weather changes suddenly."),
	BRILLIANT_COACHING(8, "Brilliant Coaching", "Brilliant Coaching", "The team with the best coaching gains a re-roll."),
	QUICK_SNAP(9, "Quick Snap", "Quick Snap!", "The offence may reposition their players 1 square each."),
	BLITZ(10, "Blitz", "Blitz!", "The defence receives a free turn for moving and blitzing."),
	THROW_A_ROCK(11, "Throw a Rock", "Throw a Rock", "A random player is hit by a rock and suffers an injury."),
	PITCH_INVASION(12, "Pitch Invasion", "Pitch Invasion", "Random players are being stunned by the crowd.");

	private int fId;
  private String fName;
	private String fTitle;
	private String fDescription;
	
	private KickoffResult(int pId, String pName, String pTitle, String pDescription) {
		fId = pId;
		fName = pName;
		fTitle = pTitle;
		fDescription = pDescription;
	}
	
  public int getId() {
    return fId;
  }
  
	public String getName() {
		return fName;
	}
	
	public String getTitle() {
    return fTitle;
  }
	
	public String getDescription() {
		return fDescription;
	}
  
}
