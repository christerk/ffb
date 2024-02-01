package com.fumbbl.ffb;

/**
 * 
 * @author Dominic Schabel
 * @author Kalimar
 */
public enum SoundId implements INamedObject {

	BLOCK("block", false), BLUNDER("blunder", false),
	// BOO("boo", true),
	BOUNCE("bounce", false), CATCH("catch", false), CHAINSAW("chainsaw", false),
	// CHEER("cheer", true),
	CLICK("click", false), DING("ding", false), DODGE("dodge", false), DUH("duh", false), EW("ew", false),
	EXPLODE("explode", false), FALL("fall", false), FIREBALL("fireball", false), FOUL("foul", false),
	// GOTTA_HURT("gottaHurt", true),
	HYPNO("hypno", false), INJURY("injury", false), KICK("kick", false), KO("ko", false), LIGHTNING("lightning", false),
	ZAP("zap", false), METAL("metal", false), NOMNOM("nomnom", false), ORGAN("organ", false), PICKUP("pickup", false),
	QUESTION("question", false), RIP("rip", false), ROAR("roar", false), ROOT("root", false), SLURP("slurp", false),
	STAB("stab", false), STEP("step", false), SWOOP("swoop", false), THROW("throw", false), TOUCHDOWN("touchdown", false),
	WHISTLE("whistle", false), WOOOAAAH("woooaaah", false), SPEC_AAH("specAah", true), SPEC_BOO("specBoo", true),
	SPEC_CHEER("specCheer", true), SPEC_CLAP("specClap", true), SPEC_CRICKETS("specCrickets", true),
	SPEC_HURT("specHurt", true), SPEC_LAUGH("specLaugh", true), SPEC_OOH("specOoh", true), SPEC_SHOCK("specShock", true),
	SPEC_STOMP("specStomp", true), PUMP_CROWD("pumpcrowd", false), TRAPDOOR("trapdoor", false), VOMIT("vomit", false);

	private final String fName;
	private final boolean fSpectatorSound;

	SoundId(String pName, boolean pSpectatorSound) {
		fName = pName;
		fSpectatorSound = pSpectatorSound;
	}

	public String getName() {
		return fName;
	}

	public boolean isSpectatorSound() {
		return fSpectatorSound;
	}

}
