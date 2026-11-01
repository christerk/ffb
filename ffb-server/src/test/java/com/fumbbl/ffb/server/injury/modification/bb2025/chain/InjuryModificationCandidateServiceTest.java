package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Chainsaw;
import com.fumbbl.ffb.injury.context.InjuryContext;
import com.fumbbl.ffb.model.Player;
import com.fumbbl.ffb.model.skill.Skill;
import com.fumbbl.ffb.model.skill.SkillUsageType;
import com.fumbbl.ffb.server.model.SkillBehaviour;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InjuryModificationCandidateServiceTest {

	private ChainModificationFixture fixture;
	private InjuryModificationCandidateService service;
	private final Set<Skill> attackerSkills = new HashSet<>();
	private final Set<Skill> defenderSkills = new HashSet<>();

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		service = new InjuryModificationCandidateService();
		when(fixture.attacker.getSkillsIncludingTemporaryOnes()).thenReturn(attackerSkills);
		when(fixture.defender.getSkillsIncludingTemporaryOnes()).thenReturn(defenderSkills);
	}

	@Test
	void attackerAskedWhileArmourHolds() {
		ChainableInjuryModification attackerMod = modification(fixture.attacker, attackerSkills, "Krump", ChainModificationFixture.ATTACKER_ID, true);
		modification(fixture.defender, defenderSkills, "Grit", ChainModificationFixture.DEFENDER_ID, true);

		assertEquals(Arrays.asList(attackerMod), modifications(service.candidates(fixture.gameState, fixture.armourState(new Block(), 2, 3))));
	}

	@Test
	void defenderAskedWhenArmourIsBroken() {
		modification(fixture.attacker, attackerSkills, "Krump", ChainModificationFixture.ATTACKER_ID, true);
		ChainableInjuryModification defenderMod = modification(fixture.defender, defenderSkills, "Grit", ChainModificationFixture.DEFENDER_ID, true);

		assertEquals(Arrays.asList(defenderMod), modifications(service.candidates(fixture.gameState, fixture.armourState(new Block(), 5, 6))));
	}

	@Test
	void attackerAskedOnBrokenArmourOnlyForHookedModificationsIfDefenderHasNothing() {
		modification(fixture.attacker, attackerSkills, "Krump", ChainModificationFixture.ATTACKER_ID, true);
		ChainableInjuryModification crushingBlow = modification(fixture.attacker, attackerSkills, "Crushing Blow", ChainModificationFixture.ATTACKER_ID, true);
		when(crushingBlow.offeredOnBrokenArmour()).thenReturn(true);

		assertEquals(Arrays.asList(crushingBlow), modifications(service.candidates(fixture.gameState, fixture.armourState(new Block(), 5, 6))));
	}

	@Test
	void attackerAskedOnBrokenArmourAfterDefenderDeclined() {
		modification(fixture.defender, defenderSkills, "Grit", ChainModificationFixture.DEFENDER_ID, true);
		ChainableInjuryModification crushingBlow = modification(fixture.attacker, attackerSkills, "Crushing Blow", ChainModificationFixture.ATTACKER_ID, true);
		when(crushingBlow.offeredOnBrokenArmour()).thenReturn(true);
		InjuryResolutionState state = fixture.armourState(new Block(), 5, 6);

		state.decline(ChainModificationFixture.DEFENDER_ID);

		assertEquals(Arrays.asList(crushingBlow), modifications(service.candidates(fixture.gameState, state)));
	}

	@Test
	void declineEndsTheSidesParticipationUntilPhaseChanges() {
		modification(fixture.attacker, attackerSkills, "Krump", ChainModificationFixture.ATTACKER_ID, true);
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.decline(ChainModificationFixture.ATTACKER_ID);

		assertTrue(service.candidates(fixture.gameState, state).isEmpty());

		state.setPhase(InjuryPhase.INJURY);
		state.setPhase(InjuryPhase.ARMOUR);
		assertEquals(1, service.candidates(fixture.gameState, state).size());
	}

	@Test
	void nobodyAskedWithoutAttackerWhileArmourHolds() {
		modification(fixture.defender, defenderSkills, "Grit", ChainModificationFixture.DEFENDER_ID, true);
		InjuryContext context = fixture.context(new Block(), false);
		context.setArmorRoll(new int[]{2, 3});

		assertTrue(service.candidates(fixture.gameState, new InjuryResolutionState(context, InjuryPhase.ARMOUR)).isEmpty());
	}

	@Test
	void defenderTakesAttackerRoleForOwnChainsaw() {
		ChainableInjuryModification oldPro = modification(fixture.defender, defenderSkills, "Old Pro", ChainModificationFixture.DEFENDER_ID, true);
		InjuryContext context = fixture.context(new Chainsaw(), false);
		context.setArmorRoll(new int[]{2, 3});

		assertEquals(Arrays.asList(oldPro), modifications(service.candidates(fixture.gameState, new InjuryResolutionState(context, InjuryPhase.ARMOUR))));
	}

	@Test
	void injuryPhaseAsksAttackerOrDefenderWithoutAttacker() {
		ChainableInjuryModification attackerMod = modification(fixture.attacker, attackerSkills, "Mauling", ChainModificationFixture.ATTACKER_ID, true);
		ChainableInjuryModification defenderMod = modification(fixture.defender, defenderSkills, "Other", ChainModificationFixture.DEFENDER_ID, true);

		assertEquals(Arrays.asList(attackerMod), modifications(service.candidates(fixture.gameState, fixture.injuryState(new Block(), 2, 3))));

		InjuryContext context = fixture.context(new Block(), false);
		context.setInjuryRoll(new int[]{2, 3});
		assertEquals(Arrays.asList(defenderMod), modifications(service.candidates(fixture.gameState, new InjuryResolutionState(context, InjuryPhase.INJURY))));
	}

	@Test
	void filtersNotApplicableUsedAndForeignModifications() {
		modification(fixture.attacker, attackerSkills, "NotApplicable", ChainModificationFixture.ATTACKER_ID, false);
		ChainableInjuryModification used = modification(fixture.attacker, attackerSkills, "Used", ChainModificationFixture.ATTACKER_ID, true);
		when(fixture.attacker.isUsed(used.getSkill())).thenReturn(true);
		modification(fixture.attacker, attackerSkills, "OwnedByDefender", ChainModificationFixture.DEFENDER_ID, true);
		ChainableInjuryModification valid = modification(fixture.attacker, attackerSkills, "Valid", ChainModificationFixture.ATTACKER_ID, true);

		assertEquals(Arrays.asList(valid), modifications(service.candidates(fixture.gameState, fixture.armourState(new Block(), 2, 3))));
	}

	@Test
	void candidatesAreSorted() {
		ChainableInjuryModification oncePerGame = modification(fixture.attacker, attackerSkills, "A", ChainModificationFixture.ATTACKER_ID, true);
		ChainableInjuryModification oncePerTurn = modification(fixture.attacker, attackerSkills, "B", ChainModificationFixture.ATTACKER_ID, true);
		when(oncePerTurn.getSkill().getSkillUsageType()).thenReturn(SkillUsageType.ONCE_PER_TURN);

		assertEquals(Arrays.asList(oncePerTurn, oncePerGame), modifications(service.candidates(fixture.gameState, fixture.armourState(new Block(), 2, 3))));
	}

	@Test
	void doneHasNoCandidates() {
		modification(fixture.attacker, attackerSkills, "Krump", ChainModificationFixture.ATTACKER_ID, true);
		InjuryResolutionState state = fixture.armourState(new Block(), 2, 3);
		state.setPhase(InjuryPhase.DONE);

		assertTrue(service.candidates(fixture.gameState, state).isEmpty());
	}

	private ChainableInjuryModification modification(Player<?> player, Set<Skill> skills, String name, String ownerId,
		boolean applicable) {
		Skill skill = mock(Skill.class);
		when(skill.getName()).thenReturn(name);
		when(skill.getSkillUsageType()).thenReturn(SkillUsageType.ONCE_PER_GAME);
		ChainableInjuryModification modification = mock(ChainableInjuryModification.class);
		when(modification.getSkill()).thenReturn(skill);
		when(modification.restriction()).thenReturn(new NoRestriction());
		when(modification.ownerId(any())).thenReturn(ownerId);
		when(modification.isApplicable(any(), any())).thenReturn(applicable);
		SkillBehaviour<?> behaviour = mock(SkillBehaviour.class);
		List<ChainableInjuryModification> modifications = new ArrayList<>();
		modifications.add(modification);
		when(behaviour.getChainableInjuryModifications()).thenReturn(modifications);
		doReturn(behaviour).when(skill).getSkillBehaviour();
		skills.add(skill);
		return modification;
	}

	private List<ChainableInjuryModification> modifications(List<InjuryModificationCandidate> candidates) {
		return candidates.stream().map(InjuryModificationCandidate::getModification).collect(Collectors.toList());
	}
}
