package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {
		versions.add(new VersionChangeList("3.3.0")
			.addImprovement("Added scoreboard icons for the Cheering Fans offensive assist bonus")
			.addImprovement("Updated db connector to most current mariadb client")
			.addBugfix("Master Assassin: Re-rolled Stab armour breaks did not apply the resulting injury")
			.addBugfix("Selecting Fumblerooskie during a foul action could foul the active player instead")
			.addFeature("Infamous Staff - Josef Bugman")
			.addBugfix("Reset button could block hiring star players")
			.addBugfix("Emoji picker disappeared after switching between spectator and replay mode")
			.addFeature("Dwarfen Grit (Star Josef Bugman)")
			.addFeature("Optional client-side captions for spectator-triggered sounds")
			.addFeature("Added game option to disable underdog treasury spending on inducements")
			.addBugfix("Taunt was available while distracted")
			.addBugfix("Projectile Vomit did not end Blitz activation")
			.addFeature("Add game option to toggle grab vs sidestep on blitz behavior")
			.addBugfix("FA triggered after Quick Foul")
			.addBugfix("Using OtB vs G&G pass caused the game to crash if the passer moved on")
			.addImprovement("Used-player marking now shows a dedicated icon for the player who used the team's blitz action")
			.addBugfix("Self inflicted injuries never triggered Getting Even")
			.addBugfix("Star players incorrectly rolled for Getting Even")
			.addImprovement("Moved active cards into the inducements menu to reduce top-level menu width")
			.addImprovement("Added chat command to reset used skills for selected players")
			.addFeature("Added support for I'll Carry You (Stars Grak & Crumbleberry)")
			.addBugfix("Special team re-rolls like Leader or Brilliant Coaching that were saved by Team Captain got converted to regular team re-rolls")
			.addBugfix("Mascot/Loner fail on DT dodge re-roll caused the game to lock up")
			.addBugfix("A manipulated client could submit an out-of-range pass that stalled the game")
			.addBugfix("Conceding in the 2025 ruleset did not let the winning coach assign the awarded touchdowns for SPP")
			.addImprovement("Buying inducements now prompts before closing if petty cash remains that could still buy an inducement")
			.addBugfix("Active team players ending up in the crowd (crowd push, throw team-mate, ball & chain and trap doors) always cause a turnover")
			.addBugfix("Gained Hatred no longer counts as a skill advancement for the player level or the post-concession player loss check")
			.addFeature("Added game option to disable Getting Even")
			.addBugfix("Leader re-roll was granted when Leader player was fielded only for a subsequent drive of a half")
			.addBugfix("Jump up was rolled after Foul Appearance")
			.addBugfix("Foul Appearance fail on a blitz from prone (when target was adjacent) left the blitzing player prone")
			.addBugfix("Conceding teams did not lose their spp")
			.addImprovement("Player choice dialogs now display amount of selected and to be selected players")
			.addBugfix("Plague Ridden also worked for non-block casualties")
		);

		versions.add(new VersionChangeList("3.2.3")
			.addBugfix("Weather Mage effect only lasted until end of drive/opponents next turn")
			.addBugfix(
				"On Linux JVMs past 1.8 it was not possible to close the actions menu by clicking the active player again")
			.addBugfix(
				"Apply confusion flag if player is prone and fails the respective check (Bone Head, Really Stupid, Animal Savagery)")
			.addBugfix("Ball & Chain hit by bomb did roll for armour")
			.addBugfix("Dodgy Snack did not trigger auto marking update")
			.addBugfix("Multiblock did not generate spp")
			.addBugfix("Punt: If direction or distance put the ball out of bounds re-rolling the result did not reset the ball being in bounds")
			.addBugfix("Blessing of Nuffle: Description text was incorrect")
			.addBugfix("With JVMs newer than 8, range rulers did not show the required roll anymore")
			.addBugfix("Gaining additional Hatred results in duplication of existing Hatred skill listings")
			.addBugfix("Bloodlust: When opting to move instead of fouling directly due to failed Bloodlust the game crashed")
			.addBugfix("Missing Zoat and Spite keywords caused Hatred/Getting Even to show Unknown")
		);

		versions.add(new VersionChangeList("3.2.2")
			.addBugfix("All prayer rolls resulted in Blessing of Nuffle")
		);

		versions.add(new VersionChangeList("3.2.1")
			.addBugfix("Disabling timeout button also disabled the turn timer")
			.addBugfix(
				"All ruleset: Touchback with only no ball players could result in the ball not being available for the drive")
			.addBehaviorChange(
				"All ruleset: In case of a touchback with no players or only no ball players placing the ball in a field does not bounce it anymore")
			.addBugfix("\"Did not stall\" message was displayed even if there was no potential stalling")
		);

		versions.add(new VersionChangeList("3.2.0")
			.addBugfix("Banned coach does not affect Brilliant Coaching roll")
			.addBugfix("Prevent staff and technical player types to be eligible to be raised")
			.addBugfix("Safe Pair of Hands did prevent turnovers")
			.addBugfix("Leap was not applied when combined with other positive modifiers like Very Long Legs and the " +
				"resulting modifier was lower than 2")
			.addBugfix("Player with Fend and Taunt was not able to use Taunt")
			.addBugfix("Fumbled KTM did not apply stunty to injury roll")
			.addBugfix("Give and Go did not trigger when a bomb was intercepted/caught")
			.addFeature("Wisdom of the White Dwarf (Star Grombrindal)")
			.addImprovement("Set antialiasing for non-menu text components (mainly affecting Linux environments")
			.addBugfix("Player Markings for 2020 skills caused false positives in 2025 games")
			.addBugfix("Blessing of Nuffle was not applied randomly (and still used the old name)")
			.addBugfix("Thinking Man's Troll could not be used on regeneration re-rolls")
			.addBugfix("Kaboom! did not work on bouncing bomb")
			.addBugfix("Fumblerooski was not reverted when player was held in place by tentacles")
			.addBugfix("Arm Bar against non-dodge players caused a second re-roll option in case of a failed dodge")
			.addFeature("Added game option to turn off timeouts")
			.addBugfix("Timeout did not work for first turn of a drive")
		);

		versions.add(new VersionChangeList("3.1.2")
			.addBugfix("B&C could perform Multi Block if skill was present")
			.addImprovement("Reword B&C knock out message")
			.addBugfix("B&C self cas did not generate spp")
			.addBugfix("For underdog teams with less than 50k treasury the report used treasury was reported incorrectly")
			.addBugfix("Foul Appearance triggered for the first move after blitzing a player with that skill")
			.addBugfix("Monstrous Mouth: On both downs chomp states were not always removed properly")
			.addBugfix("Leader re-roll was not restored if player returned to pitch after KO or surf")
			.addBugfix("Using Safe Pair Of Hands with Wrestle on ball carrier did not prevent turnover")
			.addBehaviorChange("Fallback checkbox for team re-roll on mascot use is now pre-selected")
			.addBugfix("Diving Catch did not trigger for kick-offs")
			.addImprovement(
				"Message about preventing Strip Ball (Stand Firm, Rooted, Chomped) is only shown if player is actually carrying the ball")
			.addBugfix("Lone Fouler did not work for Chainsaw fouls")
			.addBugfix("TTM landing on the ball did allow a pick up")
			.addBugfix("It was possible to move players on the pitch during mvp selection")
			.addBugfix("Brilliant coaching message reported a tie when rolls were equal but ignored modifiers")
			.addImprovement("Technical: Game results are now also loaded from backups if game is not in cache anymore")
			.addBugfix("Eye Gouge: In addition to not assisting, gouged players did also not cancel opposing assist")
			.addBugfix("Steady Footing was triggered for prone/stunned players being hit by Ball&Chain")
			.addBugfix("Knocking down team-mates on TTM/KTM did not cause turnovers")
			.addBugfix("Chomp was not available on blitz during Charge!")
			.addBugfix("Target selection was not always removed after a blitz")
			.addBugfix("When using swoop it is now possible to re-roll direction and distance")
			.addBugfix("Bomb knock down team-mates did not cause a turnover")
			.addBugfix("Interception rolls where not modified per tacklezone but only by one for being marked")
			.addBugfix("Prayers were not added to inducement count")
			.addBugfix(
				"When a Steady Footing player blitzed the ball carrier with a both down (both no block) and got saved by Steady Footing the ball did not bounce")
			.addBugfix("Interception SPP were not awarded")
		);

		versions.add(new VersionChangeList("3.1.1")
			.addBugfix("Reloading during kick off sequence was broken")
			.addBugfix("Steady Footing after being pushed on ball did not bounce the ball")
			.addBugfix("Hypnotic Gaze triggers Foul Appearance")
			.addBugfix("Black Ink and Zoat Gaze are only available if non-distracted players are in range")
			.addBugfix("Punt was not available when rushes were exhausted")
			.addBugfix("High Kick with no open players did not skip sequence")
			.addImprovement("Skip Pick Me Up in last turn of half")
			.addBugfix(
				"When Mascot re-roll was available without a team re-roll regular re-roll dialog did not react to mascot button and block dialog did not offer mascot")
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
			.addBugfix(
				"Steady Footing: Attacker blocks defender with both down, defender uses Steady Footing Successfully while attacker fell down, did not cause a turnover")
			.addImprovement("TTM and KTM: reroll choice for Subpar results")
			.addBugfix("Solid Defence: During player selection it was able to move players around")
			.addBugfix("Permanent injuries were not removed by regeneration")
			.addBugfix("Charge: During kickoff blitz, Dodge and Rush skill re-rolls were not available")
			.addBugfix(
				"Hypnotic Gaze + Bloodlust: prone players now can move/feed after failed Bloodlust instead of auto-gazing and ending activation")
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
