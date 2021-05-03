package com.fumbbl.ffb;

/**
 * 
 * @author Kalimar
 */
public enum SendToBoxReason implements INamedObject {

	MNG("mng", "is recovering from a Serious Injury"), FOUL_BAN("foulBan", "was banned for fouling"),
	SECRET_WEAPON_BAN("secretWeaponBan", "was banned for using a Secret Weapon"), FOULED("fouled", "was fouled"),
	BLOCKED("blocked", "was blocked"), CROWD_PUSHED("crowdPushed", "got pushed into the crowd"),
	CROWD_KICKED("crowdKicked", "got kicked into the crowd"), DODGE_FAIL("dodgeFail", "failed a dodge"),
	GFI_FAIL("gfiFail", "failed to go for it"), KICKED("kicked", "got kicked"), JUMP_FAIL("leapFail", "failed a leap"),
	STABBED("stabbed", "has been stabbed"), HIT_BY_ROCK("hitByRock", "has been hit by a rock"),
	EATEN("eaten", "has been eaten"), HIT_BY_THROWN_PLAYER("hitByThrownPlayer", "has been hit by a thrown player"),
	LANDING_FAIL("landingFail", "failed to land after being thrown"), PILED_ON("piledOn", "was piled upon"),
	CHAINSAW("chainsaw", "has been hit by a chainsaw"), BITTEN("bitten", "was bitten by a team-mate"),
	NURGLES_ROT("nurglesRot", "has been infected with Nurgle's Rot"), RAISED("raised", "has been raised from the dead"),
	LIGHTNING("lightning", "has been hit by a lightning bolt"), FIREBALL("fireball", "has been hit by a fireball"),
	KO_ON_PILING_ON("koOnPilingOn", "has been knocked out while Piling On"), BOMB("bomb", "has been hit by a bomb"),
	BALL_AND_CHAIN("ballAndChain", "has been hit by a ball and chain"), PLAGUE_RIDDEN("plagueRidden", "is now plague ridden"),
	PROJECTILE_VOMIT("projectileVomit", "has been hit by projectile vomit");

	private final String fName;
	private final String fReason;

	SendToBoxReason(String pName, String pReason) {
		fName = pName;
		fReason = pReason;
	}

	public String getName() {
		return fName;
	}

	public String getReason() {
		return fReason;
	}

}
