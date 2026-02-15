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
