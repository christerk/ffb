package com.fumbbl.ffb;

public enum ApothecaryStatus implements INamedObject {

	NO_APOTHECARY("noApothecary"), DO_REQUEST("doRequest"), WAIT_FOR_APOTHECARY_USE("waitForApothecaryUse"),
	WAIT_FOR_APOTHECARY_CHOICE("waitForApothecaryChoice"),
	USE_APOTHECARY("useApothecary"), DO_NOT_USE_APOTHECARY("doNotUseApothecary"), RESULT_CHOICE("resultChoice"),
	WAIT_FOR_IGOR_USE("waitForIgorUse"), USE_IGOR("useIgor"), DO_NOT_USE_IGOR("doNotUseIgor"),
	WAIT_FOR_GETTING_EVEN("waitForGettingEven");

	private final String fName;

	ApothecaryStatus(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
