package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	private final List<VersionChangeList> versions = new ArrayList<>();

	public static final ChangeList INSTANCE = new ChangeList();

	public ChangeList() {

		versions.add(new VersionChangeList("2.3.0")
			.addImprovement("Added \"What's new?\" dialog")
			.addBugfix("Prevent throwing throw/kick player that was injured too severe by Animal Savagery")
			.addBugfix("Prevent apothecary usage on zapped players")
			.addBugfix("Do not use Chainsaw modifier when player throws regular block")
			.addBugfix("Prevent overflow in 2016 petty cash dialog")
			.addBugfix("Preserve labels in replay mode when playing/moving backwards")
			.addBugfix("Potential fix for missing stat upgrades")
			.addBugfix("Allow Chainsaw players to continue blitz move after performing regular block")

		);

	}

	public List<VersionChangeList> getVersions() {
		return versions;
	}

	public String fingerPrint() {
		return String.valueOf(versions.get(0).hashCode());
	}
}
