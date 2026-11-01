package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.StabForSpp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ToxinConnoisseurChainModificationTest {

	private ChainModificationFixture fixture;
	private ToxinConnoisseurChainModification modification;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		modification = new ToxinConnoisseurChainModification(fixture.evaluationService, fixture.copyService);
		modification.setSkill(fixture.skill("Toxin Connoisseur", 0, 1));
	}

	@Test
	void offeredForStabsWhenItChangesTheInjury() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.injuryState(new Stab(), 4, 3)));
		assertTrue(modification.isApplicable(fixture.gameState, fixture.injuryState(new StabForSpp(), 4, 5)));
	}

	@Test
	void notOfferedWithoutChange() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.injuryState(new Stab(), 2, 3)));
	}

	@Test
	void notOfferedForCasualtyOrOtherTypes() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.injuryState(new Stab(), 5, 6)));
		assertFalse(modification.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 4, 3)));
	}

	@Test
	void notOfferedInArmourPhase() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Stab(), 2, 3)));
	}

	@Test
	void applyChangesInjury() {
		InjuryResolutionState state = fixture.injuryState(new Stab(), 4, 3);

		modification.apply(fixture.gameState, state);

		assertEquals(new PlayerState(PlayerState.KNOCKED_OUT), state.getInjuryContext().getInjury());
		assertFalse(state.isInjuryReRolled());
	}
}
