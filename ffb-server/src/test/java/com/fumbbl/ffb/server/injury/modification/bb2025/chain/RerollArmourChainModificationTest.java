package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.Stab;
import com.fumbbl.ffb.injury.context.InjuryContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class RerollArmourChainModificationTest {

	private ChainModificationFixture fixture;
	private KrumpAndSmashChainModification krumpAndSmash;
	private MasterAssassinChainModification masterAssassin;
	private DwarfenGritChainModification dwarfenGrit;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		krumpAndSmash = new KrumpAndSmashChainModification(fixture.evaluationService, fixture.copyService);
		krumpAndSmash.setSkill(fixture.skill("Krump and Smash", 0, 0));
		masterAssassin = new MasterAssassinChainModification(fixture.evaluationService, fixture.copyService);
		masterAssassin.setSkill(fixture.skill("Master Assassin", 0, 0));
		dwarfenGrit = new DwarfenGritChainModification(fixture.evaluationService, fixture.copyService);
		dwarfenGrit.setSkill(fixture.skill("Dwarfen Grit", 0, 0));
	}

	@Test
	void krumpAndSmashOnlyForBlocksWithArmourHolding() {
		assertTrue(krumpAndSmash.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
		assertFalse(krumpAndSmash.isApplicable(fixture.gameState, fixture.armourState(new Block(), 5, 6)));
		assertFalse(krumpAndSmash.isApplicable(fixture.gameState, fixture.armourState(new Stab(), 2, 3)));
	}

	@Test
	void masterAssassinOnlyForStabsWithArmourHolding() {
		assertTrue(masterAssassin.isApplicable(fixture.gameState, fixture.armourState(new Stab(), 2, 3)));
		assertFalse(masterAssassin.isApplicable(fixture.gameState, fixture.armourState(new Stab(), 5, 6)));
		assertFalse(masterAssassin.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void reRollKeepsSkillModifiersAndReEvaluates() {
		when(fixture.diceRoller.rollArmour()).thenReturn(new int[]{3, 5});
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.getInjuryContext().addArmorModifiers(fixture.skill("Modifier", 1, 0).getArmorModifiers());

		krumpAndSmash.apply(fixture.gameState, state);

		InjuryContext context = state.getInjuryContext();
		assertArrayEquals(new int[]{3, 5}, context.getArmorRoll());
		assertEquals(1, context.getArmorModifiers().length);
		assertTrue(context.isArmorBroken());
	}

	@Test
	void dwarfenGritOnlyWhenArmourIsBroken() {
		assertTrue(dwarfenGrit.isApplicable(fixture.gameState, fixture.armourState(new Block(), 5, 6)));
		assertTrue(dwarfenGrit.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 5, 6)));
		assertFalse(dwarfenGrit.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void dwarfenGritIsOwnedByDefenderAndUsableWithoutAttacker() {
		InjuryContext context = fixture.context(new Foul(), false);
		context.setArmorRoll(new int[]{5, 6});
		context.setArmorBroken(true);

		assertEquals(ChainModificationFixture.DEFENDER_ID, dwarfenGrit.ownerId(context));
		assertTrue(dwarfenGrit.isApplicable(fixture.gameState, new InjuryResolutionState(context, InjuryPhase.ARMOUR)));
	}

	@Test
	void dwarfenGritUsableAgainstTeamMate() {
		fixture.sameTeam();
		assertTrue(dwarfenGrit.isApplicable(fixture.gameState, fixture.armourState(new Block(), 5, 6)));
	}

	@Test
	void dwarfenGritNotOfferedAfterKrumpAndSmash() {
		when(fixture.diceRoller.rollArmour()).thenReturn(new int[]{5, 6});
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);

		krumpAndSmash.apply(fixture.gameState, state);

		assertTrue(state.getInjuryContext().isArmorBroken());
		assertFalse(dwarfenGrit.isApplicable(fixture.gameState, state));
	}
}
