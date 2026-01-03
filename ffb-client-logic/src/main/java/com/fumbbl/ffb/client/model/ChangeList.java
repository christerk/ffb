package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {
		
		versions.add(new VersionChangeList("3.0.0").setDescription("First version of 2025 rules, a.k.a. 3rd Season")
			.addBugfix("Fixed crash when using Pile Driver foul")
			.addFeature("Animosity")
			.addFeature("Hatred")
			.addFeature("Getting Even")
			.addBugfix("Brawler not working on both frenzy or multi block rolls")
			.addBugfix("Lethal Flight is only applied when the thrown player is standing and not distracted")
			.addBugfix("Swoop is only applied when the thrown player is standing and not distracted")
		);


	}

	public List<VersionChangeList> getVersions() {
		return versions;
	}

	public String fingerPrint() {
		return String.valueOf(versions.get(0).hashCode());
	}
}
