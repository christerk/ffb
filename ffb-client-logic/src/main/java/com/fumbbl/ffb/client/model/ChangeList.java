package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("3.0.1")
			.addBugfix("Stalling: No stalling did not grant cash bonus")
			.addImprovement("Stalling: On turn 7+ do not roll for stalling")
			.addBugfix("Do not offer Forgo for prone players")
			.addBugfix("Jump: Declining re-roll granted a free re-roll")
			.addBugfix("Hypnotic Gaze: rushing twice would end activation before selecting the target")
			.addBugfix("Brilliant Coaching: Tied result did give no re-roll to either team")
			.addImprovement("iron Man: Only players with AV 10+ or less are eligible")
			.addBugfix("Under Scrutiny: Only triggers for av breaks")
			.addBugfix("Stripball: no longer works against Stand Firm/Rooted players")
			.addBugfix("Master Chef was rolled twice also stealing Leader re-rolls")
			.addBugfix("Selected kick-off results for overtime did not work")
			.addBugfix("Steady Footing: Attacker blocks defender with both down, defender uses Steady Footing Successfully while attacker fell down, did not cause a turnover")
		);

		versions.add(new VersionChangeList("3.0.0")
			.setDescription("First version of 2025 rules, a.k.a. 3rd Season - beware of bugs"));
	}

	public List<VersionChangeList> getVersions() {
		return versions;
	}

	public String fingerPrint() {
		return String.valueOf(versions.get(0).hashCode());
	}
}
