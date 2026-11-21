package com.fumbbl.ffb.model;

import com.fumbbl.ffb.skill.bb2025.SquigRage;
import com.fumbbl.ffb.skill.bb2025.special.ConsummateProfessional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ActingPlayerHasActedTest {
	private final SquigRage squigRage = new SquigRage();
	private final ConsummateProfessional otherSkill = new ConsummateProfessional();
	private ActingPlayer actingPlayer;

	@BeforeEach
	void setUp() {
		squigRage.postConstruct();
		otherSkill.postConstruct();
		Game game = mock(Game.class);
		Player<?> player = mock(Player.class);
		when(game.getPlayerById("squig")).thenAnswer(invocation -> player);
		actingPlayer = new ActingPlayer(game);
		actingPlayer.setPlayerId("squig");
	}

	@Test
	void usingOnlyTheIgnoredSkillDoesNotCountAsActing() {
		actingPlayer.markSkillUsed(squigRage);

		assertTrue(actingPlayer.hasActed());
		assertFalse(actingPlayer.hasActedIgnoring(squigRage));
	}

	@Test
	void usingAnotherSkillCountsAsActing() {
		actingPlayer.markSkillUsed(squigRage);
		actingPlayer.markSkillUsed(otherSkill);

		assertTrue(actingPlayer.hasActedIgnoring(squigRage));
	}

	@Test
	void movingCountsAsActing() {
		actingPlayer.markSkillUsed(squigRage);
		actingPlayer.setHasMoved(true);

		assertTrue(actingPlayer.hasActedIgnoring(squigRage));
	}

	@Test
	void notActingAtAll() {
		assertFalse(actingPlayer.hasActed());
		assertFalse(actingPlayer.hasActedIgnoring(squigRage));
	}
}
