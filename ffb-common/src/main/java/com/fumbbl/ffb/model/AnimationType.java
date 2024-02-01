package com.fumbbl.ffb.model;

import com.fumbbl.ffb.INamedObject;

/**
 * 
 * @author Kalimar
 */
public enum AnimationType implements INamedObject {

	PASS("pass"), THROW_TEAM_MATE("throwTeamMate"), KICK_TEAM_MATE("kickTeamMate"), KICK("kick"),
	SPELL_FIREBALL("spellFireball"), SPELL_LIGHTNING("spellLightning"), SPELL_ZAP("spellZap"),
	KICKOFF_BLITZ("kickoffBlitz"), KICKOFF_BLIZZARD("kickoffBlizzard"),
	KICKOFF_BRILLIANT_COACHING("kickoffBrilliantCoaching"), KICKOFF_CHEERING_FANS("kickoffCheeringFans"),
	KICKOFF_GET_THE_REF("kickoffGetTheRef"), KICKOFF_HIGH_KICK("kickoffHighKick"), KICKOFF_NICE("kickoffNice"),
	KICKOFF_PERFECT_DEFENSE("kickoffPerfectDefense"), KICKOFF_SOLID_DEFENSE("kickoffSolidDefense"),
	KICKOFF_PITCH_INVASION("kickoffPitchInvasion"), KICKOFF_OFFICIOUS_REF("kickoffOfficiousRef"),
	KICKOFF_POURING_RAIN("kickoffPouringRain"), KICKOFF_QUICK_SNAP("kickoffQuickSnap"), KICKOFF_RIOT("kickoffRiot"),
	KICKOFF_TIMEOUT("kickoffTimeout"),
	KICKOFF_SWELTERING_HEAT("kickoffSwelteringHeat"), KICKOFF_THROW_A_ROCK("kickoffThrowARock"),
	KICKOFF_VERY_SUNNY("kickoffVerySunny"), HAIL_MARY_PASS("hailMaryPass"), THROW_A_ROCK("throwARock"),
	THROW_BOMB("throwBomb"), HAIL_MARY_BOMB("hailMaryBomb"), BOMB_EXLOSION("bombExplosion"), CARD("card"),
	THROW_KEG("throwKeg"), FUMBLED_KEG("fumbledKeg");

	private final String fName;

	AnimationType(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
