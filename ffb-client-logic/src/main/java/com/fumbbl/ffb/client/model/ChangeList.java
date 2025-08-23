package com.fumbbl.ffb.client.model;

import com.fumbbl.ffb.option.GameOptionId;

import java.util.ArrayList;
import java.util.List;

public class ChangeList {

	public static final ChangeList INSTANCE = new ChangeList();
	private final List<VersionChangeList> versions = new ArrayList<>();

	public ChangeList() {

		versions.add(new VersionChangeList("2.37.0")
			.addImprovement("Dialogs: On systems using gtk laf dialogs with dropdowns did not render properly")
			.addFeature("Player Markings: it is now possible to add manual player markings in addition to automatic player markings using different modes")
			.addBugfix("Manual Markings: Changes to manual markings should now be preserved should the other coach leave the game")
			.addFeature("Added command to change amount of rerolls in test games")
			.addBugfix("Preserve shown dialogs during init phase (login dialog, replay choice and various progress bars)")
			.addBugfix("Disconnection: When the away client got disconnected while performing a blitz action it was not possible to block after reconnecting")
			.addBugfix("Fix wording of target select dialogs")
			.addImprovement("User Settings menu has been restructured")
		);

		versions.add(new VersionChangeList("2.36.1")
			.addBugfix("Replay Mode: Mouse hover did not show player details anymore")
			.addBugfix("Spectator Mode: When switching to replay mode during spectating the replay controls were disabled")
			.addBugfix("Replay Mode: When changing client layout replay controls got disabled")
			.addBugfix("Replay Mode: Automarking did not use coach's settings anymore")
			.addBugfix("Replay Mode: Manual markings could not be set")
			.addImprovement("Sketches: Custom cursor can now be turned off in the replay menu")
		);

		versions.add(new VersionChangeList("2.36.0")
			.addBugfix("Savage Blow: When performing an uphill block wthout any other re-roll sources left the dialog did not show any buttons to continue")
			.addBehaviorChange("Players in dugouts are spaced more evenly")
			.addImprovement("Player in dugout are scaled up in wide mode")
			.addFeature("Collaborative replays: Replays can now be watched with multiple participants in an online session")
			.addFeature("Replay sketches: In replays (collaborative or single) coaches can sketch player paths/mark squares which is visible to other participants")
		);

		versions.add(new VersionChangeList("2.35.2")
			.addImprovement("Client Logging: Capture more error messages in log file")
			.addBugfix("Replays: In some cases replays did not load when automarking was not enabled")
			.addBugfix("Dodge: Sporadic errors during rendering of dodge messages")
			.addBugfix("Replays in spec mode: With automarking enabled switching back from replay to spec mode would result in subsequent moves being rendered twice")
			.addBugfix("Baleful Hex: When applied to an adjacent player, that player was still considered marking the active player")
			.addBugfix("Master Chef: When both teams have a master chef re-rolls were not calculated correctly")
			.addBugfix("Old Pro: When the pro roll failed Old Pro was still marked as used")
		);

		versions.add(new VersionChangeList("2.35.1")
			.addBugfix("Replays: Automatic player markings were not applied")
		);

		versions.add(new VersionChangeList("2.35.0")
			.addFeature("Logging: Added client side logging, activated by default")
			.addImprovement("Log sent messages in case of failure")
			.addBugfix("Hit And Run: There were reported cases where turns were ended after using Hit And Run, this update includes chances that might fix that issue")
			.addImprovement("Log call stacks for all end turn commands on client side")
			.addBugfix("Take Root: Ending the player action after failing Take Root for a hand-off did end the turn instead")
			.addBugfix("Replay: Show correct automarking during replay")
			.addBugfix("Replay: Show inducement icons correctly during replay")
			.addBugfix("Automarking: Include injuries suffered during the current game")
			.addBugfix("Blitz & Gaze vs Foul Appearance: Canceling the action after triggering Foul Appearance did not mark the player as used")
			.addBugfix("Furious Outburst: Stabbing the ball carrier successfully did not cause the ball to bounce")
			.addBugfix("Arm Bar: Modifier was not applied for jumps failing with a natural 1")
			.addBugfix("\"Then I Started Blastin'!\": Knocking yourself down after selecting a blitz or gaze target caused the game to be stuck")
			.addBugfix("\"Then I Started Blastin'!\": Action was not available when selecting foul action")
			.addBugfix("Furious Outburst: Could be used when prone")
			.addBugfix("Deactivating a prone player again after selecting jump caused the player to stand up and use up their activation")
			.addBugfix("Apo Dialog: Punctuation issue, reworded text to improve grammar")
		);

		versions.add(new VersionChangeList("2.34.2")
			.addBugfix("Hail Mary Bomb: It was not possible to deselect a player with hail mary pass and bombardier once bomb action was selected")
			.addBugfix("Ball & Chain: When B&C movement can be re-rolled in some cases loading the re-roll setting caused the game to crash")
		);

		versions.add(new VersionChangeList("2.34.1")
			.addBugfix("Pushback arrows did not get updated properly")
			.addBugfix("Chainsaw: Fouls did target the chainsaw player themself")
		);

		versions.add(new VersionChangeList("2.34.0")
			.addImprovement("Separate client state logic from UI")
			.addBugfix("Vicious Vines: Could be used by prone players")
			.addBugfix("2016: Stab was not working anymore")
			.addImprovement("Technical: Add half count to result xml")
			.addBugfix("Dauntless: Natural 6 counted as success")
			.addBugfix("Ball & Chain: Failing rush into block with cas did give opponent spp")
			.addBugfix("Grab: Also cancelled sidestep on secondary pushes")
			.addBugfix("Pro: Reverting change from 2.30.0 - Failed pro on a block die has to prevent other re-rolls on that die")
		);

		versions.add(new VersionChangeList("2.33.1")
			.addBugfix("Animal Savagery: Failed Animal Savagery rolls caused the game to crash")
			.addBugfix("AdminServlet: Logs for games without compresssed logs could not be loaded")
		);

		versions.add(new VersionChangeList("2.33.0")
			.addBugfix("Custom font color was not applied to initial log messages")
			.addFeature("Vicious Vines")
			.addFeature("Furious Outburst")
			.addBugfix("Multi Block and Pile Driver: If a player with both skills used multi block and was then standing next to a knocked down player the game crashed")
			.addBugfix("Multi Block and Bloodlust: It was not possible to change a multi block action to a move action after failing bloodlust")
			.addImprovement("Local Icon Cache: Custom pitches are now cached as well")
			.addImprovement("Updated bundled icon cache")
			.addBugfix("Animosity: Was applied against star players")
			.addImprovement("In case the Intensive Training prayer is wasted on a player that can not gain any primary skills the log message is now clearer")
			.addBehaviorChange("Set InternalFrame.useTaskBar to false, hoping this solves issues with white bars at the client bottom for Linux users")
		);

		versions.add(new VersionChangeList("2.32.0")
			.addBugfix("Hit And Run: Rooted players were able to use Hit And Run after performing a block")
			.addFeature("Wide Layout: More streaming friendly layout with larger horizontal pitch, requested by Christer")
			.addBehaviorChange("\"Pitch Orientation\" submenu is now called \"Client Layout\" and moved from \"Pitch Customization\" to new \"Client UI\" submenu")
			.addBehaviorChange("\"Client Size\" menu entry moved to new \"Client UI\" submenu")
			.addImprovement("Timeouts for image loading increased")
		);

		versions.add(new VersionChangeList("2.31.0")
			.addFeature("The Flashing Blade")
			.addBugfix("Bloodlust: When a player that lost their tacklezone due to a failed Bloodlust roll in a previous turn used gaze or blitz action, failed Bloodlust again and then changed their action to a move action the target of that action lost their tacklezone.")
			.addBugfix("Automarking: Markings for niggling injuries were not applied")
			.addBugfix("Multiple Block: Special states like confusion (e.g. Bone-Head) where transferred from the first target to the second")
			.addBugfix("If players with multiple use once per drive|half|game skills had more than one of these skills used the game crashed when one of these skills was reset")
		);

		versions.add(new VersionChangeList("2.30.3")
			.addBugfix("Riotous Rookies: Plague Ridden position from ruleset was in some cases used for riotous rookies")
		);

		versions.add(new VersionChangeList("2.30.2")
			.addBugfix("Breathe Fire: B&C injuries did not trigger if player was only placed prone")
			.addBugfix("Breathe Fire: Blitz activation did not end automatically")
			.addBugfix("Multiple Block & Pile Driver: When both opponents were available as Pile Driver targets it was not able to skip Pile Driver usage")
			.addBugfix("Pathfinding: Moving through the opposing endzone did reduce the available movement range (only for the shown path, not the actual movement)")
			.addBehaviorChange("The opposing endzone now has the same \"weight\" as regular squares, so when trying to score when suffering from bloodlust make sure not to enter the endzone to early as the auto path might enter it when not next to a thrall")
			.addBugfix("Watch Out!: Special actions like Stab did use up the skill even though they are not a block action but replace it")
			.addBugfix("Spiked Ball: Did not trigger when ball was passed to player without tacklezones")
			.addBugfix("All roster were able to hire raised positions as mercs if the fallback was defined in the ruleset")
		);

		versions.add(new VersionChangeList("2.30.1")
			.addBugfix("Breathe Fire: Ball did not bounce for placed prone or knock down results without av breaks")
		);

		versions.add(new VersionChangeList("2.30.0")
			.addFeature("Breathe Fire")
			.addBugfix("Multiple Block: Projectile Vomit was available")
			.addFeature("Unstoppable Momentum")
			.addImprovement("Re-roll buttons in block dialogs now display the mnemonic explicitly")
			.addFeature("Toxin Connoisseur")
			.addBugfix("Pile Driver: Could be used after special block actions like Projectile Vomit")
			.addBugfix("Tentacles: When player was held in place during blitz action the block was skipped even if the target player was already adjacent")
			.addBugfix("Bloodlust: When using blitz action to stand up and end activation the blitz action was not actually used when failing bloodlust and not changing action")
			.addBugfix("For some skill when waiting on the opponent it was possible to open the action menu on the acting player")
			.addFeature("\"Then I Started Blastin'!\"")
			.addRuleChange("Iron Hard Skin now ignores all kinds of armour modifications")
			.addFeature("Savage Blow")
			.addBugfix("Pro: When pro roll failed for re-rolling a block die that die was still considered having been re-rolled")
			.addFeature("Quick Bite")
		);

		versions.add(new VersionChangeList("2.29.0")
			.addBugfix("Auto Marking: Skill and stat changes of some prayers did not get applied")
			.addRuleChange("Bloodlust: Allow changing blitz and gaze to move actions in case of failed bloodlust so FA rolls are avoided")
			.addBugfix("Juggernaut: Message of cancelling wrestle was always displayed even if opponent did not have wrestle")
			.addBugfix("Raiding Party: Can't move players without tz anymore, since they have to mark an opponent after move")
			.addBugfix("Slayer: Modifier could be used on injury even against opponent with st4 or lower")
			.addImprovement("During kick-off events like Quick Snap the ball is now always drawn over a player on the same coordinate to make it more visible")
			.addImprovement("When balls or bombs land out of bounds they are marked with white cross similar to the stunned marking during the kick off phase and deflector selection")
			.addBugfix("Distance Markings were not aligned properly in portrait and square modes")
			.addImprovement("Increase contrast for away team pitch markings")
			.addFeature("Pitch now supports row markings")
			.addBugfix("What's new: Descriptions of dialog and releases were not scaled")
			.addBugfix("Nerves of Steel: Did work on TTM actions")
			.addBugfix("Nerves of Steel: Did not apply for HMP bombs")
			.addBugfix("If reconnecting during bomb intercept, the bomb was not rendered")
			.addBugfix("Range ruler was not rendered when (re)connecting during an interference attempt")
			.addImprovement("Show ball/bomb in target square on successful throw during interception dialog")
			.addBugfix("Nerves of Steel: Had no wording for bomb action")
		);

		versions.add(new VersionChangeList("2.28.1")
			.addBugfix("Moles under the pitch only lasted a drive instead of a half")
			.addBugfix("Trickster could be used without tackle zone")
			.addRuleChange("Leap & Pogo Stick: When failing rush for jumps, player will always land in target square")
			.addFeature("Technical: Logs are now grouped in folders, limited by number")
			.addBugfix("Penalty Shootout: Dialog did prevent client from being closed in spec mode")
		);

		versions.add(new VersionChangeList("2.28.0")
			.addBugfix("Auto Marking: Add synchronization to client side event handling until the game is initialized completely")
			.addImprovement("Added logs to exception logging code")
			.addImprovement("Flush writer in exception logging code")
			.addImprovement("Make code compatible with open jdk by removing javafx references")
			.addRemoval("Technical: Removed 2 unreachable steps in block sequences")
			.addBugfix("Prevent exceptions when automarking is handled for a game already removed from game cache")
			.addImprovement("Technical: Logs are now separated by game id (old log is still written until new logging proves stable)")
			.addFeature("Spectator names are now also displayed in the spectator tool tip")
			.addImprovement("Cheering Fans: Prayers now have their own animation")
			.addImprovement("Log files are no split by game and zipped")
			.addRuleChange("Guard no longer works for foul assists")
			.addRuleChange("Players now can be restunned again on their turn, e.g. failed Bloodlust")
			.addBugfix("Player without ball failing take root on a hand over action did cause a turnover")
			.addBugfix("Range ruler was sometimes not removed properly")
		);

		versions.add(new VersionChangeList("2.27.0")
			.addFeature("Trickster")
			.addRuleChange("Reduce Master Chef costs are now only available to Halfling teams and not all Halfling Thimble Cup teams")
			.addFeature("My Ball")
			.addFeature("Bounding Leap")
			.addBugfix("Primal Savagery: When lashing out against an opponent during a block action that was not the target of the block, the action got skipped after lashing out")
			.addBugfix("Animal Savagery: When failing AS for TTM of an adjacent player and another adjacent team-mate the select dialog was not displayed but instead the game froze")
			.addBugfix("Raiding Party: Moving a player without tackle zone onto the ball did not result in a bounce")
			.addBugfix("Stat reductions by injuries were not always applied properly")
			.addBugfix("Sure Feet: Did trigger during Blitz! kick-off event and caused the game to crash")
			.addBugfix("Animal Savagery: When lashing out against a player to be thrown that player was still able to make their landing roll")
			.addBugfix("Auto Marking: Add synchronization to client side event handling until the game is initialized completely")
		);

		versions.add(new VersionChangeList("2.26.1")
			.addBugfix("Unchanneled Fury & Bone-Head: Failure caused the game to hang due to re-roll dialog not being displayed")
			.addBugfix("Client allowed dodges to be re-rolled twice")
		);

		versions.add(new VersionChangeList("2.26.0")
			.addBugfix("Special actions like stab or chainsaw were not available during the blitz action of a Blitz! kick-off event, for details see comments here:  https://fumbbl.com/p/bugs?id/4093")
			.addBugfix("When bloodlusting passes and hand offs to empty squares or opposing players were not performed even after having fed")
			.addBugfix("Some pitch images cause errors when trying to rescale them (happens when the client size is set to something other than 100%) which led to the client not rendering any more. These errors are no ignored and images shown unscaled")
			.addImprovement("Force closing of shoot out dialog before closing the client")
			.addBugfix("Internal reordering of requests to hopefully make auto marking work consistently at start up")
			.addFeature("Added options to change colors for field marker and player marker (home and away separately)")
			.addBugfix("Chat command dialog did not scale nicely")
			.addImprovement("Change Shootout dialog to use explicit close button")
			.addBugfix("Surfing your own ball carrier did not result in a turnover")
			.addBugfix("Surfing your team mate with fan interaction did generate spp")
			.addFeature("Catch of the Day")
			.addBugfix("Primal Savagery: Lashing out against the opposing ball carrier did not bounce the ball and suppressed the injury")
			.addBugfix("Gored by the Bull: The option to use the skill was not available on the second (frenzy) block")
			.addBehaviorChange("Gored by the Bull: The skill use has not to be declared at activation start but directly when performing the block similar to chainsaw etc.")
			.addFeature("Swift As The Breeze")
			.addImprovement("Add Yoink! sound to yoink interceptions (is also used for catch of the day), provided by ramchop/LCYing")
			.addImprovement("Include Java and tussock in about dialog credits")
			.addBugfix("Hardening of setup phase against player positions getting out of sync with high network latency")
			.addBugfix("Bloodlust: A ball carrier suffering from bloodlust was allowed to move within the opposing end zone and even leave it")
			.addBehaviorChange("Automove: Squares in the opposing end zone are considered less favorable than empty squares so a player enters it at the last possible square")
			.addImprovement("Player select dialogs now also show markers, ball and prone/stunned state")
			.addBehaviorChange("Player select dialogs now have a light gray background so the default marker color is better to see")
		);

		versions.add(new VersionChangeList("2.25.0")
			.addBugfix("Using Old Pro to save player from kick back av break, did not generate a turnover even if game options are set to default (turnover on all kickbacks)")
			.addBugfix("When Cindy was knocked down by an intercepted \"All You Can Eat\" bomb the ref ejected the interceptor instead of Cindy")
			.addBugfix("Dump-Off was counted as negative passing yards")
			.addBugfix("Failed Foul Appearance roll still allowed use of Hit and Run")
			.addBugfix("Failed pickup for bloodlusting player allowed feeding which did prevent the turnover when no re-roll was used for the pickup")
			.addRuleChange("Players starting their turn stunned and getting stunned again still roll over (client performs roll over at turn start and ignores further stunned results)")
			.addBugfix("Declining to use a re-roll for a failed diving catch caused the game to halt")
			.addBugfix("Game log did not get updated for each diving catch attempt when several players tried to catch the ball")
			.addRuleChange("Catch and Monstrous Mouth can not be used for bombs")
			.addRuleChange("B&C may use Brawler")
			.addFeature("Adding game option \"catchWorksForBombs\" to retain old behavior for catch and monstrous mouth")
			.addRuleChange("Multiple bribes can be used per send off")
			.addFeature("Adding game option \"onlyOneBribePerSendOff\" to retain old behavior for bribes")
			.addBugfix("Piling On was still available for mercenaries and Intensive Training")
			.addBugfix("Block knockdowns where not handled when the blocker followed up and a player from the opposing team failed their tentacle roll and after that succeeded with their shadowing roll")
			.addFeature("Master Assassin")
			.addFeature("Animated dialog for penalty shootout results after overtime")
			.addBugfix("Monstrous Mouth was not selectable for Intensive Training")
			.addBugfix("Interception attempts of bombs thrown by stunties did give +1 bonus even though that only appplies to pass actions")
			.addBugfix("Biting the opposing ball carrier with Tasty Morsel did cause a turnover")
			.addBugfix("When Primal Savagery was used to lash out against an opponent the tackle zones were not removed and still forced a dodge roll when moving away")
			.addBugfix("Lashing out against the opposing ball carrier with Primal Savagery did cause a turnover")
			.addBugfix("When the defence team got their ball carrier knocked down due to Throw A Rock (prayer) and still scored the touchdown by catching the bouncing ball in the endzone, both teams effectively lost a turn")
			.addBugfix("The scoring player was not reported when there were also secret weapons that AtC or bribes were used for")
			.addBugfix("Adjacent prone players declaring a blitz or jump up block on a dump off ball carrier did count as tackle zones for the pass")
			.addBugfix("Feeding from Bloodlust during a blitz kick-off event caused the player to be counted twice")
			.addBugfix("Feeding from Bloodlust during a blitz kick-off event with the last or second to last player allowed rendered the player limit ineffective")
		);

		versions.add(new VersionChangeList("2.24.1")
			.addBugfix("Black Ink was available for prone players")
			.addBugfix("Skipping Black Ink and deselecting player removed confusion state")
			.addBugfix("Tasty Morsel only worked if at least one player of the own team was eligible to be bitten")
			.addImprovement("Reworded log messages for activation counts during Blitz! and Quick Snap kick-off events")
			.addImprovement("Player action menu should now properly reflect if a Blitz or Gaze action can be cancelled")
		);

		versions.add(new VersionChangeList("2.24.0")
			.addImprovement("When failing Bloodlust an no re-roll is used or available player will not move so they can be redirected")
			.addBugfix("Special abilities like Crushing Blow did not always print injuries correctly in log")
			.addBugfix("Players never have animosity vs mercenaries")
			.addBugfix("TTM scattering out of bounds after the first scatter was logged incorrectly as bounce")
			.addBugfix("B&C was able to use Brawler")
			.addBugfix("Sidestepper with Shadowing prevented second Frenzy block by shadowing back into the square the blocker came from")
			.addFeature("Black Ink")
			.addFeature("Add setting to show sweet spot during offence setup/kick off sequence")
			.addImprovement("Auto markings now also get updated for added players (e.g. mercenaries) and prayer effects")
			.addBugfix("Raised thralls could become MVP")
			.addImprovement("Block action now also shows when alternative block actions like Stab or Chainsaw are available")
		);

		versions.add(new VersionChangeList("2.23.0")
			.addFeature("Bloodlust")
			.addFeature("Vampire Lord")
			.addFeature("Tasty Morsel")
			.addFeature("Star of the Show")
			.addFeature("Dwarfen Scourge")
		);

		versions.add(new VersionChangeList("2.22.0")
			.addFeature("Add game option " + GameOptionId.BOMB_USES_MB.getName() + " to allow granting Mighty Blow to bombs")
			.addBugfix("Replays with applied bomb modifiers (armour or injury) did not load")
			.addBugfix("Skipping forward in replay mode did not always jump to the next turn in the dice log")
			.addBugfix("Menu entry for switching between replay and spec mode was not always updated correctly")
			.addRuleChange("Chainsaw kickback now always causes turnover")
			.addFeature("Add game option " + GameOptionId.CHAINSAW_TURNOVER.getName() + " to allow control in what cases chainsaw causes turnovers")
			.addRemoval("Remove game option " + GameOptionId.CHAINSAW_TURNOVER_ON_AV_BREAK.getName())
			.addBugfix("Failed hand offs/passes by rooted players did not cause turnovers")
			.addBugfix("Exclude Infamous Staff from MVP")
			.addImprovement("Add additional log entry when juggernaut prevents use of wrestle")
			.addFeature("Add game option " + GameOptionId.OVERTIME_GOLDEN_GOAL.getName() + " which will end overtime after first td")
			.addFeature("Add game option " + GameOptionId.OVERTIME_KICK_OFF_RESULTS.getName() + " defining the available kick-off results during overtime")
			.addBugfix("Hovering over empty resource slots caused NPEs")
			.addFeature("Add game option " + GameOptionId.INDUCEMENTS_ALLOW_OVERDOG_SPENDING.getName() + " defining if overdog can spend treasury")
			.addImprovement("Display treasury in red on inducements dialog")
			.addImprovement("Raiding Party and Hit and Run do not auto select if only one square is available")
			.addFeature("Add user setting to hide explosion craters and blood spots on the pitch, can be found under \"User Settings\" -> \"Pitch\"")
			.addBugfix("Chainsaw could be used on block and pile driver foul in the same action")
			.addBugfix("Crushing Blow was not offered when Mighty Blow was used to break armour")
			.addBugfix("Chainsaw using Pile Driver still performed foul after kick back")
			.addBugfix("Kick back on Kick 'em, while they're down! resulted in opponent standing")
		);

		versions.add(new VersionChangeList("2.21.0")
			.addBugfix("Reduce rounding impact on scaled up clients when mapping mouse position to squares")
			.addBugfix("Checkboxes for locally stored properties could not be checked")
			.addBugfix("Inducement tooltips were not rendered in the proper locations")
			.addBugfix("BB2016: Buying cards caused the client to lock up")
			.addImprovement("Reworking handling of chat and log messages to cause less blocking, could help with sluggish client behavior during streaming")
			.addRuleChange("Chainsaw causes turnover if armour breaks on kickbacks")
			.addFeature("Add game option " + GameOptionId.CHAINSAW_TURNOVER.getName() + " to allow control in what cases chainsaw causes turnovers")
			.addRuleChange("\"Gored By The Bull\" must new be declared before rolling block dice. To activate click the active player before performing the block.")
			.addRuleChange("Star pairs now only use one star slot")
			.addRuleChange("Bombs no longer modify armour or injury")
			.addRuleChange("Bombardier placed prone by own bomb now causes a turnover")
			.addFeature("Add game option " + GameOptionId.BOMBER_PLACED_PRONE_IGNORES_TURNOVER.getName() + " to allow control if Bombardier can cause turnovers")
			.addRuleChange("Sneaky Git can no longer move after foul")
			.addFeature("Add game option " + GameOptionId.SNEAKY_GIT_CAN_MOVE_AFTER_FOUL.getName() + " to allow control if sneaky git may move after foul")
			.addRuleChange("Raiding Party can now be used on the active player, still only after declaring an action")
			.addBugfix("Weather tool tip for blizzard still showed \"go for it\" instead of \"rushes\"")
		);

		versions.add(new VersionChangeList("2.20.0")
			.addBugfix("Replays were not synced up with controls anymore")
			.addBugfix("Remove superfluous line spacings in game log")
			.addBugfix("Using right click to end player action during dump off, froze game")
			.addBugfix("Replays with rostered stars did not load")
			.addBugfix("Bombardier that got stunned by his bomb thrown back at him was able to act again")
			.addBugfix("Casualty against zapped player was not logged properly")
			.addFeature("Enable Resizing using ctrl +, ctrl 0 and ctrl -")
		);

		versions.add(new VersionChangeList("2.19.1")
			.addBugfix("Invalid frame background color caused client to crash")
		);

		versions.add(new VersionChangeList("2.19.0")
			.addFeature("Add option to store user settings locally only")
			.addFeature("Store remotely stored options also locally to init the client before connecting")
			.addFeature("Resizing")
		);

		versions.add(new VersionChangeList("2.18.0")
			.addFeature("Added options to change background colors for frame components, log and chat windows via User Settings -> Background Styles (should make it easier to filter out background for streaming)")
			.addBugfix("Sidebars and text colors were not adjusted when swapping team colors")
			.addFeature("Added options to change text colors in frame, log and chat components via User Settings -> Font Colors")
			.addBugfix("Force player states to active when coordinate change was not received by the server at the end of setup phases")
		);

		versions.add(new VersionChangeList("2.17.1")
			.addRemoval("Force player states to active")
		);

		versions.add(new VersionChangeList("2.17.0")
			.addBugfix("Players in box were not always redrawn when an automated marker was set")
			.addBugfix("Potential race condition fixed that might have caused loss of marker updates")
			.addBugfix(
				"Force player states to active when ending setup or kick off phases, potential fix for unmovable players after kick off")
			.addBugfix("Free inducement cash was not subtracted from used gold")
			.addBugfix("Moved chef rolls to occur after kick-off deviation")
			.addImprovement("Added server side exception logging")
			.addBugfix(
				"Potential race condition fixed that might have caused teams to show up on the opposite side of the client")
			.addBugfix("Rename \"Go For It\" to \"Rush\"")
			.addBugfix("Kick 'em while they're down! could be used against players not being adjacent")
			.addFeature("Added option to swap team colors (User Settings -> Icons -> Swap team colors")
			.addBugfix("Interceptions did not generate spp")
		);

		versions.add(new VersionChangeList("2.17.0")
			.addBugfix("Players in box were not always redrawn when an automated marker was set")
			.addBugfix("Potential race condition fixed that might have caused loss of marker updates")
			.addBugfix("Force player states to active when ending setup or kick off phases, potential fix for unmovable players after kick off")
			.addBugfix("Free inducement cash was not subtracted from used gold")
			.addBugfix("Moved chef rolls to occur after kick-off deviation")
			.addImprovement("Added server side exception logging")
			.addBugfix("Potential race condition fixed that might have caused teams to show up on the opposite side of the client")
			.addBugfix("Rename \"Go For It\" to \"Rush\"")
			.addBugfix("Kick 'em while they're down! could be used against players not being adjacent")
			.addFeature("Added option to swap team colors (User Settings -> Icons -> Swap team colors")
			.addBugfix("Interceptions did not generate spp")
		);

		versions.add(new VersionChangeList("2.16.2")
			.addBugfix("Failing TTM due to a confusion roll caused the thrown player to vanish")
			.addBugfix("Prone player with HG failing FA was left standing")
		);

		versions.add(new VersionChangeList("2.16.1")
			.addBugfix(
				"Automatic Markings were not applied during start of spectator mode (required toggling setting)")
			.addBugfix("Markings were reset once a player rejoined")
			.addBugfix(
				"Players without tacklezones could use dodge when asked for (i.e. near side lines)")
		);

		versions.add(new VersionChangeList("2.16.0")
			.addFeature("Thinking Man's Troll")
			.addFeature("Halfling Luck")
			.addFeature("Watch Out!")
			.addFeature("Yoink!")
			.addFeature("A Sneaky Pair")
			.addFeature("All You Can Eat")
			.addBugfix("Allow Fumblerooskie during all move related actions")
			.addFeature("Putrid Regurgitation")
			.addFeature("Kick 'em while they're down!")
			.addBugfix("Dead players could defect")
			.addBugfix("Raiding Party was able to move rooted players")
			.addBugfix(
				"When a thrown player hit and removed the ttm player a ghost image of the thrown player was shown in it's place")
			.addBugfix("Players hit by ttm were not shown on the pitch during the animation sequences")
			.addBugfix("Weather Mage effect did not revert at the end of a drive")
			.addFeature(
				"Automated Markings: Can be activated in user settings, more details can be found in the wiki https://fumbbl.com/help:AutomaticMarking or help menu")
			.addFeature(
				"Hypnotic Gaze can now be used with a custom skill value to be used instead of agility (requested by Secret League)")
			.addImprovement("Use new pitch icons")
		);

		versions.add(new VersionChangeList("2.15.0")
			.addBugfix(
				"Allow re-rolling natural one on Argue the Call with Biased Ref even though coach would not have been banned")
			.addBugfix("Argue the Call now is coming before Bribes")
			.addImprovement(
				"Argue the Call for weapons is now asked multiple times so a coach can first argue for selected players")
			.addBugfix(
				"Repeated Bribes dialog for weapons now also shows up if all bribes roll were successful so now a coach can first bribe for selected players")
			.addBugfix(
				"Failing the first rush on a jump requiring two rushes knocks the player down in the start square")
			.addBugfix("Spec counter in square mode was not rendered properly")
			.addBugfix("Two For One bonus stays even if partner returns to play")
			.addBugfix(
				"In some cases duplicate commands could trigger double activation (network issues)")
			.addBugfix(
				"Take \"replacesPosition\" attribute into account for mercenaries (prevent buying more players than allowed e.g. for Jaguar Warriors")
			.addImprovement(
				"Replay mode now loads user settings if authentication data is available (changes will not be saved)")
			.addFeature(
				"New setting to display green check mark for used players. User Settings -> Mark used players")
			.addBugfix("Animal Savagery player lashing out against the ball carrier got confused")
			.addImprovement("Added explanation to skill use dialog for Grab")
		);

		versions.add(new VersionChangeList("2.14.0")
			.addFeature("Square mode (like portrait but logs and score board on the side")
			.addBugfix("Inducement phase now follows Designer's Commentary from Nov 22")
			.addBugfix("Animal Savagery caused game crashes used with ttm/ktm/pass/hand off")
			.addBugfix("Default value for ttm turnover option on hitting team mates is now false again")
			.addBugfix(
				"Multiple Block vs Foul Appearance opponents could cause game crashes when failing a Foul Appearance roll")
			.addBugfix("Pogo stick now ignores Diving Tackle and Prehensile Tail")
			.addBugfix(
				"Inducement phase calculated spent treasury incorrectly for underdog, draining money while petty cash was used")
			.addFeature(
				"Add game option inducementsAllowSpendingTreasuryOnEqualCTV, when active in case of equal CTV both teams can buy inducements from treasury simultaneously")
			.addFeature(
				"Add game option inducementsAlwaysUseTreasury, when active teams always only use treasury for inducements instead of petty cash")
		);

		versions.add(new VersionChangeList("2.13.1")
			.addBugfix("RaidingParty used with e.g. Block caused the game to break")
			.addBugfix("Block dice indicators were not updated after Raiding Party or Hit and Run")
			.addImprovement("Improvements for local development")
			.addBugfix("Hit and run message referred to a team-mate")
			.addImprovement("Closed session are no longer eligible when checking for game start")
			.addBugfix("Team logos did not display properly in portrait mode")
		);

		versions.add(new VersionChangeList("2.13.0")
			.addFeature("Portrait mode")
			.addImprovement("Adjustments for dice stats")
			.addImprovement("Cleanups")
			.addBugfix("Prevent duplicate adding of temporary effects")
			.addBugfix("Ball did not bounce after hit from Beer Barrel Bash!")
			.addBugfix("Pitch Invasion caused crash if affected team could not field players")
			.addBugfix("Bugman did not show up in inducement menu and result xml")
			.addBugfix("Block roll log entry did show player id instead of name")
			.addBugfix("Game crashed when kicking team could not field any players")
			.addBugfix("Message for failed AtC did not display proper minimum roll")
			.addBugfix("Empty player names caused player to be unusable")
			.addBugfix("Iron Man has to last for the entire game")
			.addBugfix("Thick Skull for stunties was not displayed in game log")
			.addBugfix("Some replays failed to load")
			.addBugfix("Treacherous against chainsaw player did not add chainsaw modifier")
			.addBugfix(
				"If the away client disconnected prayers were not displayed anymore for that client after rejoining")
			.addBugfix("When moving on the ball with Raiding Party no pickup was made")
			.addBugfix(
				"Lashing out against the player to be thrown or receive the ball did not work properly")
			.addBugfix("Pickup via Raiding Party used active player for pickup")
			.addBugfix("Fumblerooskie did not work for square 1 of a movement")
			.addBugfix(
				"Using fumblerooskie before a blitz block and not following up resulted in a pick up roll")
			.addBugfix("Cancelling jump up blitz left player standing")
			.addBugfix(
				"Players were marked as activated during setup and could not be moved during kick-off")
			.addBugfix("Consummate Professional was not marked as used when re-rolling a catch")
			.addBugfix(
				"Bomb rethrows could be re-rolled by any player using pro not just the original thrower")
			.addFeature("Vomit sound")
			.addFeature("Trapdoor sound")
			.addFeature("Pump Up The Crowd sound")
			.addFeature("Primal Savagery")
			.addFeature("Hit and Run")
		);

		versions.add(new VersionChangeList("2.12.0")
			.addFeature("Temp Agency Cheerleaders")
			.addFeature("Part-time Assistant Coaches")
			.addFeature("Biased Referee")
			.addFeature("Weather Mage")
			.addImprovement("Rename Igors to Mortuary Assistants in dialogs and game options")
			.addImprovement("Apo dialogs now state which apo will be used")
			.addFeature("Plague Doctor")
			.addFeature("Josef Bugman")
			.addFeature("Look Into My Eyes")
			.addBugfix("Multi Block from player with Pile Driver caused games to crash")
			.addBugfix("Failed interception caught by moving team did cause a turn over")
			.addBugfix("Mercenaries could not use Regeneration")
			.addBugfix("Could not leap during gaze action")
			.addBugfix("Stat reducing injuries where capped at 2")
			.addFeature("Baleful Hex")
			.addBugfix("Two For One triggered repeatedly in some situations")
			.addBugfix("Old replays did not load")
		);

		versions.add(new VersionChangeList("2.11.0")
			.addBugfix("Add synchronization on client side (suspect for setup bug)")
			.addBugfix("Leader state was not set properly at end of drives")
			.addBugfix(
				"Further attempt for setup bug mitigation, setting proper player state when syncing player positions")
			.addBugfix("Fixed some admin commands")
			.addFeature("Add standup command for admins and test mode")
			.addFeature("Add turnMode command for admins and test mode")
			.addImprovement(
				"After selecting a player for high kick clicking the player again puts them back to the original square")
			.addBugfix(
				"Double clicking the receiver when performing a quick pass with running pass caused two passes to be thrown")
		);

		versions.add(new VersionChangeList("2.10.1")
			.addBugfix("Some games were unable to restart")
			.addBugfix("FA fail with Frenzy caused games to break")
		);

		versions.add(new VersionChangeList("2.10.0")
			.addFeature("Gored By The Bull")
			.addFeature("Maximum Carnage")
			.addBugfix("Wisdom not working on passes")
			.addFeature("Fury of the Blood God")
			.addFeature("Kaboom!")
			.addFeature("Whirling Dervish")
			.addFeature(
				"New game option to re-roll ball and chain movement (game option boolean: allowBallAndChainReRoll)")
			.addFeature("Add menu to select re-roll options for ball and chain movement")
			.addFeature("Blast It!")
			.addImprovement(
				"If only one Diving Catcher is selected in total, no second dialog is shown")
			.addBugfix(
				"When several Diving Catchers tried to catch the ball and the first failed while using a re-roll there was no roll for the second player")
			.addBugfix(
				"No choice to use dodge skill when being pushed over the LoS during first turn after kick-off when the kick resulted in a touch back")
			.addBugfix(
				"To mitigate the setup bug where client and server state weren't in sync the client now sends all player positions at the end of setup, swarming and solid defence phases")
			.addBehaviorChange(
				"Due to the fix for the setup bug it can happen that opponent players are suddenly moved when the opponent ends his setup")
			.addBugfix("Turn counter in player results was also incremented during kick-off")
			.addFeature(
				"New game option to prevent turn over when hitting opponent with ttm (game option boolean: endTurnWhenHittingAnyPlayerWithTtm)")
			.addFeature("New game option to allow fixed swoop distance: swoopDistance")
			.addFeature(
				"New game option to allow ball and chain use special block actions: allowSpecialBlocksWithBallAndChain")
			.addBehaviorChange("Removed card panel from inducement dialog")
			.addBugfix("Ball & Chain is placed prone after using Pile Driver")
			.addBugfix("Prevent Pile Driver from Ball & Chain against prone/stunned players")
		);

		versions.add(new VersionChangeList("2.9.1")
			.addBugfix("TTM landing on a player is always a turn over")
			.addBugfix("KTM does not generate spp")
			.addBugfix("Do not increment turn counter if td is scored on a blitz kick-off event")
			.addBugfix(
				"Secret weapons not ejected when a touchdown is scored during a blitz kick-off event")
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
			.addFeature(
				"Add logging of chat commands on fumbbl side (allows staff to follow conversations in case of a dispute, can not be accessed publicly)")
			.addBugfix("Failed take root on blitz action did prevent blocking adjacent target")
			.addBugfix("Display casualty modifiers on apo roll")
		);

		versions.add(new VersionChangeList("2.8.1")
			.addBugfix("Potential fix for skipped turns")
			.addImprovement("Combined icons for rooted with confused/gazed")
			.addBugfix(
				"Selecting a player on kick-off return sequence forced at least one movement square")
			.addBugfix("B&C did uproot prone or stunned players when hitting them")
			.addBugfix("Riotous Rookies could be healed by team apo despite being Journeymen")
			.addBugfix("FA did not trigger on second frenzy block during blitz action")
			.addImprovement("Report touchback during kick-off")
			.addBugfix("Synchronize mouse click handling during setup")
			.addBugfix(
				"Wrong team got asked to use pass re-roll if player with pass skill did blitz player with dump-off but no pass skill")
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
			.setDescription(
				"\"Wisdom of the White Dwarf\" and \"Treacherous\" can be activated after selecting an action (but before selecting a target) by clicking the player again. Wisdom will only be used if the player actually acts, if he is just deselected again the skill will still be available for this turn.")
		);

		versions.add(new VersionChangeList("2.6.2")
			.addBugfix("Player falling on ball did not bounce it")
		);

		versions.add(new VersionChangeList("2.6.1")
			.addBugfix(
				"Hitting 'S' short cut on re-roll dialogs where no skill (e.g. Mesmerizing Dance) was available caused player to get stuck")
			.addBugfix("Throw/Kick team-mate actions where not logged properly")
			.addBugfix("B&C hitting team-mates does not get any assists")
			.addBugfix("BB2016: Fix broken replays due to Greased Shoes")
			.addBugfix(
				"BB2016: Fix interaction with Safe Throw and Bombardier (Ball was moved to Bombardier after holding on to the bomb)")
			.addBugfix("Display proper tool tip for players ejected by Officious Ref")
			.addBugfix("Pushing prone Team-mate on ball with B&C caused turn over")
		);

		versions.add(new VersionChangeList("2.6.0")
			.addBugfix(
				"Opening and closing the action menu required an additional click to open it again")
			.addBugfix(
				"Opening and closing the action menu required an additional click for side bar to update (player details) when hovering")
			.addBugfix(
				"Failed download for pitch images caused graphic issues (squares were filled with blue when hovering over them)")
			.addBugfix("Potential fix for setup issues on Mac OS")
			.addFeature("More setting options for right click behavior")
			.addBugfix(
				"Animal Savagery lashing out against ball carrier still started the action (e.g. move one square) before turn ended")
			.addImprovement(
				"Use actual name of raised positions for log message (affects only custom roster)")
			.addBugfix("Kickback on foul did still perform foul")
			.addBugfix("Chainsaw not used on fouls")
			.addBugfix(
				"Juggernaut did not allow to change both down into a pushback vs a rooted opponent")

		);

		versions.add(new VersionChangeList("2.5.1")
			.addBugfix("Animal Savagery players always put prone when failing roll")
		);

		versions.add(new VersionChangeList("2.5.0")
			.setDescription(
				"This release adds a first batch of star abilities. Abilities modifying armour or injury rolls "
					+
					"have some technical limitations: When interacting with Multiple Block the option to use the skill for each block "
					+
					"separately and not for both together and the client does not support one player having more than one of those skills "
					+
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
			.addImprovement(
				"Allow marking of players on the pitch during own setup phase (using shift + left/right mouse button)")
			.addFeature("Excuse Me, Are You a Zoat?")
			.addFeature("Shot to Nothing")
			.addBugfix("BB2016: Witch Brew Snake Oil caused client errors (games and replays)")
			.addBugfix(
				"Intercepting and rethrowing a bomb with a Running Pass player gave opponent control over that player")
			.addBugfix("BB2016: Gromskull's Exploding Runes must only apply to player affected by card")
		);

		versions.add(new VersionChangeList("2.4.0")
			.addBugfix("Prevent multi button clicks which caused inconsistent states during quick snap")
			.addBugfix(
				"Properly update position of thrown players to avoid catch/bounce rolls for bouncing balls")
			.addBugfix(
				"Tacklezones are recovered at the start of activation, allowing Pro to be used for Really Stupid etc.")
			.addImprovement("Updated icon cache")
			.addBugfix("Play fall sound for players placed prone from bomb")
			.addBugfix("Play sounds for all multi block actions")
			.addBugfix("Hopefully fixed switching from spectator to replay mode")
			.addFeature("Confirmation dialog to prevent wasting the blitz action")
			.addFeature("Allow cancelling blitz or gaze actions when no roll was made")
			.addFeature(
				"Option to enable right mouse button ending current player action (or deselect) by clicking anywhere on the pitch")
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
			.addBugfix(
				"Prevent throwing throw/kick player that was injured too severe by Animal Savagery")
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
			.addBugfix(
				"Bombardier with Running Pass can move after throwing bomb (quick or short pass)")
			.addBugfix(
				"Pro only works when having a tackle zone (not while prone/stunned/confused/hypnotized)")
			.addBugfix(
				"Amount of swarming players allowed to be placed is limited by swarming players already on the pitch")
			.addBugfix("Animal Savagery players can not use Claws or Mighty Blow when prone")
			.addBugfix("No team re-rolls during wizard phase")
			.addBugfix("Chainsaw must not be applied when hit by a bomb")
			.addImprovement("Display prayer names in skill list of affected players")
			.addBugfix("Rush/Jump interaction works correctly again")
			.addBugfix("Apply Stunty on attacker down block results")
			.addBugfix("Not activated team mate placed prone after fumbling caught bomb can still act")
			.addBugfix("Bombardier can now use Pro to re-roll failed bomb throw")
			.addBugfix(
				"Team mate stunned by bomb returned by opposing team was still able to act (foul only)")
			.addBugfix("Confusion and Gaze can now be recovered correctly in all cases")
			.addBugfix("Ball & Chain can't use Mighty Blow against prone/stunned players")
			.addBehaviorChange(
				"Hypnotic Gaze must now be declared as action when activating. NOTE: You can't use Hypnotic Gaze anymore if you select a Move action!!")
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
