package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("3.0.2")
			.addBugfix("Lethal Flight is only applied when the thrown player is standing and not distracted")
			.addBugfix("Swoop is only applied when the thrown player is standing and not distracted")
		);

		versions.add(new VersionChangeList("3.0.1")
			.addBugfix("Fixed crash when using Pile Driver foul")
		);

		versions.add(new VersionChangeList("3.0.0").setDescription("First version of 2025 rules, a.k.a. 3rd Season"));


	}

	public List<VersionChangeList> getVersions() {
		return versions;
	}

	public String fingerPrint() {
		return String.valueOf(versions.get(0).hashCode());
	}
}
