package com.fumbbl.ffb.kickoff.bb2016;

public enum KickoffResult implements com.fumbbl.ffb.kickoff.KickoffResult {

	GET_THE_REF("Get the Ref", "Each coach receives a free bribe."),
	RIOT("Riot", "The referee adjusts the clock after the riot clears."),
	PERFECT_DEFENCE("Perfect Defence", "The kicking team may reorganize its players."),
	HIGH_KICK("High Kick", "A player on the receiving team may try to catch the ball directly."),
	CHEERING_FANS("Cheering Fans", "The team with the most enthusiastic fans gains a re-roll.") {
		@Override
		public boolean isFanReRoll() {
			return true;
		}
	},
	WEATHER_CHANGE("Weather Change", "The weather changes suddenly."),
	BRILLIANT_COACHING("Brilliant Coaching", "The team with the best coaching gains a re-roll.") {
		@Override
		public boolean isCoachReRoll() {
			return true;
		}
	},
	QUICK_SNAP("Quick Snap", "The offence may reposition their players 1 square each."),
	BLITZ("Blitz", "The defence receives a free turn for moving and blitzing."),
	THROW_A_ROCK("Throw a Rock", "A random player is hit by a rock and suffers an injury."),
	PITCH_INVASION("Pitch Invasion", "Random players are being stunned by the crowd.");

	private final String fName;
	private final String fDescription;

	KickoffResult(String pName, String pDescription) {
		fName = pName;
		fDescription = pDescription;
	}

	public String getName() {
		return fName;
	}

	public String getDescription() {
		return fDescription;
	}

	public boolean isFanReRoll() {
		return false;
	}

	public boolean isCoachReRoll() {
		return false;
	}
}
