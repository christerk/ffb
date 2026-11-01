package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.property.NamedProperties;
import com.fumbbl.ffb.model.skill.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChainableInjuryModificationTest {

	private ChainModificationFixture fixture;
	private KrumpAndSmashChainModification modification;
	private Skill skill;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		skill = fixture.skill("Krump and Smash", 0, 0);
		modification = new KrumpAndSmashChainModification(fixture.evaluationService, fixture.copyService);
		modification.setSkill(skill);
	}

	@Test
	void applicableInMatchingPhaseAndType() {
		assertTrue(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void notApplicableForOtherInjuryType() {
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 2, 3)));
	}

	@Test
	void notApplicableInOtherPhase() {
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.setPhase(InjuryPhase.INJURY);
		assertFalse(modification.isApplicable(fixture.gameState, state));
		state.setPhase(InjuryPhase.DONE);
		assertFalse(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void notApplicableWithoutArmourRoll() {
		InjuryContext context = fixture.context(new Block(), true);
		assertFalse(modification.isApplicable(fixture.gameState, new InjuryResolutionState(context, InjuryPhase.ARMOUR)));
	}

	@Test
	void reRollNotApplicableOnceTheRollWasReRolled() {
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.addAppliedModification(new AppliedInjuryModification(fixture.skill("Other", 0, 0),
			ChainModificationFixture.DEFENDER_ID, InjuryPhase.ARMOUR, ModificationEffect.RE_ROLL, SkillUse.RE_ROLL_ARMOUR,
			Collections.emptyList()));

		assertFalse(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void injuryReRollDoesNotBlockArmourReRoll() {
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.addAppliedModification(new AppliedInjuryModification(fixture.skill("Other", 0, 0),
			ChainModificationFixture.DEFENDER_ID, InjuryPhase.INJURY, ModificationEffect.RE_ROLL, SkillUse.RE_ROLL_INJURY,
			Collections.emptyList()));

		assertTrue(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void notApplicableAgainstTeamMate() {
		fixture.sameTeam();
		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void notApplicableWhenDefenderIgnoresArmourModifiersFromSkills() {
		Skill ironHardSkin = mock(Skill.class);
		when(ironHardSkin.hasSkillProperty(NamedProperties.ignoresArmourModifiersFromSkills)).thenReturn(true);
		when(fixture.defender.getSkillsIncludingTemporaryOnes()).thenReturn(Collections.singleton(ironHardSkin));

		assertFalse(modification.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
	}

	@Test
	void ownerIsAttacker() {
		assertEquals(ChainModificationFixture.ATTACKER_ID, modification.ownerId(fixture.context(new Block(), true)));
	}

	@Test
	void noOwnerWithoutAttacker() {
		assertNull(modification.ownerId(fixture.context(new Block(), false)));
	}

	@Test
	void defenderOwnsSelfInflictedChainsawModifications() {
		assertEquals(ChainModificationFixture.DEFENDER_ID, modification.ownerId(fixture.context(new Chainsaw(), false)));
	}

	@Test
	void applyRecordsModificationAndReRoll() {
		when(fixture.diceRoller.rollArmour()).thenReturn(new int[]{5, 6});
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);

		AppliedInjuryModification applied = modification.apply(fixture.gameState, state);

		assertSame(skill, applied.getSkill());
		assertEquals(ChainModificationFixture.ATTACKER_ID, applied.getPlayerId());
		assertEquals(InjuryPhase.ARMOUR, applied.getPhase());
		assertEquals(ModificationEffect.RE_ROLL, applied.getEffect());
		assertEquals(SkillUse.RE_ROLL_ARMOUR, applied.getSkillUse());
		assertTrue(state.isArmourReRolled());
		assertFalse(state.isInjuryReRolled());
		assertTrue(state.isApplied(skill, ChainModificationFixture.ATTACKER_ID));
		assertTrue(state.getInjuryContext().isArmorBroken());
		assertFalse(modification.isApplicable(fixture.gameState, state));
	}

	@Test
	void applyInDonePhaseFails() {
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.setPhase(InjuryPhase.DONE);
		assertThrows(IllegalStateException.class, () -> modification.apply(fixture.gameState, state));
	}
}
