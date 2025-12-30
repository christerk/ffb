package com.fumbbl.ffb;

import java.util.ArrayList;
import java.util.List;

// immutable object
public class PlayerState {

	public static final int UNKNOWN = 0x00000;
	public static final int STANDING = 0x00001;
	public static final int MOVING = 0x00002;
	public static final int PRONE = 0x00003;
	public static final int STUNNED = 0x00004;
	public static final int KNOCKED_OUT = 0x00005;
	public static final int BADLY_HURT = 0x00006;
	public static final int SERIOUS_INJURY = 0x00007;
	public static final int RIP = 0x00008;
	public static final int RESERVE = 0x00009;
	public static final int MISSING = 0x0000a;
	public static final int FALLING = 0x0000b;
	public static final int BLOCKED = 0x0000c;
	public static final int BANNED = 0x0000d;
	public static final int EXHAUSTED = 0x0000e;
	public static final int BEING_DRAGGED = 0x0000f;
	public static final int PICKED_UP = 0x00010;
	public static final int HIT_ON_GROUND = 0x00011;
	public static final int HIT_BY_FIREBALL = 0x00011; // used for bloodSpots only
	public static final int HIT_BY_LIGHTNING = 0x00012; // used for bloodSpots only
	public static final int HIT_BY_BOMB = 0x00013; // used for bloodSpots only
	public static final int SETUP_PREVENTED = 0x00014;
	public static final int IN_THE_AIR = 0x00015;
	private static final int _BIT_ACTIVE = 0x00100;
	private static final int _BIT_CONFUSED = 0x00200;
	private static final int _BIT_ROOTED = 0x00400;
	private static final int _BIT_HYPNOTIZED = 0x00800;
	private static final int _BIT_SELECTED_STAB_TARGET = 0x01000;
	private static final int _BIT_USED_PRO = 0x02000;
	private static final int _BIT_SELECTED_BLITZ_TARGET = 0x04000;
	private static final int _BIT_SELECTED_BLOCK_TARGET = 0x08000;
	private static final int _BIT_SELECTED_GAZE_TARGET = 0x10000;
	private static final int _BIT_EYE_GOUGED = 0x20000;

	private static final int[] _BASE_MASK = new int[]{
		0x00000, // UNKNOWN
		0xfff00, // STANDING
		0xfff00, // MOVING
		0xfff00, // PRONE
		0xfff00, // STUNNED
		0x00000, // KNOCKED_OUT
		0x00000, // BADLY_HURT
		0x00000, // SERIOUS_INJURY
		0x00000, // RIP
		0x00000, // RESERVE
		0x00000, // MISSING
		0xfff00, // FALLING
		0xfff00, // BLOCKED
		0x00000, // BANNED
		0xfff00, // EXHAUSTED
		0xfff00, // BEING_DRAGGED
		0xfff00, // PICKED_UP
		0xfff00, // HIT_ON_GROUND
		0xfff00, // SETUP_PREVENTED
		0xfff00, // IN_THE_AIR
	};

	public static List<Integer> REMOVED_FROM_PLAY = new ArrayList<Integer>() {{
		add(BANNED);
		add(BADLY_HURT);
		add(SERIOUS_INJURY);
		add(RIP);
	}};

	private final int fId;

	public PlayerState(int pId) {
		fId = pId;
	}

	public String toString() {
		return String.valueOf(fId);
	}

	public int getId() {
		return fId;
	}

	public int getBase() {
		return (getId() & 0x000ff);
	}

	public PlayerState changeBase(int pBase) {
		int baseMask = ((pBase > 0) && (pBase < _BASE_MASK.length)) ? _BASE_MASK[pBase] : 0x00000;
		return new PlayerState((getId() & baseMask) | pBase);
	}


	public boolean isSelectedBlitzTarget() {
		return hasBit(_BIT_SELECTED_BLITZ_TARGET);
	}

	public PlayerState addSelectedBlitzTarget() {
		if (!hasBit(_BIT_SELECTED_BLITZ_TARGET)) {
			return changeBit(_BIT_SELECTED_BLITZ_TARGET, true);
		}
		return this;
	}

	public PlayerState removeSelectedBlitzTarget() {
		if (hasBit(_BIT_SELECTED_BLITZ_TARGET)) {
			return changeBit(_BIT_SELECTED_BLITZ_TARGET, false);
		}
		return this;
	}


	public boolean isActive() {
		return hasBit(_BIT_ACTIVE);
	}

	public PlayerState changeActive(boolean pActive) {
		return changeBit(_BIT_ACTIVE, pActive);
	}

	public boolean isConfused() {
		return hasBit(_BIT_CONFUSED);
	}

	public PlayerState changeConfused(boolean pConfused) {
		return changeBit(_BIT_CONFUSED, pConfused);
	}

	public boolean isRooted() {
		return hasBit(_BIT_ROOTED);
	}

	public PlayerState changeRooted(boolean pRooted) {
		return changeBit(_BIT_ROOTED, pRooted);
	}

	public boolean isHypnotized() {
		return hasBit(_BIT_HYPNOTIZED);
	}

	public PlayerState changeHypnotized(boolean pHypnotized) {
		return changeBit(_BIT_HYPNOTIZED, pHypnotized);
	}

	public PlayerState recoverTacklezones() {
		return changeHypnotized(false).changeConfused(false);
	}

	public boolean isEyeGouged() {
		return hasBit(_BIT_EYE_GOUGED);
	}

	public PlayerState changeEyeGouged(boolean gouged) {
		return changeBit(_BIT_EYE_GOUGED, gouged);
	}

	public PlayerState clearEyeGouge() {
		return changeEyeGouged(false);
	}

	public boolean isSelectedStabTarget() {
		return hasBit(_BIT_SELECTED_STAB_TARGET);
	}

