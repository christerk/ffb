package com.fumbbl.ffb;

public enum Weather implements INamedObject {

	SWELTERING_HEAT("Sweltering Heat", "heat"
	),
	VERY_SUNNY("Very Sunny", "sunny"),
	NICE("Nice Weather", "nice"),
	POURING_RAIN("Pouring Rain", "rain"),
	BLIZZARD("Blizzard", "blizzard"
	),
	INTRO("Intro", "intro");

	private final String fName;
	private final String fShortName;

	Weather(String pName, String pShortName) {
		fName = pName;
		fShortName = pShortName;
	}

	public String getName() {
		return fName;
	}

	public String getShortName() {
		return fShortName;
	}

}
