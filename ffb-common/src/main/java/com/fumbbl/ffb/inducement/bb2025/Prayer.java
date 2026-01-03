package com.fumbbl.ffb.inducement.bb2025;

import com.fumbbl.ffb.inducement.InducementDuration;
import com.fumbbl.ffb.mechanics.StatsMechanic;
import com.fumbbl.ffb.model.skill.SkillClassWithValue;
import com.fumbbl.ffb.modifiers.PlayerStatKey;
import com.fumbbl.ffb.modifiers.TemporaryEnhancements;
import com.fumbbl.ffb.modifiers.TemporaryStatDecrementer;
import com.fumbbl.ffb.modifiers.TemporaryStatIncrementer;
import com.fumbbl.ffb.modifiers.TemporaryStatModifier;
import com.fumbbl.ffb.skill.mixed.Pro;
import com.fumbbl.ffb.skill.mixed.Loner;
import com.fumbbl.ffb.skill.bb2025.MightyBlow;
import com.fumbbl.ffb.skill.bb2025.Stab;

import java.util.HashSet;

public enum Prayer implements com.fumbbl.ffb.inducement.Prayer {
	TREACHEROUS_TRAPDOOR("Treacherous Trapdoor",
		"Trapdoors appear. On a roll of 1 a player stepping on them falls through them",
		InducementDuration.UNTIL_END_OF_GAME, true),
	FRIENDS_WITH_THE_REF("Friends with the Ref", "Argue the call succeeds on 5+", InducementDuration.UNTIL_END_OF_GAME),
	STILETTO("Stiletto", "One random player available to play this game gains Stab",
		InducementDuration.UNTIL_END_OF_GAME,	false,
		true) {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withSkills(new HashSet<SkillClassWithValue>() {{
				add(new SkillClassWithValue(Stab.class));
			}});
		}

		@Override
		public String eventMessage() {
			return " gains Stab";
		}
	}, IRON_MAN("Iron Man",
		"One chosen player available to play this game improves AV by 1 (Max " + "11+)",
		InducementDuration.UNTIL_END_OF_GAME, false, true) {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withModifiers(new HashSet<TemporaryStatModifier>() {{
				add(new TemporaryStatIncrementer(PlayerStatKey.AV, mechanic));
			}});
		}

		@Override
		public String eventMessage() {
			return " gains 1 AV";
		}
	}, KNUCKLE_DUSTERS("Knuckle Dusters",
		"One chosen player available to play this game gains Mighty Blow",
		InducementDuration.UNTIL_END_OF_GAME, false, true) {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withSkills(new HashSet<SkillClassWithValue>() {{
				add(new SkillClassWithValue(MightyBlow.class));
			}});
		}

		@Override
		public String eventMessage() {
			return " gains Mighty Blow (+1)";
		}
	}, BAD_HABITS("Bad Habits",
		"D3 random opponent players available to play this game without Loner gain Loner (2+)",
		InducementDuration.UNTIL_END_OF_GAME, false, true) {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withSkills(new HashSet<SkillClassWithValue>() {{
				add(new SkillClassWithValue(Loner.class, "2"));
			}});
		}

		@Override
		public String eventMessage() {
			return " gains Loner (2+)";
		}
	}, GREASY_CLEATS("Greasy Cleats",
		"One random opponent player available to play this game has his MA reduced by 1",
		InducementDuration.UNTIL_END_OF_GAME, false, true) {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withModifiers(new HashSet<TemporaryStatModifier>() {{
				add(new TemporaryStatDecrementer(PlayerStatKey.MA, mechanic));
			}});
		}

		@Override
		public String eventMessage() {
			return " loses 1 MA";
		}
	}, BLESSED_STATUE_OF_NUFFLE("Blessed Statue of Nuffle",
		"One chosen player available to play this game gains Pro",
		InducementDuration.UNTIL_END_OF_GAME, false, true) {
		@Override
		public TemporaryEnhancements enhancements(StatsMechanic mechanic) {
			return new TemporaryEnhancements().withSkills(new HashSet<SkillClassWithValue>() {{
				add(new SkillClassWithValue(Pro.class));
			}});
		}

		@Override
		public String eventMessage() {
			return " gains Pro";
		}
	}, MOLES_UNDER_THE_PITCH("Moles under the Pitch", "Rushes from opposing players have a -1 modifier",
		InducementDuration.UNTIL_END_OF_GAME),
	PERFECT_PASSING("Perfect Passing", "Completions generate 2 instead of 1 spp", InducementDuration.UNTIL_END_OF_GAME),
	DAZZLING_CATCHING("Dazzling Catching",
		"Caught passes generate 1 spp (from both teams, does not have to be " + "accurate)",
		InducementDuration.UNTIL_END_OF_GAME),
	FAN_INTERACTION("Fan Interaction", "Casualties caused by crowd pushes generate 2 spp",
		InducementDuration.UNTIL_END_OF_GAME),
	FOULING_FRENZY("Fouling Frenzy", "Casualties caused by fouls generate 2 spp", InducementDuration.UNTIL_END_OF_GAME),
	THROW_A_ROCK("Throw a Rock",
		"Once a game at the start of any turn, a randomly selected opponent on the pitch gets knocked down on a 4+",
		InducementDuration.UNTIL_END_OF_GAME),
	UNDER_SCRUTINY("Under Scrutiny", "Fouls by opposing players are always spotted",
		InducementDuration.UNTIL_END_OF_GAME), INTENSIVE_TRAINING("Intensive Training",
		"One random player available to play this game gains a chosen Primary skill",
		InducementDuration.UNTIL_END_OF_GAME, false, true);

	private final String name, description;
	private final boolean affectsBothTeams, changingPlayer;
	private final InducementDuration duration;

	Prayer(String name, String description, InducementDuration duration) {
		this(name, description, duration, false, false);
	}

	Prayer(String name, String description, InducementDuration duration, boolean affectsBothTeams) {
		this(name, description, duration, affectsBothTeams, false);
	}

	Prayer(String name, String description, InducementDuration duration, boolean affectsBothTeams,
				 boolean changingPlayer) {
		this.name = name;
		this.description = description;
		this.affectsBothTeams = affectsBothTeams;
		this.duration = duration;
		this.changingPlayer = changingPlayer;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean affectsBothTeams() {
		return affectsBothTeams;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public InducementDuration getDuration() {
		return duration;
	}

	@Override
	public boolean isChangingPlayer() {
		return changingPlayer;
	}
}
