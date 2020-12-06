package com.balancedbytes.games.ffb;

// immutable object
public class PlayerState {

	public static final int UNKNOWN = 0x0000;
	public static final int STANDING = 0x0001;
	public static final int MOVING = 0x0002;
	public static final int PRONE = 0x0003;
	public static final int STUNNED = 0x0004;
	public static final int KNOCKED_OUT = 0x0005;
	public static final int BADLY_HURT = 0x0006;
	public static final int SERIOUS_INJURY = 0x0007;
	public static final int RIP = 0x0008;
	public static final int RESERVE = 0x0009;
	public static final int MISSING = 0x000a;
	public static final int FALLING = 0x000b;
	public static final int BLOCKED = 0x000c;
	public static final int BANNED = 0x000d;
	public static final int EXHAUSTED = 0x000e;
	public static final int BEING_DRAGGED = 0x000f;
	public static final int PICKED_UP = 0x0010;
	public static final int HIT_BY_FIREBALL = 0x0011; // used for bloodSpots only
	public static final int HIT_BY_LIGHTNING = 0x0012; // used for bloodSpots only
	public static final int HIT_BY_BOMB = 0x0013; // used for bloodSpots only

	private static final int _BIT_ACTIVE = 0x0100;
	private static final int _BIT_CONFUSED = 0x0200;
	private static final int _BIT_ROOTED = 0x0400;
	private static final int _BIT_HYPNOTIZED = 0x0800;
	private static final int _BIT_BLOODLUST = 0x1000;
	private static final int _BIT_USED_PRO = 0x2000;

	private static int[] _BASE_MASK = new int[] { 0x0000, // UNKNOWN
			0xff00, // STANDING
			0xff00, // MOVING
			0xff00, // PRONE
			0xff00, // STUNNED
			0x0000, // KNOCKED_OUT
			0x0000, // BADLY_HURT
			0x0000, // SERIOUS_INJURY
			0x0000, // RIP
			0x0000, // RESERVE
			0x0000, // MISSING
			0xff00, // FALLING
			0xff00, // BLOCKED
			0x0000, // BANNED
			0xff00, // EXHAUSTED
			0xff00, // BEING_DRAGGED
			0xff00, // PICKED_UP
	};

	private int fId;

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
		return (getId() & 0x00ff);
	}

	public PlayerState changeBase(int pBase) {
		int baseMask = ((pBase > 0) && (pBase < _BASE_MASK.length)) ? _BASE_MASK[pBase] : 0x0000;
		return new PlayerState((getId() & baseMask) | pBase);
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

	public boolean hasBloodlust() {
		return hasBit(_BIT_BLOODLUST);
	}

	public PlayerState changeBloodlust(boolean pBloodlust) {
		return changeBit(_BIT_BLOODLUST, pBloodlust);
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

	public boolean canBeSetUp() {
		return ((STANDING == getBase()) || (MOVING == getBase()) || (PRONE == getBase()) || (STUNNED == getBase())
				|| (RESERVE == getBase()) || (FALLING == getBase()) || (BLOCKED == getBase()));
	}

	public boolean hasTacklezones() {
		return (((STANDING == getBase()) || (MOVING == getBase()) || (BLOCKED == getBase())) && !isConfused()
				&& !isHypnotized());
	}

	public boolean isProne() {
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

	private PlayerState changeBit(int pMask, boolean pBit) {
		if (pBit) {
			return new PlayerState(getId() | pMask);
		} else {
			return new PlayerState(getId() & (0xffff ^ pMask));
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
		if (fId != other.fId)
			return false;
		return true;
	}

}
