package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.ApothecaryMode;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Foul;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class SavageMaulingChainModificationTest {

	private ChainModificationFixture fixture;
	private SavageMaulingChainModification modification;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		modification = new SavageMaulingChainModification(fixture.evaluationService, fixture.copyService);
		modification.setSkill(fixture.skill("Savage Mauling", 0, 0));
	}

	@Test
	void offeredWithoutCasualty() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 2, 3)));
	}

	@Test
	void notOfferedForCasualtyOnOpponent() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 5, 6)));
	}

	@Test
	void offeredForCasualtyOnSpottedFoul() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.injuryState(new Foul(), 5, 5)));
	}

	@Test
	void offeredForCasualtyOnTeamMate() {
		fixture.sameTeam();
		assertTrue(modification.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 5, 6)));
	}

	@Test
	void notOfferedForCasualtyOnTeamMateDuringAnimalSavagery() {
		fixture.sameTeam();
		InjuryResolutionState state = fixture.injuryState(new Block(), 5, 6);
		state.getInjuryContext().setApothecaryMode(ApothecaryMode.ANIMAL_SAVAGERY);
		assertFalse(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void reRollsInjuryOnlyOnce() {
		when(fixture.diceRoller.rollInjury()).thenReturn(new int[]{1, 2});
		InjuryResolutionState state = fixture.injuryState(new Block(), 4, 5);

		modification.apply(fixture.gameState, state);

		assertArrayEquals(new int[]{1, 2}, state.getInjuryContext().getInjuryRoll());
		assertEquals(new PlayerState(PlayerState.STUNNED), state.getInjuryContext().getInjury());
		assertTrue(state.isInjuryReRolled());
		assertFalse(modification.isApplicable(fixture.gameState, state));
	}
}
