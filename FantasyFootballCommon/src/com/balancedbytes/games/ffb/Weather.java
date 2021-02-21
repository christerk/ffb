package com.balancedbytes.games.ffb;

import com.balancedbytes.games.ffb.model.Game;
import com.balancedbytes.games.ffb.modifiers.IRollModifier;

import java.util.Collection;
import java.util.HashSet;

public enum Weather implements INamedObject {

	SWELTERING_HEAT("Sweltering Heat", "heat",
			"Each player on the pitch may suffer from heat exhaustion on a roll of 1 before the next kick-off."),
	VERY_SUNNY("Very Sunny", "sunny", "A -1 modifier applies to all passing rolls."),
	NICE("Nice Weather", "nice", "Perfect Fantasy Football weather."),
	POURING_RAIN("Pouring Rain", "rain", "A -1 modifier applies to all catch, intercept, or pick-up rolls.") {
		public Collection<IRollModifier> modifier(Game game) {

			return new HashSet<IRollModifier>() {
				private static final long serialVersionUID = 199665269425305196L;
	
				{
					add(PickupModifiers.POURING_RAIN);
				}
			};
		}
	},
	BLIZZARD("Blizzard", "blizzard",
			"Going For It fails on a roll of 1 or 2 and only quick or short passes can be attempted.") {
		public Collection<IRollModifier> modifier(Game game) {
			return new HashSet<IRollModifier>() {
				private static final long serialVersionUID = -6560479669198677254L;
				{
					add(GoForItModifier.BLIZZARD);
				}
			};
		}
	},
	INTRO("Intro", "intro", "No weather at all, but the intro screen shown by the client.");

	private String fName;
	private String fShortName;
	private String fDescription;

	Weather(String pName, String pShortName, String pDescription) {
		fName = pName;
		fShortName = pShortName;
		fDescription = pDescription;
	}

	public String getName() {
		return fName;
	}

	public String getShortName() {
		return fShortName;
	}

	public String getDescription() {
		return fDescription;
	}
}
