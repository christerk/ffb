package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.report.mixed.ReportOldPro;
import com.fumbbl.ffb.server.util.UtilServerReRoll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OldProChainModificationTest {

	private ChainModificationFixture fixture;
	private OldProChainModification modification;
	private MockedStatic<UtilServerReRoll> reRoll;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		modification = new OldProChainModification(fixture.evaluationService, fixture.copyService);
		modification.setSkill(fixture.skill("Old Pro", 0, 0));
		reRoll = mockStatic(UtilServerReRoll.class);
		reRoll.when(() -> UtilServerReRoll.isProReRollAvailable(any(), any(), any())).thenReturn(true);
	}

	@AfterEach
	void tearDown() {
		reRoll.close();
	}

	@Test
	void requiresProReRoll() {
		assertTrue(modification.requiresConditionalReRollSkill());
		reRoll.when(() -> UtilServerReRoll.isProReRollAvailable(any(), any(), any())).thenReturn(false);
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 4)));
	}

	@Test
	void offeredWhenASixWouldBreakTheArmour() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 4)));
	}

	@Test
	void notOfferedWhenASixCanNotBreakTheArmour() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 1, 2)));
	}

	@Test
	void notOfferedWhenArmourIsAlreadyBroken() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 6)));
	}

	@Test
	void offeredOnSpottedFoulWithBrokenArmour() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 5, 5)));
	}

	@Test
	void offeredToKeepTeamMatesArmourIntact() {
		fixture.sameTeam();
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 6)));
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 4)));
	}

	@Test
	void selfInflictedChainsawIsOwnedByDefender() {
		InjuryContext context = fixture.context(new Chainsaw(), false);
		context.setArmorRoll(new int[]{4, 6});
		context.setArmorBroken(true);
		InjuryResolutionState state = new InjuryResolutionState(context, InjuryPhase.ARMOUR);

		assertEquals(ChainModificationFixture.DEFENDER_ID, modification.ownerId(context));
		assertTrue(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void checkDoesNotRollDice() {
		modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 4));
		verify(fixture.diceRoller, never()).rollDice(anyInt());
	}

	@Test
	void applyReplacesLowerDieAndReports() {
		when(fixture.diceRoller.rollDice(6)).thenReturn(5);
		InjuryResolutionState state = fixture.armourState(new Block(), 4, 2);
		int[] originalRoll = state.getInjuryContext().getArmorRoll();

		AppliedInjuryModification applied = modification.apply(fixture.gameState, state);

		assertArrayEquals(new int[]{4, 5}, state.getInjuryContext().getArmorRoll());
		assertArrayEquals(new int[]{4, 2}, originalRoll);
		assertTrue(state.getInjuryContext().isArmorBroken());
		assertTrue(state.isArmourReRolled());
		assertEquals(1, applied.getReports().size());
		assertTrue(applied.getReports().get(0) instanceof ReportOldPro);
	}

	@Test
	void applyReplacesHigherDieForSelfInflicted() {
		fixture.sameTeam();
		when(fixture.diceRoller.rollDice(6)).thenReturn(1);
		InjuryResolutionState state = fixture.armourState(new Block(), 4, 6);

		modification.apply(fixture.gameState, state);

		assertArrayEquals(new int[]{4, 1}, state.getInjuryContext().getArmorRoll());
		assertFalse(state.getInjuryContext().isArmorBroken());
	}
}
