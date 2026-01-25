package com.fumbbl.ffb.model.property;

import com.fumbbl.ffb.PassingDistance;
import com.fumbbl.ffb.modifiers.PassContext;

import java.util.HashSet;
import java.util.Set;

public class NamedProperties {
	public static final ISkillProperty addBonusForAccuratePass = new NamedProperty("Add Bonus For Accurate Pass");
	public static final ISkillProperty addStrengthOnBlitz = new NamedProperty("Add Strength on Blitz");
	public static final ISkillProperty affectsEitherArmourOrInjuryOnBlock = new NamedProperty("Affects Either Armour Or Injury On Block");
	public static final ISkillProperty affectsEitherArmourOrInjuryOnDodge = new NamedProperty("Affects Either Armour Or Injury On Dodge");
	public static final ISkillProperty affectsEitherArmourOrInjuryOnFoul = new NamedProperty("Affects Either Armour Or Injury On Foul");
	public static final ISkillProperty affectsEitherArmourOrInjuryOnJump = new NamedProperty("Affects Either Armour Or Injury On Jump");
	public static final ISkillProperty affectsEitherArmourOrInjuryOnTtm = new NamedProperty("Affects Either Armour Or Injury On TTM");
	public static final ISkillProperty allowsAdditionalFoul = new NamedProperty("Allows Additional Foul");
	public static final ISkillProperty allowsRaisingLineman = new NamedProperty("Allows Raising Lineman");
	public static final ISkillProperty allowStandUpAssists = new NamedProperty("Allow Stand Up Assists");
	public static final ISkillProperty appliesConfusion = new NamedProperty("Applies Confusion");
	public static final ISkillProperty appliesPoisonOnBadlyHurt = new NamedProperty("Applies Poison on Badly Hurt");
	public static final ISkillProperty assistsBlocksInTacklezones = new NamedProperty("Assists Blocks in Tacklezones");
	public static final ISkillProperty assistsFoulsInTacklezones = new NamedProperty("Assists Fouls in Tacklezones");
	public static final ISkillProperty becomesImmovable = new NamedProperty("Becomes Immovable");
	public static final ISkillProperty blocksDuringMove = new NamedProperty("Blocks During Move");
	public static final ISkillProperty blocksLikeChainsaw = new NamedProperty("Blocks Like Chainsaw");
	public static final ISkillProperty canAddBlockDie = new NamedProperty("Can Add Block Die");
	public static final ISkillProperty canAddStrengthToDodge = new NamedProperty("Can Add Strength To Dodge");
	public static final ISkillProperty canAddStrengthToPass = new NamedProperty("Can Add Strength To Pass");
	public static final ISkillProperty canAlwaysAssistFouls = new NamedProperty("Can Always Assist Fouls");
	public static final ISkillProperty canAttackOpponentForBallAfterCatch = new NamedProperty("Can Attack Opponent For Ball After Catch");
	public static final ISkillProperty canAttemptCatchInAdjacentSquares = new NamedProperty(
		"Can Attempt Catch In Adjacent Squares");
	public static final ISkillProperty canAttemptToTackleDodgingPlayer = new NamedProperty(
		"Can Attempt To Tackle Dodging Player");
	public static final ISkillProperty canAttemptToTackleJumpingPlayer = new NamedProperty(
		"Can Attempt To Tackle Jumping Player");
	public static final ISkillProperty canAvoidDodging = new NamedProperty("Can Avoid Dodging");
	public static final ISkillProperty canAvoidFallingDown = new NamedProperty("Can Avoid Falling Down");
	public static final ISkillProperty canBeGainedByGettingEven = new NamedProperty("Can Be Gained By Getting Even");
	public static final ISkillProperty canBeKicked = new NamedProperty("Can Be Kicked");
	public static final ISkillProperty canBeThrown = new NamedProperty("Can Be Thrown");
	public static final ISkillProperty canBeThrownIfStrengthIs3orLess = new NamedProperty("Can Be Thrown If Strength is 3 Or Less");
	public static final ISkillProperty canBiteOpponents = new NamedProperty("Can Bite Opponents");
	public static final ISkillProperty canBlastRemotePlayer = new NamedProperty("Can Blast Remote Player");
	public static final ISkillProperty canBlockMoreThanOnce = new NamedProperty("Can Block More Than Once");
	public static final ISkillProperty canBlockTwoAtOnce = new NamedProperty("Can Block Two At Once");
	public static final ISkillProperty canBlockOverDistance = new NamedProperty("Can Block Over Distance");
	public static final ISkillProperty canBlockSameTeamPlayer = new NamedProperty("Can Block Same Team Player");
	public static final ISkillProperty canCancelInterceptions = new NamedProperty("Can Force Interception Reroll");
	public static final ISkillProperty canChooseToIgnoreDodgeModifierAfterRoll = new NamedProperty("Can Choose To Ignore Dodge Modifier After Roll");
	public static final ISkillProperty canChooseToIgnoreJumpModifierAfterRoll = new NamedProperty("Can Choose To Ignore Jump Modifier After Roll");
	public static final ISkillProperty canChooseToIgnoreRushModifierAfterRoll = new NamedProperty("Can Choose To Ignore Rush Modifier After Roll");
	public static final ISkillProperty canChooseOwnPushedBackSquare = new NamedProperty(
		"Can Choose Own Pushed Back Square");
	public static final ISkillProperty canDoubleStrengthAfterDauntless = new NamedProperty("Can Double Strength After Dauntless");
	public static final ISkillProperty canDropBall = new NamedProperty("Can Drop Ball");
	public static final ISkillProperty canFollowPlayerLeavingTacklezones = new NamedProperty("Can Follow Player Leaving Tacklezones");
	public static final ISkillProperty canForceBombExplosion = new NamedProperty("Can Force Bomb Explosion");
	public static final PassingProperty canForceInterceptionRerollOfLongPasses = new PassingProperty("Can Force Interception Reroll of Long Passes") {
		private final Set<PassingDistance> longDistances = new HashSet<PassingDistance>() {{
			add(PassingDistance.LONG_PASS);
			add(PassingDistance.LONG_BOMB);
		}};

		@Override
		public boolean appliesToContext(PassContext context) {
			return longDistances.contains(context.getDistance());
		}
	};
	public static final ISkillProperty canFoulAfterBlock = new NamedProperty("Can Foul After Block");
	public static final ISkillProperty canGainClawsForBlitz = new NamedProperty("Can Gain Claws For Blitz");
	public static final ISkillProperty canGainFrenzyForBlitz = new NamedProperty("Can Gain Frenzy For Blitz");
	public static final ISkillProperty canGainGaze = new NamedProperty("Can Gain Gaze");
	public static final ISkillProperty canGainHailMary = new NamedProperty("Can Gain Hail Mary");
	public static final ISkillProperty canGazeAutomatically = new NamedProperty("Can Gaze Automatically");
	public static final ISkillProperty canGazeAutomaticallyThreeSquaresAway = new NamedProperty("Can Gaze Automatically Three Squares Away");
	public static final ISkillProperty canGazeDuringMove = new NamedProperty("Can Gaze During Move");
	public static final ISkillProperty canGetBallOnGround = new NamedProperty("Can Get Ball On Ground");
	public static final ISkillProperty canGrantReRollAfterTouchdown = new NamedProperty("Can Grant Re-Roll After Touchdown");
	public static final ISkillProperty canGrantSkillsToTeamMates = new NamedProperty("Can Grant Skills to Team-Mates");
	public static final ISkillProperty canHoldPlayersLeavingTacklezones = new NamedProperty("Can Hold Players Leaving Tacklezones");
	public static final ISkillProperty canInterceptEasily = new NamedProperty("Can Intercept Easily");
	public static final ISkillProperty canJoinTeamIfLessThanEleven = new NamedProperty("Can Join Team If Less Than Eleven");
	public static final ISkillProperty canKickTeamMates = new NamedProperty("Can Kick Team Mates");
	public static final ISkillProperty canMoveBeforeBeingBlocked = new NamedProperty("Can Move Before Being Blocked");
	public static final ISkillProperty canThrowKeg = new NamedProperty("Can Throw Keg");
	public static final ISkillProperty canLashOutAgainstOpponents = new NamedProperty("Can Lash Out Against Opponents");
	public static final ISkillProperty canLeap = new NamedProperty("Can Leap");
	public static final ISkillProperty canMakeAnExtraGfi = new NamedProperty("Can Make Extra GFI");
	public static final ISkillProperty canMakeAnExtraGfiOnce = new NamedProperty("Can Make Extra GFI Once");
	public static final ISkillProperty canMakeOpponentMissTurn = new NamedProperty("Can Make Opponent Miss Turn");
	public static final ISkillProperty canIgnoreJumpModifiers = new NamedProperty("Can Ignore Jump Modifiers");

