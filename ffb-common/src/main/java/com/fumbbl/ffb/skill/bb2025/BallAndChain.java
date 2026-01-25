package com.fumbbl.ffb.skill.bb2025;

import com.fumbbl.ffb.RulesCollection;
import com.fumbbl.ffb.RulesCollection.Rules;
import com.fumbbl.ffb.SkillCategory;
import com.fumbbl.ffb.model.property.CancelSkillProperty;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;

@RulesCollection(Rules.BB2025)
public class BallAndChain extends Skill {

	public BallAndChain() {
		super("Ball and Chain", SkillCategory.TRAIT);
	}

	@Override
	public void postConstruct() {
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
		registerProperty(new CancelSkillProperty(NamedProperties.canMoveBeforeBeingBlocked));
		registerConflictingProperty(NamedProperties.canAttemptToTackleDodgingPlayer);
		registerConflictingProperty(NamedProperties.canRemoveOpponentAssists);
		registerConflictingProperty(NamedProperties.forceSecondBlock);
		registerConflictingProperty(NamedProperties.canPushBackToAnySquare);
		registerConflictingProperty(NamedProperties.canMoveAfterBlock);
		registerConflictingProperty(NamedProperties.canLeap);
		registerConflictingProperty(NamedProperties.canBlockTwoAtOnce);
		registerConflictingProperty(NamedProperties.canMoveDuringKickOffScatter);
		registerConflictingProperty(NamedProperties.canFollowPlayerLeavingTacklezones);
		registerConflictingProperty(NamedProperties.canAvoidFallingDown);
	}

}
