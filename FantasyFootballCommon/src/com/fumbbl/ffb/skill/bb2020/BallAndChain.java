package com.fumbbl.ffb.skill.bb2020;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

/**
 * Players armed with a Ball & Chain can only take Move Actions. To move or Go
 * For It, place the throw-in template over the player facing up or down the
 * pitch or towards either sideline. Then roll a D6 and move the player one
 * square in the indicated direction; no Dodge roll is required if you leave a
 * tackle zone. If this movement takes the player off the pitch, they are beaten
 * up by the crowd in the same manner as a player who has been pushed off the
 * pitch. Repeat this process until the player runs out of normal movement (you
 * may GFI using the same process if you wish). If during his Move Action he
 * would move into an occupied square then the player will throw a block
 * following normal blocking rules against whoever is in that square, friend or
 * foe (and it even ignores Foul Appearance!). Prone or Stunned players in an
 * occupied square are pushed back and an Armour roll is made to see if they are
 * injured, instead of the block being thrown at them. The player must follow up
 * if they push back another player, and will then carry on with their move as
 * described above. If the player is ever Knocked Down or Placed Prone roll
 * immediately for injury (no Armour roll is required). Stunned results for any
 * Injury rolls are always treated as KO'd. A Ball & Chain player may use the
 * Grab skill (as if a Block Action was being used) with his blocks (if he has
 * learned it!). A Ball & Chain player may never use the Diving Tackle, Frenzy,
 * Kick-Off Return, Pass Block or Shadowing skills.
 */
@RulesCollection(Rules.BB2020)
public class BallAndChain extends Skill {

	public BallAndChain() {
		super("Ball and Chain", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
		registerProperty(NamedProperties.grabOutsideBlock);
		registerProperty(NamedProperties.placedProneCausesInjuryRoll);
		registerProperty(NamedProperties.ignoreBlockAssists);
		registerProperty(NamedProperties.preventAutoMove);
		registerProperty(NamedProperties.preventPickup);
		registerProperty(NamedProperties.preventRegularBlitzAction);
		registerProperty(NamedProperties.preventRegularBlockAction);
		registerProperty(NamedProperties.preventRegularFoulAction);
		registerProperty(NamedProperties.preventRegularHandOverAction);
		registerProperty(NamedProperties.preventRegularPassAction);
		registerProperty(NamedProperties.preventRecoverFromConcusionAction);
		registerProperty(NamedProperties.preventRecoverFromGazeAction);
		registerProperty(NamedProperties.preventStandUpAction);
		registerProperty(NamedProperties.canBlockMoreThanOnce);
		registerProperty(NamedProperties.forceFollowup);
		registerProperty(NamedProperties.canBlockSameTeamPlayer);
		registerProperty(NamedProperties.preventThrowTeamMateAction);
		registerProperty(NamedProperties.preventKickTeamMateAction);
		registerProperty(NamedProperties.goForItAfterBlock);
		registerProperty(NamedProperties.movesRandomly);
		registerProperty(NamedProperties.blocksDuringMove);
		registerProperty(NamedProperties.ignoreTacklezonesWhenMoving);
		registerProperty(NamedProperties.convertStunToKO);
		registerProperty(new CancelSkillProperty(NamedProperties.canBlockMoreThanOnce));
		registerProperty(new CancelSkillProperty(NamedProperties.canPileOnOpponent));
		registerProperty(new CancelSkillProperty(NamedProperties.forceRollBeforeBeingBlocked));
		registerProperty(new CancelSkillProperty(NamedProperties.inflictsConfusion));
		registerProperty(new CancelSkillProperty(NamedProperties.preventOpponentFollowingUp));
		registerConflictingProperty(NamedProperties.forceSecondBlock);
		registerConflictingProperty(NamedProperties.canPushBackToAnySquare);
		registerConflictingProperty(NamedProperties.canAttemptToTackleDodgingPlayer);
		registerConflictingProperty(NamedProperties.canLeap);
		registerConflictingProperty(NamedProperties.canBlockTwoAtOnce);
		registerConflictingProperty(NamedProperties.canMoveDuringKickOffScatter);
		registerConflictingProperty(NamedProperties.canFollowPlayerLeavingTacklezones);
	}

}