	public static final ISkillProperty canMoveAfterBlock = new NamedProperty("Can Move After Block");
	public static final ISkillProperty canMoveAfterFoul = new NamedProperty("Can Move After Foul");
	public static final ISkillProperty canMoveAfterQuickPass = new NamedProperty("Can Move After Quick Pass");
	public static final ISkillProperty canMoveAfterHandOff = new NamedProperty("Can Move After Hand Off");
	public static final ISkillProperty canMoveDuringKickOffScatter = new NamedProperty(
		"Can Move During Kick Off Scatter");
	public static final ISkillProperty canMoveOpenTeamMate = new NamedProperty("Can Move Open Team-mate");
	public static final ISkillProperty canMoveWhenOpponentPasses = new NamedProperty("Can Move When Opponent Passes");
	public static final ISkillProperty canPassToAnySquare = new NamedProperty("Can Pass To Any Square");
	public static final ISkillProperty canPerformArmourRollInsteadOfBlock = new NamedProperty(
		"Can Perform Armour Roll Instead Of Block");
	public static final ISkillProperty canPerformArmourRollInsteadOfBlockThatMightFail = new NamedProperty(
		"Can Perform Armour Roll Instead Of Block That Might Fail");
	public static final ISkillProperty canPerformArmourRollInsteadOfBlockThatMightFailWithTurnover = new NamedProperty(
		"Can Perform Armour Roll Instead Of Block That Might Fail With Turnover");
	public static final ISkillProperty canPerformSecondChainsawAttack = new NamedProperty("Can Perform Second Chainsaw Attack");
	public static final ISkillProperty canPerformTwoBlocksAfterFailedFury = new NamedProperty("Can Perform Two Blocks After Failed Fury");
	public static final ISkillProperty canPlaceBallWhenKnockedDownOrPlacedProne = new NamedProperty("Can Place Ball When Knocked Down Or Placed Prone");
	public static final ISkillProperty canPushBackToAnySquare = new NamedProperty("Can Push Back To Any Square");
	public static final ISkillProperty canPileOnOpponent = new NamedProperty("Can Pile On Opponent");
	public static final ISkillProperty canReduceKickDistance = new NamedProperty("Can Reduce Kick Distance");
	public static final ISkillProperty canRefuseToBePushed = new NamedProperty("Can Refuse To Be Pushed");
	public static final ISkillProperty canRemoveOpponentAssists = new NamedProperty("Can Remove Opponent Assists");
	public static final ISkillProperty canReRollAnyNumberOfBlockDice = new NamedProperty("Can Re-Roll Any Number Of Block Dice");
	public static final ISkillProperty canRerollSingleBothDown = new NamedProperty("Can Reroll Single Both Down");
	public static final ISkillProperty canRerollDodge = new NamedProperty("Can Reroll Dodge");
	public static final ISkillProperty canReRollHmpScatter = new NamedProperty("Can ReRoll Hmp Scatter");
	public static final ISkillProperty canReRollOnesOnKORecovery = new NamedProperty("Can Re-Roll Ones On KO Recovery");
	public static final ISkillProperty canRerollOncePerTurn = new NamedProperty("Can Reroll Once Per Turn");
	public static final ISkillProperty canRerollSingleDieOncePerPeriod = new NamedProperty("Can Reroll Single Die Once Per Period");
	public static final ISkillProperty canRerollSingleBlockDieDuringBlitz = new NamedProperty("Can Reroll Single Block Die During Blitz");
	public static final ISkillProperty canRerollSingleSkull = new NamedProperty("Can Reroll Single Skull");
	public static final ISkillProperty canRollToMatchOpponentsStrength = new NamedProperty(
		"Can Roll To Match Opponents Strength");
	public static final ISkillProperty canRollToSaveFromInjury = new NamedProperty("Can Roll To Save From Injury");
	public static final ISkillProperty canSabotageBlockerOnKnockdown = new NamedProperty("Can Sabotage Blocker On Knockdown");
	public static final ISkillProperty canSaveReRolls = new NamedProperty("Can Save Re-Rolls");
	public static final ISkillProperty canSkipTtmScatterOnSuperbThrow = new NamedProperty("Can Skip TTM Scatter On Superb Throw");
	public static final ISkillProperty canSneakExtraPlayersOntoPitch = new NamedProperty(
		"Can Sneak Extra Players Onto Pitch");
	public static final ISkillProperty canStabAndMoveAfterwards = new NamedProperty("Can Stab And Move Afterwards");
	public static final ISkillProperty canStabTeamMateForBall = new NamedProperty("Can Stab Team Mate For Ball");
	public static final ISkillProperty canStandUpForFree = new NamedProperty("Can Stand Up For Free");
	public static final ISkillProperty canStandUpTeamMates = new NamedProperty("Can Stand Up Team-Mates");
	public static final ISkillProperty canStealBallFromOpponent = new NamedProperty("Can Steal Ball From Opponent");
	public static final ISkillProperty canTakeDownPlayersWithHimOnBothDown = new NamedProperty("Can Take Down Players With Him On Both Down");
	public static final ISkillProperty canTeleportBeforeAndAfterAvRollAttack = new NamedProperty("Can Teleport Before And After Av Roll Attack");
	public static final ISkillProperty canThrowTeamMates = new NamedProperty("Can Throw Team Mates");
	public static final ISkillProperty canUseChainsawOnDownedOpponents = new NamedProperty("Can Use Chainsaw On Downed Opponents");
	public static final ISkillProperty canUseThrowBombActionTwice = new NamedProperty("Can Use Throw Bomb Action Twice");
	public static final ISkillProperty canUseVomitAfterBlock = new NamedProperty("Can Use Vomit After Block");
	public static final ISkillProperty convertKOToStunOn8 = new NamedProperty("Convert KO to Stun on a roll of 8");
	public static final ISkillProperty convertStunToKO = new NamedProperty("Convert Stun to KO");
	public static final ISkillProperty dontDropFumbles = new NamedProperty("Don't Drop 2+ Fumbles");
	public static final ISkillProperty droppedBallCausesArmourRoll = new NamedProperty("Dropped Ball Causes Armour Roll");
	public static final ISkillProperty enableStandUpAndEndBlitzAction = new NamedProperty(
			"Enable Stand Up and End Blitz Action");
	public static final ISkillProperty enableThrowBombAction = new NamedProperty("Enable Throw Bomb Action");
	public static final ISkillProperty flipSameTeamOpponentToOtherTeam = new NamedProperty(
			"Flip Same Team Opponent to Other Team");
	public static final ISkillProperty forceOpponentToDropBallOnPushback = new NamedProperty(
		"Force Opponent To Drop Ball On Pushback");
	public static final ISkillProperty forceOpponentToFollowUp = new NamedProperty("Force Opponent To Follow Up");
	public static final ISkillProperty forceFollowup = new NamedProperty("Force Followup");
	public static final ISkillProperty forceFullMovement = new NamedProperty("Force Full Movement");
	public static final ISkillProperty forceRollBeforeBeingBlocked = new NamedProperty("Force Roll Before Being Blocked");
	public static final ISkillProperty forceSecondBlock = new NamedProperty("Force Second Block");
	public static final ISkillProperty foulBreaksArmourWithoutRoll = new NamedProperty("Foul Breaks Armour Without Roll");
	public static final ISkillProperty fumbledPlayerLandsSafely = new NamedProperty("Fumbled Player Lands Safely");
	public static final ISkillProperty getsSentOffAtEndOfDrive = new NamedProperty("Gets Sent Off At End Of Drive");
	public static final ISkillProperty goForItAfterBlock = new NamedProperty("Go For It After Block");
	public static final ISkillProperty grabOutsideBlock = new NamedProperty("Grab Outside Block");
	public static final ISkillProperty grantsCatchBonusToReceiver = new NamedProperty("Grants Catch Bonus To Receiver");
	public static final ISkillProperty grantsSppWhenHittingOpponentOnTtm = new NamedProperty("Grants Spp When Hitting Opponent On Ttm");
	public static final ISkillProperty grantsSppFromSpecialActionsCas = new NamedProperty("Grants Spp From Special Actions Cas");
	public static final ISkillProperty grantsTeamReRollWhenCausingCas = new NamedProperty("Grants Team Re-Roll When Causing Cas");
	public static final ISkillProperty grantsTeamReRollWhenOnPitch = new NamedProperty(
		"Grants Team Re-Roll When On Pitch");
	public static final ISkillProperty grantsSingleUseTeamRerollWhenOnPitch = new NamedProperty(
		"Grants Single Use Team Reroll When On Pitch");
	public static final ISkillProperty ignoresDefenderStumblesResultForFirstBlock = new NamedProperty("Ignores Defender Stumbles Result For First Block");
	public static final ISkillProperty hasToMissTurn = new NamedProperty("Has To Miss Turn");
	public static final ISkillProperty hasNoTacklezoneForDodging = new NamedProperty("Has No Tacklezone For Dodging");
	public static final ISkillProperty hasToRollToPassBallOn = new NamedProperty("Has To Roll To Pass Ball On");
	public static final ISkillProperty hasToRollToUseTeamReroll = new NamedProperty("Has To Roll To Use Team Reroll");
	public static final ISkillProperty ignoresArmourModifiersFromFouls = new NamedProperty("Ignores Armour Modifiers From Fouls");
	public static final ISkillProperty ignoresArmourModifiersFromSkills = new NamedProperty("Ignores Armour Modifiers From Skills");
	public static final ISkillProperty ignoresArmourModifiersFromSpecialEffects = new NamedProperty("Ignores Armour Modifiers From Special Effects");
	public static final ISkillProperty ignoreBlockAssists = new NamedProperty(
		"Ignore Block Assists");
	public static final ISkillProperty ignoreDefenderStumblesResult = new NamedProperty(
		"Ignore Defender Stumbles Result");
	public static final ISkillProperty ignoreFirstArmourBreak = new NamedProperty("Ignore First Armour Break");
	public static final ISkillProperty ignoreFirstSecretWeaponSentOff = new NamedProperty("Ignore First Secret Weapon Sent Off");
	public static final ISkillProperty ignoreTackleWhenBlocked = new NamedProperty("Ignore Tackle When Blocked");
	public static final ISkillProperty ignoreTacklezonesWhenCatching = new NamedProperty(
		"Ignore Tacklezones when Catching");
	public static final ISkillProperty ignoreTacklezonesWhenDodging = new NamedProperty("Ignore Tacklezones When Dodging");
	public static final ISkillProperty ignoreTacklezonesWhenJumping = new NamedProperty("Ignore Tacklezones When Jumping");
	public static final ISkillProperty ignoreTacklezonesWhenMoving = new NamedProperty("Ignore Tacklezones When Moving");
	public static final ISkillProperty ignoreTacklezonesWhenPassing = new NamedProperty(
			"Ignore Tacklezones when Passing");
	public static final ISkillProperty ignoreTacklezonesWhenPickingUp = new NamedProperty(
			"Ignore Tacklezones When Picking Up");
	public static final ISkillProperty ignoreWeatherWhenPickingUp = new NamedProperty("Ignore Weather when Picking Up");
	public static final ISkillProperty increasesTeamsFame = new NamedProperty("Increases Teams Fame");
	public static final ISkillProperty inflictsConfusion = new NamedProperty("Inflicts Confusion");
	public static final ISkillProperty inflictsDisturbingPresence = new NamedProperty("Inflicts Disturbing Presence");
	public static final ISkillProperty isHurtMoreEasily = new NamedProperty("Is Hurt More Easily");
	public static final ISkillProperty makesDodgingHarder = new NamedProperty("Makes Dodging Harder");
	public static final ISkillProperty makesJumpingHarder = new NamedProperty("Makes Jumping Harder");
	public static final ISkillProperty makesStrengthTestObsolete = new NamedProperty("Makes Strength Test Obsolete");
	public static final ISkillProperty mightEatPlayerToThrow = new NamedProperty("Might Eat Player To Throw");
	public static final ISkillProperty movesRandomly = new NamedProperty("Moves Randomly");
	public static final ISkillProperty needsNoDiceDecorations = new NamedProperty("Needs No Dice Decorations");
	public static final ISkillProperty needsToBeSetUp = new NamedProperty("Needs To Be Set Up");
	public static final ISkillProperty needsToRollForActionButKeepsTacklezone = new NamedProperty("Needs To Roll For Action But Keeps Tacklezone");
	public static final ISkillProperty needsToRollForActionBlockingIsEasier = new NamedProperty("Needs To Roll For Action Blocking Is Easier");
	public static final ISkillProperty needsToRollHighToAvoidConfusion = new NamedProperty("Need To Roll High To Avoid Confusion");
	public static final ISkillProperty failedRushForJumpAlwaysLandsInTargetSquare = new NamedProperty("Failed Rush For Jump Always Lands In Target Square");
	public static final ISkillProperty passesAreInterceptedEasier = new NamedProperty("Passes Are Intercepted Easier");
	public static final ISkillProperty passesAreNotIntercepted = new NamedProperty("Passes Are Not Intercepted");
	public static final ISkillProperty placedProneCausesInjuryRoll = new NamedProperty("Placed Prone Causes Injury Roll");
	public static final ISkillProperty preventArmourModifications = new NamedProperty("Prevent Armour Modifications");
	public static final ISkillProperty preventAutoMove = new NamedProperty("Prevent AutoMove");
	public static final ISkillProperty preventBeingFouled = new NamedProperty("Prevent Being Fouled");
	public static final ISkillProperty preventCardRabbitsFoot = new NamedProperty("Prevent Rabbit's Foot Card");
	public static final ISkillProperty preventCatch = new NamedProperty("Prevent Catch");
	public static final ISkillProperty preventDamagingInjuryModifications = new NamedProperty("Prevent Damaging Injury Modifications");
	public static final ISkillProperty preventFallOnBothDown = new NamedProperty("Prevent Fall on Both Down");
	public static final ISkillProperty preventHoldBall = new NamedProperty("Prevent Hold Ball");
	public static final ISkillProperty preventKickTeamMateAction = new NamedProperty("Prevent Kick Team Mate Action");
	public static final ISkillProperty preventOpponentFollowingUp = new NamedProperty("Prevent Opponent Following Up");
	public static final ISkillProperty preventPickup = new NamedProperty("Prevent Pickup");
	public static final ISkillProperty preventRaiseFromDead = new NamedProperty("Prevent Raise From Dead");
	public static final ISkillProperty preventRecoverFromConcusionAction = new NamedProperty(
		"Prevent Recover from Confusion Action");
	public static final ISkillProperty preventRecoverFromGazeAction = new NamedProperty(
		"Prevent Recover from Gaze Aztion");
	public static final ISkillProperty preventRegularBlitzAction = new NamedProperty("Prevent Regular Blitz Action");
	public static final ISkillProperty preventRegularBlockAction = new NamedProperty("Prevent Regular Block Action");
	public static final ISkillProperty preventRegularFoulAction = new NamedProperty("Prevent Regular Foul Action");
	public static final ISkillProperty preventRegularHandOverAction = new NamedProperty(
		"Prevent Regular Hand Over Action");
	public static final ISkillProperty preventRegularPassAction = new NamedProperty("Prevent Regular Pass Action");
	public static final ISkillProperty preventSecureTheBall = new NamedProperty("Prevent Secure The Ball");
	public static final ISkillProperty preventStandUpAction = new NamedProperty("Prevent Regular Stand Up Action");
	public static final ISkillProperty preventStuntyDodgeModifier = new NamedProperty("Prevent Stunty Dodge Modifier");
	public static final ISkillProperty preventThrowTeamMateAction = new NamedProperty("Prevent Throw Team Mate Action");
	public static final ISkillProperty providesBlockAlternative = new NamedProperty("Provides Block Alternative");
	public static final ISkillProperty providesBlockAlternativeDuringBlitz = new NamedProperty("Provides Block Alternative During Blitz");
	public static final ISkillProperty providesFoulingAlternative = new NamedProperty("Provides Fouling Alternative");
	public static final ISkillProperty providesChainsawBlockAlternative = new NamedProperty("Provides Chainsaw Block Alternative");
	public static final ISkillProperty providesChainsawFoulingAlternative = new NamedProperty("Provides Chainsaw Fouling Alternative");
	public static final ISkillProperty providesMultipleBlockAlternative = new NamedProperty("Provides Multiple Block Alternative");
	public static final ISkillProperty providesStabBlockAlternative = new NamedProperty("Provides Stab Block Alternative");
	public static final ISkillProperty reducesArmourToFixedValue = new NamedProperty("Reduces Armour To Fixed Value");
	public static final ISkillProperty reducesLonerRollIfPartnerIsHurt = new NamedProperty("Reduces Loner If Partner Is Hurt");
	public static final ISkillProperty requiresSecondCasualtyRoll = new NamedProperty("Requires Second Casualty Roll");
	public static final ISkillProperty smallIcon = new NamedProperty("Display with a small icon");
	public static final ISkillProperty setGfiRollToFive = new NamedProperty("Set Gfi Roll To Five");
	public static final ISkillProperty ttmScattersInSingleDirection = new NamedProperty(
			"Throw Team Mate Scatters In Single Direction");
	public static final ISkillProperty weakenOpposingBlitzer = new NamedProperty("Weaken Opposing Blitzer");
}
