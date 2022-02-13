package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("2.4.0")
			.addBugfix("Prevent multi button clicks which caused inconsistent states during quick snap")
			.addBugfix("Properly update position of thrown players to avoid catch/bounce rolls for bouncing balls")
			.addBugfix("Tacklezone are recovered at the start of activation, allowing Pro to be used for Really Stupid etc.")
			.addImprovement("Updated icon cache")
			.addBugfix("Play fall sound for players placed prone from bomb")
			.addBugfix("Play sounds for all multi block actions")
			.addBugfix("Hopefully fixed switching from spectator to replay mode")
			.addFeature("Confirmation dialog to prevent wasting the blitz action")
			.addFeature("Allow cancelling blitz or gaze actions when no roll was made")
			.addFeature("Option to enable right mouse button ending current player action by clicking anywhere on the pitch")
			.addBehaviorChange("Right mouse button is ignored when clicking on the pitch by default")
			.addBugfix("Allow mutations for mercenaries in case of primary access")
			.addImprovement("Better visualization for niggling injuries")
			.addImprovement("Add marker to kick scatter square")
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
