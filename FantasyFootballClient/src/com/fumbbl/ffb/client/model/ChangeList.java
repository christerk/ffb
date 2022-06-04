package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("2.10.0")
			.addFeature("Gored By The Bull")
			.addFeature("Maximum Carnage")
			.addBugfix("Wisdom not working on passes")
			.addFeature("Fury of the Blood God")
			.addFeature("Kaboom!")
			.addFeature("Whirling Dervish")
			.addFeature("Add option to re-roll ball and chain movement (game option: allowBallAndChainReRoll)")
			.addFeature("Add menu to select re-roll options for ball and chain movement")
		);

		versions.add(new VersionChangeList("2.9.1")
			.addBugfix("TTM landing on a player is always a turn over")
			.addBugfix("KTM does not generate spp")
			.addBugfix("Do not increment turn counter if td is scored on a blitz kick-off event")
			.addBugfix("Secret weapons not ejected when a touchdown is scored during a blitz kick-off event")
			.addBugfix("Roll for heat after weapons have been ejected")
			.addBugfix("Roll for KOs after weapons have been ejected")
			.addBugfix("No stand up roll for player with MA 2 or less with Jump Up")
			.addBugfix("Ask for special block actions (stab, chainsaw, vomit) on second frenzy block")
			.addBugfix("Specs command did not work")
		);

		versions.add(new VersionChangeList("2.9.0")
			.addFeature("Add various admin commands to alter game state")
			.addFeature("Add new test game commands")
			.addRemoval("Test mode command: pitches")
			.addRemoval("Test mode command: pitch")
			.addRemoval("Test mode command: animations")
			.addRemoval("Test mode command: animation")
			.addBugfix("Remove range ruler from bombardier when cancelling action via right click")
			.addBugfix("Suppress ending action via right click when throwing intercepted bomb")
			.addFeature("Add logging of chat commands on fumbbl side (allows staff to follow conversations in case of a dispute, can not be accessed publicly)")
			.addBugfix("Failed take root on blitz action did prevent blocking adjacent target")
			.addBugfix("Display casualty modifiers on apo roll")
		);

		versions.add(new VersionChangeList("2.8.1")
			.addBugfix("Potential fix for skipped turns")
			.addImprovement("Combined icons for rooted with confused/gazed")
			.addBugfix("Selecting a player on kick-off return sequence forced at least one movement square")
			.addBugfix("B&C did uproot prone or stunned players when hitting them")
			.addBugfix("Riotous Rookies could be healed by team apo despite being Journeymen")
			.addBugfix("FA did not trigger on second frenzy block during blitz action")
			.addImprovement("Report touchback during kick-off")
			.addBugfix("Synchronize mouse click handling during setup")
			.addBugfix("Wrong team got asked to use pass re-roll if player with pass skill did blitz player with dump-off but no pass skill")
			.addImprovement("Added api call to game state connector to access game results")
		);

		versions.add(new VersionChangeList("2.8.0")
			.addFeature("Drunkard")
			.addFeature("Pick-me-up")
			.addFeature("Beer Barrel Bash")
			.addFeature("Raiding Party")
			.addImprovement("Added Java, Neilwat, Nelphine, Stimme, Tussock to credits")
			.addFeature("Pump Up The Crowd")
		);

		versions.add(new VersionChangeList("2.7.2")
			.addBugfix("BB2016: Games did crash when deselecting a player")
		);

		versions.add(new VersionChangeList("2.7.1")
			.addBugfix("BB2016: Games did crash after kick off")
			.addBugfix("\"Excuse me, are you a Zoat?\" was not available")
		);

		versions.add(new VersionChangeList("2.7.0")
			.addFeature("Treacherous")
			.addFeature("Burst of Speed")
			.addFeature("Two for One")
			.addFeature("Lord of Chaos")
			.addFeature("Consummate Professional")
			.addFeature("Strong Passing Game")
			.addFeature("Incorporeal")
			.addFeature("Wisdom of the White Dwarf")
			.addFeature("I'll be back!")
			.addImprovement("Add missing action bindings and descriptions")
			.setDescription("\"Wisdom of the White Dwarf\" and \"Treacherous\" can be activated after selecting an action (but before selecting a target) by clicking the player again. Wisdom will only be used if the player actually acts, if he is just deselected again the skill will still be available for this turn.")
		);

		versions.add(new VersionChangeList("2.6.2")
			.addBugfix("Player falling on ball did not bounce it")
		);

		versions.add(new VersionChangeList("2.6.1")
			.addBugfix("Hitting 'S' short cut on re-roll dialogs where no skill (e.g. Mesmerizing Dance) was available caused player to get stuck")
			.addBugfix("Throw/Kick team-mate actions where not logged properly")
			.addBugfix("B&C hitting team-mates does not get any assists")
			.addBugfix("BB2016: Fix broken replays due to Greased Shoes")
			.addBugfix("BB2016: Fix interaction with Safe Throw and Bombardier (Ball was moved to Bombardier after holding on to the bomb)")
			.addBugfix("Display proper tool tip for players ejected by Officious Ref")
			.addBugfix("Pushing prone Team-mate on ball with B&C caused turn over")
		);

		versions.add(new VersionChangeList("2.6.0")
			.addBugfix("Opening and closing the action menu required an additional click to open it again")
			.addBugfix("Opening and closing the action menu required an additional click for side bar to update (player details) when hovering")
			.addBugfix("Failed download for pitch images caused graphic issues (squares were filled with blue when hovering over them)")
			.addBugfix("Potential fix for setup issues on Mac OS")
			.addFeature("More setting options for right click behavior")
			.addBugfix("Animal Savagery lashing out against ball carrier still started the action (e.g. move one square) before turn ended")
			.addImprovement("Use actual name of raised positions for log message (affects only custom roster)")
			.addBugfix("Kickback on foul did still perform foul")
			.addBugfix("Chainsaw not used on fouls")
			.addBugfix("Juggernaut did not allow to change both down into a pushback vs a rooted opponent")

		);

		versions.add(new VersionChangeList("2.5.1")
			.addBugfix("Animal Savagery players always put prone when failing roll")
		);

		versions.add(new VersionChangeList("2.5.0")
			.setDescription("This release adds a first batch of star abilities. Abilities modifying armour or injury rolls " +
				"have some technical limitations: When interacting with Multiple Block the option to use the skill for each block " +
				"separately and not for both together and the client does not support one player having more than one of those skills " +
				"(it might result in neither being used).")
			.addFeature("Blind Rage")
			.addFeature("Indomitable")
			.addFeature("Mesmerizing Dance")
			.addFeature("The Ballista")
			.addFeature("Old Pro")
			.addFeature("Crushing Blow")
			.addFeature("Ghostly Flames")
			.addFeature("Brutal Block")
			.addFeature("Savage Mauling")
			.addFeature("Ram")
			.addFeature("Slayer")
			.addBugfix("Chainsaw player performing regular block was not able to use Dauntless")
			.addFeature("Sneakiest of the Lot")
			.addFeature("Reliable")
			.addFeature("Frenzied Rush")
			.addImprovement("Reduce game statistics dialog height")
			.addImprovement("Allow marking of players on the pitch during own setup phase (using shift + left/right mouse button)")
			.addFeature("Excuse Me, Are You a Zoat?")
			.addFeature("Shot to Nothing")
			.addBugfix("BB2016: Witch Brew Snake Oil caused client errors (games and replays)")
			.addBugfix("Intercepting and rethrowing a bomb with a Running Pass player gave opponent control over that player")
			.addBugfix("BB2016: Gromskull's Exploding Runes must only apply to player affected by card")
		);

		versions.add(new VersionChangeList("2.4.0")
			.addBugfix("Prevent multi button clicks which caused inconsistent states during quick snap")
			.addBugfix("Properly update position of thrown players to avoid catch/bounce rolls for bouncing balls")
			.addBugfix("Tacklezones are recovered at the start of activation, allowing Pro to be used for Really Stupid etc.")
			.addImprovement("Updated icon cache")
			.addBugfix("Play fall sound for players placed prone from bomb")
			.addBugfix("Play sounds for all multi block actions")
			.addBugfix("Hopefully fixed switching from spectator to replay mode")
			.addFeature("Confirmation dialog to prevent wasting the blitz action")
			.addFeature("Allow cancelling blitz or gaze actions when no roll was made")
			.addFeature("Option to enable right mouse button ending current player action (or deselect) by clicking anywhere on the pitch")
			.addBehaviorChange("Right mouse button is ignored when clicking on the pitch by default")
			.addBugfix("Allow mutations for mercenaries in case of primary access")
			.addImprovement("Better visualization for niggling injuries")
			.addImprovement("Add marker to kick scatter square")
			.addImprovement("Add border to kick off scatter squares")
			.addBugfix("Game statistics dialog does not become larger than client window anymore")
			.addBugfix("TTMing Swoop player is now counted as action during blitz turn")
		);

		versions.add(new VersionChangeList("2.3.2")
			.addBugfix("Internal fix potentially related to missing stat increases")
			.addBugfix("Hopefully prevent skipped turns after quick snap")
			.addBugfix("Internal fixes potentially related to missing/stuck players after quick snap")
			.addBugfix("Various stability fixes")
			.addBugfix("Completions for TTM/KTM with Swoop player where awarded to the thrown player")
		);

		versions.add(new VersionChangeList("2.3.1")
			.addBugfix("Allow Gaze after 2 rushes")
			.addBugfix("Log Gaze action")
			.addBugfix("Failing Foul Appearance for Gaze action used Blitz action")
		);

		versions.add(new VersionChangeList("2.3.0")
			.addImprovement("Added \"What's new?\" dialog")
			.addBugfix("Prevent throwing throw/kick player that was injured too severe by Animal Savagery")
			.addBugfix("Prevent apothecary usage on zapped players")
			.addBugfix("Do not use Chainsaw modifier when player throws regular block")
			.addBugfix("Prevent overflow in 2016 petty cash dialog")
			.addBugfix("Preserve labels in replay mode when playing/moving backwards")
			.addBugfix("Potential fix for missing stat upgrades")
			.addBugfix("Allow Chainsaw players to continue blitz move after performing regular block")
			.addBugfix("Prone/stunned players hit by B&C failing a rush suffer av roll")
			.addBugfix("Rooted players can uproot themselves with Diving Tackle")
			.addBugfix("Do not use Claws or Mighty Blow when attacker is knocked down as well")
			.addBugfix("Roll for Foul Appearance on Frenzy blocks during blitz actions as well")
			.addBugfix("BB2016: Do not roll for Foul Appearance on Frenzy blocks")
			.addBugfix("Tentacles can be used to hold players following up a block")
			.addBugfix("Safe Pass prevents bombs from exploding")
			.addBugfix("Safe Pass working correctly with Hail Mary Pass")
			.addBugfix("Bombardier with Running Pass can move after throwing bomb (quick or short pass)")
			.addBugfix("Pro only works when having a tackle zone (not while prone/stunned/confused/hypnotized)")
			.addBugfix("Amount of swarming players allowed to be placed is limited by swarming players already on the pitch")
			.addBugfix("Animal Savagery players can not use Claws or Mighty Blow when prone")
			.addBugfix("No team re-rolls during wizard phase")
			.addBugfix("Chainsaw must not be applied when hit by a bomb")
			.addImprovement("Display prayer names in skill list of affected players")
			.addBugfix("Rush/Jump interaction works correctly again")
			.addBugfix("Apply Stunty on attacker down block results")
			.addBugfix("Not activated team mate placed prone after fumbling caught bomb can still act")
			.addBugfix("Bombardier can now use Pro to re-roll failed bomb throw")
			.addBugfix("Team mate stunned by bomb returned by opposing team was still able to act (foul only)")
			.addBugfix("Confusion and Gaze can now be recovered correctly in all cases")
			.addBugfix("Ball & Chain can't use Mighty Blow against prone/stunned players")
			.addBehaviorChange("Hypnotic Gaze must now be declared as action when activating. NOTE: You can't use Hypnotic Gaze anymore if you select a Move action!!")
			.addBugfix("Pile Driver could be used on Blitz! kick-off event")
			.addFeature("Added new game option to prevent concessions")
		);

	}

	public List<VersionChangeList> getVersions() {
		return versions;
	}

	public String fingerPrint() {
		return String.valueOf(versions.get(0).hashCode());
	}
}
