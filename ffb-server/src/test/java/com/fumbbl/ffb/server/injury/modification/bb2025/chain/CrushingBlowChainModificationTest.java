package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.modifiers.ArmorModifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CrushingBlowChainModificationTest {

	private ChainModificationFixture fixture;
	private CrushingBlowChainModification modification;
	private Skill skill;
	private ArmorModifier mightyBlow;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		skill = fixture.skill("Crushing Blow", 1, 0);
		modification = new CrushingBlowChainModification(fixture.evaluationService, fixture.copyService);
		modification.setSkill(skill);
		mightyBlow = mock(ArmorModifier.class);
		when(mightyBlow.isRegisteredToSkillWithProperty(NamedProperties.affectsEitherArmourOrInjuryOnBlock)).thenReturn(true);
	}

	@Test
	void offeredWhenItBreaksTheArmour() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 4)));
	}

	@Test
	void notOfferedWhenItCanNotBreakTheArmour() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void notOfferedWithoutTacklezones() {
		fixture.attackerHasTacklezones(false);
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 4)));
	}

	@Test
	void notOfferedWhenArmourIsBrokenWithoutMightyBlow() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 5, 6)));
	}

	@Test
	void offeredWhenMightyBlowBrokeTheArmourAndCrushingBlowAloneBreaksIt() {
		assertTrue(modification.isApplicable(fixture.gameState, mightyBlowState(4, 4)));
		assertTrue(modification.offeredOnBrokenArmour());
	}

	@Test
	void notOfferedWhenMightyBlowBrokeTheArmourButCrushingBlowAloneDoesNot() {
		InjuryResolutionState state = mightyBlowState(4, 4);
		state.getInjuryContext().addArmorModifiers(fixture.skill("Other", 1, 0).getArmorModifiers());
		fixture.armour = 9;
		// 8 + other + crushing blow = 10 still breaks armour 9
		assertTrue(modification.isApplicable(fixture.gameState, state));

		fixture.armour = 10;
		assertFalse(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void applyReplacesMightyBlowWithCrushingBlow() {
		InjuryResolutionState state = mightyBlowState(4, 4);

		modification.apply(fixture.gameState, state);

		InjuryContext context = state.getInjuryContext();
		assertEquals(Collections.singletonList(skill.getArmorModifiers().get(0)), Arrays.asList(context.getArmorModifiers()));
		assertTrue(context.isArmorBroken());
		assertEquals(1, state.getSkillArmourModifiers().size());
	}

	@Test
	void applyAddsCrushingBlowWhenArmourHolds() {
		InjuryResolutionState state = fixture.armourState(new Block(), 4, 4);

		modification.apply(fixture.gameState, state);

		assertTrue(state.getInjuryContext().isArmorBroken());
		assertFalse(state.isArmourReRolled());
	}

	private InjuryResolutionState mightyBlowState(int... armourRoll) {
		InjuryResolutionState state = fixture.armourState(new Block(), armourRoll);
		state.getInjuryContext().addArmorModifier(mightyBlow);
		state.getInjuryContext().setArmorBroken(true);
		return state;
	}
}
