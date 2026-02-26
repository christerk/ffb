package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("3.1.1")
			.addBugfix("Reloading during kick off sequence was broken")
			.addBugfix("Steady Footing after being pushed on ball did not bounce the ball")
			.addBugfix("Hypnotic Gaze triggers Foul Appearance")
			.addBugfix("Black Ink and Zoat Gaze are only available if non-distracted players are in range")
			.addBugfix("Punt was not available when rushes were exhausted")
			.addBugfix("High Kick with no open players did not skip sequence")
			.addImprovement("Skip Pick Me Up in last turn of half")
			.addBugfix("When Mascot re-roll was available without a team re-roll regular re-roll dialog did not react to mascot button and block dialog did not offer mascot")
			.addBugfix("Sprint was not considered when calculating blitz range")
		);

		versions.add(new VersionChangeList("3.1.0")
			.addBugfix("Stalling: No stalling did not grant cash bonus")
			.addImprovement("Stalling: On turn 7+ do not roll for stalling")
			.addBugfix("Do not offer Forgo for prone players")
			.addBugfix("Jump: Declining re-roll granted a free re-roll")
			.addBugfix("Hypnotic Gaze: rushing twice would end activation before selecting the target")
			.addBugfix("Brilliant Coaching: Tied result did give no re-roll to either team")
			.addImprovement("iron Man: Only players with AV 10+ or less are eligible")
			.addBugfix("Under Scrutiny: Only triggers for av breaks")
			.addBugfix("Strip Ball: no longer works against Stand Firm/Rooted players")
			.addBugfix("Master Chef was rolled twice also stealing Leader re-rolls")
			.addBugfix("Selected kick-off results for overtime did not work")
			.addBugfix("Steady Footing: Attacker blocks defender with both down, defender uses Steady Footing Successfully while attacker fell down, did not cause a turnover")
			.addImprovement("TTM and KTM: reroll choice for Subpar results")
			.addBugfix("Solid Defence: During player selection it was able to move players around")
			.addBugfix("Permanent injuries were not removed by regeneration")
			.addBugfix("Charge: During kickoff blitz, Dodge and Rush skill re-rolls were not available")
			.addBugfix("Hypnotic Gaze + Bloodlust: prone players now can move/feed after failed Bloodlust instead of auto-gazing and ending activation")
			.addBugfix("Fixed wording for \"Under Scrutiny\"")
			.addImprovement("Add strip ball cancel message")
			.addBugfix("Chomped state was not removed if chomper blocked chompee and rolled a skull")
			.addFeature("Implement concession rules")
			.addBugfix("Dauntless has to be handled before horns")
			.addBugfix("Support multiple cheering fans assist per team")
			.addBugfix("Ensure only players in reserves can be selected for prayers")
			.addFeature("Support icon set index for player icons")
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
