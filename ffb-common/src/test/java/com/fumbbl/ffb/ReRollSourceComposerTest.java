package com.fumbbl.ffb;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReRollSourceComposerTest {

	private final ReRollSourceComposer composer = new ReRollSourceComposer();

	private static final List<ReRollSource> TEAM_SOURCES =
		Arrays.asList(null, ReRollSources.TEAM_RE_ROLL, ReRollSources.SINGLE_USE_RE_ROLL);

	@Test
	void composedSourcesDecomposeIntoTheirParts() {
		for (boolean pro : new boolean[]{false, true}) {
			for (boolean mascot : new boolean[]{false, true}) {
				for (ReRollSource teamSource : TEAM_SOURCES) {
					ReRollSource composed = composer.compose(pro, mascot, teamSource);
					if (!pro && !mascot && teamSource == null) {
						assertNull(composed);
						continue;
					}
					ReRollSourceComposition composition = composer.decompose(composed);
					assertEquals(pro, composition.isPro(), composed.getName());
					assertEquals(mascot, composition.isMascot(), composed.getName());
					assertSame(teamSource, composition.getTeamReRollSource(), composed.getName());
				}
			}
		}
	}

	@Test
	void existingSourcesAreReused() {
		assertSame(ReRollSources.TEAM_RE_ROLL, composer.compose(false, false, ReRollSources.TEAM_RE_ROLL));
		assertSame(ReRollSources.MASCOT, composer.compose(false, true, null));
		assertSame(ReRollSources.MASCOT_TRR, composer.compose(false, true, ReRollSources.TEAM_RE_ROLL));
		assertSame(ReRollSources.PRO, composer.compose(true, false, null));
		assertSame(ReRollSources.PRO_TRR, composer.compose(true, false, ReRollSources.TEAM_RE_ROLL));
		assertSame(ReRollSources.PRO_MASCOT, composer.compose(true, true, null));
		assertSame(ReRollSources.PRO_MASCOT_TRR, composer.compose(true, true, ReRollSources.TEAM_RE_ROLL));
	}

	@Test
	void leaderIsSentAsTeamReRoll() {
		assertSame(ReRollSources.TEAM_RE_ROLL, composer.compose(false, false, ReRollSources.LEADER));
		assertSame(ReRollSources.PRO_MASCOT_TRR, composer.compose(true, true, ReRollSources.LEADER));
	}

	@Test
	void skillSourcesAreNotDecomposed() {
		assertNull(composer.decompose(ReRollSources.DODGE));
		assertNull(composer.decompose(ReRollSources.LEADER));
		assertNull(composer.decompose(null));
	}

	@Test
	void noTeamReRollSourcesWithoutProperties() {
		assertTrue(composer.teamReRollSources(properties(), true).isEmpty());
	}

	@Test
	void teamReRollOnlyWithoutLeaderOrSingleUse() {
		assertEquals(Collections.singletonList(ReRollSources.TEAM_RE_ROLL),
			composer.teamReRollSources(properties(ReRollProperty.TRR), true));
	}

	@Test
	void mascotIsOfferedEvenWithoutTeamReRolls() {
		assertEquals(Collections.singletonList(ReRollSources.MASCOT),
			composer.teamReRollSources(properties(ReRollProperty.MASCOT), true));
	}

	@Test
	void mascotIsExcludedOnRequest() {
		assertEquals(Collections.singletonList(ReRollSources.TEAM_RE_ROLL),
			composer.teamReRollSources(properties(ReRollProperty.MASCOT, ReRollProperty.TRR), false));
	}

	@Test
	void leaderReplacesTeamReRoll() {
		assertEquals(Arrays.asList(ReRollSources.MASCOT, ReRollSources.LEADER),
			composer.teamReRollSources(properties(ReRollProperty.MASCOT, ReRollProperty.TRR, ReRollProperty.LEADER), true));
	}

	@Test
	void singleUseAndLeaderAreOfferedTogether() {
		assertEquals(Arrays.asList(ReRollSources.MASCOT, ReRollSources.SINGLE_USE_RE_ROLL, ReRollSources.LEADER),
			composer.teamReRollSources(properties(ReRollProperty.MASCOT, ReRollProperty.TRR, ReRollProperty.SINGLE_USE,
				ReRollProperty.LEADER), true));
	}

	@Test
	void singleUseReplacesTeamReRoll() {
		assertEquals(Collections.singletonList(ReRollSources.SINGLE_USE_RE_ROLL),
			composer.teamReRollSources(properties(ReRollProperty.TRR, ReRollProperty.SINGLE_USE), true));
	}

	@Test
	void singleUseRequiresTeamReRollAvailability() {
		assertTrue(composer.teamReRollSources(properties(ReRollProperty.SINGLE_USE, ReRollProperty.LEADER), true)
			.isEmpty());
	}

	private HasReRollProperties properties(ReRollProperty... properties) {
		List<ReRollProperty> list = Arrays.asList(properties);
		return list::contains;
	}
}
