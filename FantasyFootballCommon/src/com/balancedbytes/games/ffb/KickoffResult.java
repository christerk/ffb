package com.balancedbytes.games.ffb;

public enum KickoffResult implements IEnumWithName {
  
  GET_THE_REF("Get the Ref", "Get the Ref", "Each coach receives a free bribe."),
	RIOT("Riot", "Riot", "The referee adjusts the clock after the riot clears."),
	PERFECT_DEFENCE("Perfect Defence", "Perfect Defence", "The kicking team may reorganize its players."),
	HIGH_KICK("High Kick", "High Kick", "A player on the receiving team may try to catch the ball directly."),
	CHEERING_FANS("Cheering Fans", "Cheering Fans", "The team with the most enthusiastic fans gains a re-roll."),
	WEATHER_CHANGE("Weather Change", "Weather Change", "The weather changes suddenly."),
	BRILLIANT_COACHING("Brilliant Coaching", "Brilliant Coaching", "The team with the best coaching gains a re-roll."),
	QUICK_SNAP("Quick Snap", "Quick Snap!", "The offence may reposition their players 1 square each."),
	BLITZ("Blitz", "Blitz!", "The defence receives a free turn for moving and blitzing."),
	THROW_A_ROCK("Throw a Rock", "Throw a Rock", "A random player is hit by a rock and suffers an injury."),
	PITCH_INVASION("Pitch Invasion", "Pitch Invasion", "Random players are being stunned by the crowd.");

  private String fName;
	private String fTitle;
	private String fDescription;
	
	private KickoffResult(String pName, String pTitle, String pDescription) {
		fName = pName;
		fTitle = pTitle;
		fDescription = pDescription;
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