	public PlayerState changeSelectedStabTarget(boolean isSelectedStabTarget) {
		return changeBit(_BIT_SELECTED_STAB_TARGET, isSelectedStabTarget);
	}

	public boolean isSelectedBlockTarget() {
		return hasBit(_BIT_SELECTED_BLOCK_TARGET);
	}

	public PlayerState changeSelectedBlockTarget(boolean isSelectedBlockTarget) {
		return changeBit(_BIT_SELECTED_BLOCK_TARGET, isSelectedBlockTarget);
	}

	public boolean isSelectedGazeTarget() {
		return hasBit(_BIT_SELECTED_GAZE_TARGET);
	}

	public PlayerState changeSelectedGazeTarget(boolean isSelectedBlockTarget) {
		return changeBit(_BIT_SELECTED_GAZE_TARGET, isSelectedBlockTarget);
	}

	public PlayerState removeAllTargetSelections() {
		return changeSelectedGazeTarget(false).removeSelectedBlitzTarget();
	}

	public boolean hasUsedPro() {
		return hasBit(_BIT_USED_PRO);
	}

	public PlayerState changeUsedPro(boolean pUsedPro) {
		return changeBit(_BIT_USED_PRO, pUsedPro);
	}

	public boolean isCasualty() {
		return ((BADLY_HURT == getBase()) || (SERIOUS_INJURY == getBase()) || isKilled());
	}

	public boolean isKilled() {
		return (RIP == getBase());
	}

	public boolean canBeSetUpNextDrive() {
		return ((STANDING == getBase()) || (MOVING == getBase()) || (PRONE == getBase()) || (STUNNED == getBase())
			|| (RESERVE == getBase()) || (FALLING == getBase()) || (HIT_ON_GROUND == getBase()) || (BLOCKED == getBase()));
	}

	public boolean canBeMovedDuringSetup() {
		return STANDING == getBase() || RESERVE == getBase();
	}

	public boolean hasTacklezones() {
		return (((STANDING == getBase()) || (MOVING == getBase()) || (BLOCKED == getBase())) && !isConfused()
			&& !isHypnotized());
	}

	public boolean isProneOrStunned() {
		return ((PRONE == getBase()) || (STUNNED == getBase()));
	}

	public boolean isStunned() {
		return (STUNNED == getBase());
	}

	public boolean isAbleToMove() {
		return (((STANDING == getBase()) || (MOVING == getBase()) || (PRONE == getBase())) && isActive() && !isRooted());
	}

	public boolean canBeBlocked() {
		return ((STANDING == getBase()) || (MOVING == getBase()));
	}

	public boolean canBeFouled() {
		return ((PRONE == getBase()) || (STUNNED == getBase()));
	}

	// added this to keep the same wording as the rulebook
	public boolean isStanding() {
		return ((STANDING == getBase()) || (MOVING == getBase()) || (BLOCKED == getBase()));
	}

	public boolean isDistracted() {
		return isConfused() || isHypnotized();
	}

	public boolean isCarried() {
		return ((PICKED_UP == getBase()) || (IN_THE_AIR == getBase()));
	}

	private PlayerState changeBit(int pMask, boolean pBit) {
		if (pBit) {
			return new PlayerState(getId() | pMask);
		} else {
			return new PlayerState(getId() & (0xfffff ^ pMask));
		}
	}

	private boolean hasBit(int pMask) {
		return ((getId() & pMask) > 0);
	}

	public String getDescription() {
		switch (getBase()) {
			case UNKNOWN:
				return "is unknown";
			case STANDING:
				return "is standing";
			case MOVING:
				return "is moving";
			case PRONE:
				return "is prone";
			case STUNNED:
				return "has been stunned";
			case KNOCKED_OUT:
				return "has been knocked out";
			case BADLY_HURT:
				return "has been badly hurt";
			case SERIOUS_INJURY:
				return "has been seriously injured";
			case RIP:
				return "has been killed";
			case RESERVE:
				return "is in reserve";
			case MISSING:
				return "is missing the game";
			case FALLING:
				return "is about to fall down";
			case BLOCKED:
				return "is being blocked";
			case BANNED:
				return "is banned from the game";
			case EXHAUSTED:
				return "is exhausted";
			case BEING_DRAGGED:
				return "is being dragged";
			case PICKED_UP:
				return "has been picked up";
			case HIT_ON_GROUND:
				return "was hit while on the ground";
			case SETUP_PREVENTED:
				return "can not be set up";
			case IN_THE_AIR:
				return "is in the air";
			default:
				return null;
		}
	}

	public String getButtonText() {
		switch (getBase()) {
			case UNKNOWN:
				return "Unknown";
			case STANDING:
				return "Standing";
			case MOVING:
				return "Moving";
			case PRONE:
				return "Prone";
			case STUNNED:
				return "has been stunned";
			case KNOCKED_OUT:
				return "Knocked Out";
			case BADLY_HURT:
				return "Badly Hurt";
			case SERIOUS_INJURY:
				return "Serious Injury";
			case RIP:
				return "Killed";
			case RESERVE:
				return "Reserve";
			case MISSING:
				return "Missing";
			case FALLING:
				return "Falling Down";
			case BLOCKED:
				return "Blocked";
			case BANNED:
				return "Banned";
			case EXHAUSTED:
				return "Exhausted";
			case BEING_DRAGGED:
				return "Being Dragged";
			case PICKED_UP:
				return "Picked Up";
			case HIT_ON_GROUND:
				return "Hit on the ground";
			case SETUP_PREVENTED:
				return "Can't be set up";
			case IN_THE_AIR:
				return "In the air";
			default:
				return null;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlayerState other = (PlayerState) obj;
		return fId == other.fId;
	}

}
