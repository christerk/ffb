package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InjuryModificationOrderingServiceTest {

	private final InjuryModificationOrderingService service = new InjuryModificationOrderingService();

	@Test
	void ordersByUsageThenRestrictionThenName() {
		List<InjuryModificationCandidate> candidates = new ArrayList<>(Arrays.asList(
			candidate("Old Pro", SkillUsageType.ONCE_PER_GAME, new NoRestriction()),
			candidate("Slayer", SkillUsageType.ONCE_PER_GAME, new KeywordRestriction(Keyword.BIG_GUY)),
			candidate("Lone Fouler", SkillUsageType.ONCE_PER_TURN, new NoRestriction()),
			candidate("Mighty Blow", SkillUsageType.REGULAR, new NoRestriction()),
			candidate("Crushing Blow", SkillUsageType.ONCE_PER_GAME, new NoRestriction()),
			candidate("Half", SkillUsageType.ONCE_PER_HALF, new NoRestriction()),
			candidate("Drive", SkillUsageType.ONCE_PER_DRIVE, new NoRestriction())
		));

		candidates.sort(service.comparator());

		assertEquals(Arrays.asList("Mighty Blow", "Lone Fouler", "Drive", "Half", "Slayer", "Crushing Blow", "Old Pro"),
			candidates.stream().map(candidate -> candidate.getSkill().getName()).collect(Collectors.toList()));
	}

	@Test
	void statRestrictionBeforeKeywordRestriction() {
		ModificationRestriction stat = mock(ModificationRestriction.class);
		when(stat.type()).thenReturn(RestrictionType.STAT);
		List<InjuryModificationCandidate> candidates = new ArrayList<>(Arrays.asList(
			candidate("A", SkillUsageType.ONCE_PER_GAME, new KeywordRestriction(Keyword.BIG_GUY)),
			candidate("B", SkillUsageType.ONCE_PER_GAME, stat)
		));

		candidates.sort(service.comparator());

		assertEquals("B", candidates.get(0).getSkill().getName());
	}

	@Test
	void specialAndTeamMateUsageRankedLikeOncePerTurn() {
		assertEquals(service.usageRank(skill("a", SkillUsageType.ONCE_PER_TURN)),
			service.usageRank(skill("b", SkillUsageType.SPECIAL)));
		assertEquals(service.usageRank(skill("a", SkillUsageType.ONCE_PER_TURN)),
			service.usageRank(skill("b", SkillUsageType.ONCE_PER_TURN_BY_TEAM_MATE)));
	}

	private Skill skill(String name, SkillUsageType usageType) {
		Skill skill = mock(Skill.class);
		when(skill.getName()).thenReturn(name);
		when(skill.getSkillUsageType()).thenReturn(usageType);
		return skill;
	}

	private InjuryModificationCandidate candidate(String name, SkillUsageType usageType,
		ModificationRestriction restriction) {
		ChainableInjuryModification modification = mock(ChainableInjuryModification.class);
		Skill skill = skill(name, usageType);
		when(modification.getSkill()).thenReturn(skill);
		when(modification.restriction()).thenReturn(restriction);
		return new InjuryModificationCandidate(null, modification);
	}
}
