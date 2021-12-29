package com.fumbbl.ffb.client.model;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

    private final List<VersionChangeList> versions = new ArrayList<>();

    public static final ChangeList INSTANCE = new ChangeList();

    public ChangeList() {

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
            .addBugfix("Apply stunty on attacker down block results")
            .addBugfix("Not activated team mate placed prone after fumbling caught bomb can still act")
            .addBugfix("Bombardier can now use Pro to re-roll failed bomb throw")
            .addBugfix("Team mate stunned by bomb returned by opposing team was still able to act (foul only)")
            .addBugfix("Confusion and gaze can now be recovered correctly in all cases")
        );

    }

    public List<VersionChangeList> getVersions() {
        return versions;
    }

    public String fingerPrint() {
        return String.valueOf(versions.get(0).hashCode());
    }
}
