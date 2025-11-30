package com.fumbbl.ffb;

import com.fumbbl.ffb.model.Player;

public enum SkillUse implements INamedObject {

	WOULD_NOT_HELP("wouldNotHelp", "because it would not help"),
	NO_TEAM_MATE_IN_RANGE("noTeamMateInRange", "because no team-mate is in range"),
	STOP_OPPONENT("stopOpponent", "to stop %g opponent"),
	PUSH_BACK_OPPONENT("pushBackOpponent", "to push %g opponent back"),
	BRING_DOWN_OPPONENT("bringDownOppponent", "to bring %g opponent down"),
	AVOID_PUSH("avoidPush", "to avoid being pushed"),
	CANCEL_FEND("cancelFend", "to cancel %g opponent's Fend skill"),
	CANCEL_STAND_FIRM("cancelStandFirm", "to cancel %g opponent's Stand Firm skill"),
	STAY_AWAY_FROM_OPPONENT("stayAwayFromOpponent", "to stay away from %g opponent"),
	CATCH_BALL("catchBall", "to catch the ball"),
	STEAL_BALL("stealBall", "to steal the ball"),
	CANCEL_STRIP_BALL("cancelStripBall", "to cancel %g opponent's Strip Ball skill"),
	HALVE_KICKOFF_SCATTER("halveKickoffScatter", "to halve the kickoff scatter"),
	CANCEL_DODGE("cancelDodge", "to cancel %g opponent's Dodge skill"),
	CANCEL_WATCH_OUT("cancelWatchOut", "to cancel %g opponent's Watch Out! skill"),

	AVOID_FALLING("avoidFalling", "to avoid falling"),
	CANCEL_TACKLE("cancelTackle", "to cancel %g opponent's Tackle skill"),
	INCREASE_STRENGTH_BY_1("increaseStrengthBy1", "to increase %g strength by 1"),
	CANCEL_DIVING_CATCH("cancelDivingCatch", "because players from both teams hinder each other"),
	PLACE_BALL("placeBall", "to place ball in an empty adjacent square"),
	RE_ROLL_SINGLE_ARMOUR_DIE("reRollSingleArmourDie", "to re-roll a single armour die"),
	ADD_ARMOUR_MODIFIER("addArmourModifier", "to add +1 to the armour roll"),
	INCREASE_CHAINSAW_DAMAGE("increaseChainsawDamage", "to add +4 instead of +3 to armour roll"),
	ADD_INJURY_MODIFIER("addInjuryModifier", "to add +1 to injury roll"),
	RE_ROLL_INJURY("reRollInjury", "to re-roll the injury roll"),
	RE_ROLL_ARMOUR("reRollArmour", "to re-roll the armour roll"),
	FUMBLED_PLAYER_LANDS_SAFELY("fumbledPlayerLandsSafely", "to let the fumbled player land safely"),
	GAIN_FRENZY_FOR_BLITZ("gainFrenzy", "to gain the Frenzy skill for this Blitz action"),
	GAIN_GAZE("gainFrenzy", "to gain the Hypnotic Gaze skill"),
	GAIN_HAIL_MARY("gainHailMary", "to gain Hail Mary Pass skill"),
	TREACHEROUS("treacherous", "to steal the ball from %g team mate"),
	RUSH_ADDITIONAL_SQUARE_ONCE("rushAdditionalSquareOnce", "to rush an additional square"),
	ADD_STRENGTH_TO_ROLL("addStrengthToRoll", "to add %g strength to the roll"),
	GAIN_GRANTED_SKILL("gainGrantedSkill", "to gain a skill for this turn"),
	IGNORE_SENT_OFF("ignoreSentOff", "to not be ejected"),
	MOVE_OPEN_TEAM_MATE("moveOpenTeamMate", "to move a team-mate"),
	MOVE_SQUARE("moveSquare", "to move a square"),
	ADD_BLOCK_DIE("addBlockDie", "to add a block die"),
	PERFORM_SECOND_CHAINSAW_ATTACK("performSecondChainsawAttack", "to perform a second chainsaw attack"),
	PERFORM_SECOND_TWO_BLOCKS("performSecondTwoBlocks", "to perform two block actions"),
	FORCE_BOMB_EXPLOSION("forceBombExplosion", "to force the bomb to explode"),
	RE_ROLL_DIRECTION("reRollDirection", "to re-roll the direction roll"),
	GRANT_CATCH_BONUS("grantCatchBonus", "to grant %g team-mate a catch bonus"),
	RE_ROLL_CATCH("reRollCatch", "to re-roll the catch roll"),
	LOOK_INTO_MY_EYES("lookIntoMyEyes", "to steal the ball from %g opponent"),
	MAKE_OPPONENT_MISS_TURN("makeOpponentMissTurn", "to make an opponent player miss a turn"),
	LASH_OUT_AGAINST_OPPONENT("lashOutAgainstOpponent", "to lash out against an opponent player instead"),
	EASY_INTERCEPT("easyIntercept", "to try an easy interception"),
	PERFORM_ADDITIONAL_ATTACK("performAdditionalAttack", "to perform an additional attack"),
	CANCEL_WRESTLE("cancelWrestle", "to cancel wrestle"),
	REMOVE_TACKLEZONE("removeTacklezone", "to remove opponents tacklezone"),
	GET_BALL_ON_GROUND("getBallFromGround", "to try getting the ball on the ground"),
	PASS_DODGE_WITHOUT_MODIFIERS("passDodgeWithoutModifiers", "to pass the dodge roll ignoring modifiers"),
	PASS_JUMP_WITHOUT_MODIFIERS("passJumpWithoutModifiers", "to pass the jump roll ignoring modifiers"),
	PASS_RUSH_WITHOUT_MODIFIERS("passRushWithoutModifiers", "to pass the rush roll ignoring modifiers"),
	QUICK_BITE("quickBite", "to try to get the ball"),
	STEADY_FOOTING("steadyFooting", "to keep standing"),
	NO_TACKLEZONE("noTackleZone", "because they have no tacklezones"),
	FORCE_FOLLOW_UP("forceFollowUp", "to force %g opponent to follow up"),
	EYE_GOUGED("eyeGouged", "to gouge %g opponent's eyes");

	private final String fName;
	private final String fDescription;

	private static final String _PARAMETER_GENITIVE = "%g";

	SkillUse(String pName, String pDescription) {
		fName = pName;
		fDescription = pDescription;
	}

	public String getName() {
		return fName;
	}

	public String getDescription(Player<?> pPlayer) {
		if (pPlayer != null) {
			return fDescription.replaceAll(_PARAMETER_GENITIVE, pPlayer.getPlayerGender().getGenitive());
		} else {
			return fDescription;
		}
	}

}
