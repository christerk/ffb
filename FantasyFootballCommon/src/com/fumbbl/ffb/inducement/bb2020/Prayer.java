package com.fumbbl.ffb.inducement.bb2020;

import com.fumbbl.ffb.INamedObject;
import com.fumbbl.ffb.inducement.InducementDuration;

public enum Prayer implements INamedObject {
	TREACHEROUS_TRAPDOOR("Treacherous Trapdoor",
		"Trapdoors appear. On a roll of 1 a player stepping on them falls through them",
		InducementDuration.UNTIL_END_OF_HALF, true),
	FRIENDS_WITH_THE_REF("Friends with the Ref", "Argue the call succeeds on 5+"),
	STILETTO("Stiletto",
		"One random player available to play during this drive without Loner gains Stab",
		InducementDuration.UNTIL_END_OF_GAME),
	IRON_MAN("Iron Man",
		"One chosen player available to play during this drive without Loner improves AV by 1 (Max 11+)"),
	KNUCKLE_DUSTERS("Knuckle Dusters",
		"One chosen player available to play during this drive without Loner gains Mighty Blow (+1)"),
	BAD_HABITS("Bad Habits",
		"D3 random opponent players available to play during this drive without Loner gain Loner (2+)"),
	GREASY_CLEATS("Greasy Cleats",
		"One random opponent player available to play during this drive has his MA reduced by 1"),
	BLESSED_STATUE_OF_NUFFLE("Blessed Statue of Nuffle",
		"One chosen player available to play during this drive without Loner gains Pro"),
	MOLES_UNDER_THE_PITCH("Moles under the Pitch",
		"Rushes have a -1 modifier (-2 if both coaches rolled this result)"),
	PERFECT_PASSING("Perfect Passing",
		"Completions generate 2 instead of 1 spp", InducementDuration.UNTIL_END_OF_GAME),
	FAN_INTERACTION("Fan Interaction",
		"Casualties caused by crowd pushes generate 2 spp"),
	NECESSARY_VIOLENCE("Necessary Violence",
		"Casualties generate 3 instead of 2 spp"),
	FOULING_FRENZY("Fouling Frenzy",
		"Casualties caused by fouls generate 2 spp"),
	THROW_A_ROCK("Throw a Rock",
		"If an opposing player should stall they get hit by a rock on a 5+ and knocked down immediately"),
	UNDER_SCRUTINY("Under Scrutiny",
		"Fouls by opposing players are always spotted",
		InducementDuration.UNTIL_END_OF_HALF),
	INTENSIVE_TRAINING("Intensive Training",
		"One random player available to play during this drive without Loner gains a chosen Primary skill",
		InducementDuration.UNTIL_END_OF_GAME);
	private final String name, description;
	private final boolean affectsBothTeams;
	private final InducementDuration duration;

	Prayer(String name, String description) {
		this(name, description, InducementDuration.UNTIL_END_OF_DRIVE);
	}

	Prayer(String name, String description, InducementDuration duration) {
		this(name, description, duration, false);
	}

	Prayer(String name, String description, InducementDuration duration, boolean affectsBothTeams) {
		this.name = name;
		this.description = description;
		this.affectsBothTeams = affectsBothTeams;
		this.duration = duration;
	}

	@Override
	public String getName() {
		return name;
	}

	public boolean affectsBothTeams() {
		return affectsBothTeams;
	}

	public String getDescription() {
		return description;
	}

	public InducementDuration getDuration() {
		return duration;
	}
}
