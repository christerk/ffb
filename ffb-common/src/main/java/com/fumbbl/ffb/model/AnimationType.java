package com.fumbbl.ffb.model;

import com.fumbbl.ffb.INamedObject;

/**
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
	KICKOFF_VERY_SUNNY("kickoffVerySunny"), KICKOFF_CHARGE("kickoffCharge"), KICKOFF_DODGY_SNACK("kickoffDodgySnack"),
	HAIL_MARY_PASS("hailMaryPass"), THROW_A_ROCK("throwARock"),
	THROW_BOMB("throwBomb"), HAIL_MARY_BOMB("hailMaryBomb"), BOMB_EXPLOSION("bombExplosion"), CARD("card"),
	THROW_KEG("throwKeg"), FUMBLED_KEG("fumbledKeg"), TRICKSTER("trickster"), BREATHE_FIRE("breatheFire"),
	THEN_I_STARTED_BLASTIN("thenIStartedBlastin"),
	PRAYER_TREACHEROUS_TRAPDOOR("prayerTrapdoor"), PRAYER_BAD_HABITS("badhabits"), PRAYER_BLESSED_STATUE_OF_NUFFLE("blessedStatueOfNuffle"),
	PRAYER_FAN_INTERACTION("fanInteraction"), PRAYER_FOULING_FRENZY("foulingFrenzy"), PRAYER_FRIENDS_WITH_THE_REF("friendsWithTheRef"),
	PRAYER_GREASY_CLEATS("greasyCleats"), PRAYER_INTENSIVE_TRAINING("intensiveTraining"), PRAYER_IRON_MAN("ironMan"),
	PRAYER_KNUCKLE_DUSTERS("knuckleDusters"), PRAYER_MOLES_UNDER_THE_PITCH("molesUnderThePitch"), PRAYER_NECESSARY_VIOLENCE("necessaryViolence"),
	PRAYER_PERFECT_PASSING("perfectPassing"), PRAYER_STILETTO("stiletto"), PRAYER_THROW_A_ROCK("throwARockPrayer"),
	PRAYER_DAZZLING_CATCHING("dazzlingCatching"), PRAYER_UNDER_SCRUTINY("underScrutiny");

	private final String fName;

	AnimationType(String pName) {
		fName = pName;
	}

	public String getName() {
		return fName;
	}

}
