package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {
		versions.add(new VersionChangeList("Future")
		);

		versions.add(new VersionChangeList("2026-02-08")
			.addBugfix("\"Blastin' Solves Everything\" roll-2 flow fixed")
			.addFeature("Violent Innovator with Ball and Chain")
			.addFeature("Swift Brothers")
			.addBugfix("One team's mascot was offered for opposing team on steady footing and other rolls")
			.addBugfix("Catching Kick-Off still caused -1 modifier")
			.addBugfix("Incorporeal was not working on first dodge")
			.addBugfix("Renamed Ivan skill to Dwarven Scourge")
			.addBugfix("Secure The Ball message did not show correct base value")
			.addBugfix("Stalling check did not trigger if ball carrier was blitzing")
			.addFeature("Biased ref: Coach is banned on a natural 1 even with biased ref")
			.addFeature("Throw-Ins: The first square (under template) counts and corner Throw-Ins")
			.addBugfix("Tentacles only works on dodge and jump/leap")
			.addBugfix("Woodland Fury (Star Willow Rosebark")
		);

		versions.add(new VersionChangeList("2026-02-01")
			.addFeature("TTM and Gaze not available from unless game option \"allowSpecialActionsFromProne\" is set")
			.addFeature("Brawler can only be used once per activation unless game option \"allowBrawlerOnBothBlocks\" is set")
			.addFeature("Game option \"askForKickAfterRoll\"")
		);

		versions.add(new VersionChangeList("2026-01-31")
			.addBugfix("Prevent MB to be applied when blocking an opponent using chainsaw special action")
			.addBugfix("No stalling check on turnover")
			.addFeature("\"Excuse Me, Are You a Zoat?\" (Star Zolcath the Zoat)")
			.addFeature("Saboteur")
			.addFeature("Forgo")
			.addBugfix("Stalling did trigger after turnover -> retest other stalling scenarios as well")
			.addBugfix("Getting Even message")
			.addFeature("Diving Tackle: RR needs to be decided before DT prompt (Dodge and Jump).")
			.addFeature("\"Blastin' Solves Everything\" (Star Zzharg Madeye)")
			.addFeature("Ball And Chain")
			.addFeature("Diving Tackle: only triggers when the dodger leaves the tackler's TZ (toggleable via game option).")
			.addFeature("Incorporeal (Star Gretchen Wächter)")
			.addFeature("Lord Of Chaos (Star Lord Borak the Despoiler)")
			.addFeature("Monstrous Mouth")
		);

		versions.add(new VersionChangeList("2026-01-15")
			.addFeature("Animal Savagery, including option to toggle action loss after lashing out")
			.addFeature("Dwarfen Scourge (Star Ivan ‘the Animal’ Deathshroud)")
			.addBugfix("Bombardier: Bombs should now explode after a bounce lands on a player")
			.addFeature("Violent Innovator: Spp for Stab, Chainsaw, Breath Fire, KTM and Bombardier")
			.addFeature("Lethal Flight: Works with KTM")
			.addBugfix("Fixed crash when CAS had no attacker")
			.addFeature("Slashing Nails (Roxanna Darknail)")
			.addFeature("Frenzied Rush (Glart Smashrip)")
			.addFeature("KTM: Spp on a superb throw. Swoop and Bullseye work on KTM")
			.addFeature("Landing SPP")
			.addFeature("Regeneration")
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
