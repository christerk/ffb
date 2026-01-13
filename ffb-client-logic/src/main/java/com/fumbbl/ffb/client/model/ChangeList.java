package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {
		versions.add(new VersionChangeList("Future")
			.addFeature("Animal Savagery, including option to toggle action loss after lashing out")
			.addFeature("Dwarfen Scourge (Star Ivan ‘the Animal’ Deathshroud)")
			.addFeature("Violent Innovator: Spp for Stab, Chainsaw, Breath Fire, KTM and Bombardier")
			.addFeature("Lethal Flight: Works with KTM")
			.addBugfix("Fixed crash when CAS had no attacker")
			.addFeature("Slashing Nails (Roxanna Darknail)")
			.addFeature("Frenzied Rush (Glart Smashrip)")
		);

		versions.add(new VersionChangeList("2026-01-08 B")
			.addFeature(
				"Bombardier: Bombs dont explode on a 4+ when caught anymore and bounce on empty square (with game option)")
			.addFeature("Shadowing")
		);

		versions.add(new VersionChangeList("2026-01-08")
			.addFeature("Safe Pass optional")
			.addBugfix("Add Iron Hard Skin")
		);

		versions.add(new VersionChangeList("2026-01-07")
			.addBugfix("Foul Appearance: Fixed bug where FA was not ending turn during a blitz")
			.addFeature("Allow Bribe after failed AtC")
		);

		versions.add(new VersionChangeList("2026-01-06")
			.addFeature("Nerves Of Steel")
			.addFeature("Arm Bar. Selection Dialog for multiple Arm Bar players and spp.")
			.addFeature("Masters of Undeath -> Also for multi block")
			.addFeature("Plague Ridden -> Also for multi block")
		);

		versions.add(new VersionChangeList("2026-01-05")
			.addFeature("Blitz targets need to be in theoretical range")
			.addBugfix("Remove blitz and gaze from actions to change to move in case of bloodlust failure")
			.addBugfix("Getting Even on Multi Block did not give selection for both player types")
			.addBugfix(
				"Various multi block fixes for double attacker down where only the first result was apoed/regenerated and resulted in player going to reserve despite a remaining injury")
		);

		versions.add(new VersionChangeList("3.0.0").setDescription("First version of 2025 rules, a.k.a. 3rd Season")
			.addBugfix("Fixed crash when using Pile Driver foul")
			.addFeature("Animosity")
			.addFeature("Hatred")
			.addFeature("Getting Even")
			.addBugfix("Brawler not working on both frenzy or multi block rolls")
			.addBugfix("Lethal Flight is only applied when the thrown player is standing and not distracted")
			.addBugfix("Swoop is only applied when the thrown player is standing and not distracted")
			.addFeature("KTM: can target prone/distracted teammates; no Mighty Blow on fumble, Stun becomes KO")
			.addFeature("Special block actions are now top level actions, can still be used on blitz as before")
			.addFeature("No more stab on multi block")
			.addFeature("MB and Claw work on both down and skull (requires game option to be added)")
			.addFeature("MB can be used against Chainsaw players")
			.addFeature("Kick Team-Mate")
		);


	}

	public List<VersionChangeList> getVersions() {
		return versions;
	}

	public String fingerPrint() {
		return String.valueOf(versions.get(0).hashCode());
	}
}
