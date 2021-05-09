package com.fumbbl.ffb.kickoff.bb2020;

public enum KickoffResult implements com.fumbbl.ffb.kickoff.KickoffResult {

	GET_THE_REF("Get the Ref", "Each coach receives a free bribe."),
	TIME_OUT("Time-out", "Turn marker moves back by one if kicking team is on turn 6, 7 or 8 or forward otherwise."),
	SOLID_DEFENCE("Solid Defence", "The kicking team may reorganize D3+3 of its players."),
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
	QUICK_SNAP("Quick Snap", "The offence may reposition D3+3 of their open players 1 square each."),
	BLITZ("Blitz", "The defence receives a free turn for moving and blitzing. TTM is allowed but no team re-rolls can be used."),
	OFFICIOUS_REF("Officious Ref", "A random player gets into an argument with the ref and might be sent off."),
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
