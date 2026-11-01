package com.fumbbl.ffb.server.injury.modification.bb2025.chain;

import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.SkillUse;
import com.fumbbl.ffb.injury.Block;
import com.fumbbl.ffb.injury.Foul;
import com.fumbbl.ffb.model.Keyword;
import com.fumbbl.ffb.model.skill.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvOrInjChainModificationTest {

	private ChainModificationFixture fixture;
	private AvOrInjChainModification ram;
	private AvOrInjChainModification slayer;
	private Skill ramSkill;

	@BeforeEach
	void setUp() {
		fixture = new ChainModificationFixture();
		ramSkill = fixture.skill("Ram", 1, 1);
		ram = new AvOrInjChainModification(new NoRestriction(), fixture.evaluationService, fixture.copyService);
		ram.setSkill(ramSkill);
		slayer = new AvOrInjChainModification(new KeywordRestriction(Keyword.BIG_GUY), fixture.evaluationService,
			fixture.copyService);
		slayer.setSkill(fixture.skill("Slayer", 1, 1));
	}

	@Test
	void offeredForArmourWhenItBreaksIt() {
		assertTrue(ram.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 4)));
		assertFalse(ram.isApplicable(fixture.gameState, fixture.armourState(new Block(), 2, 3)));
		assertFalse(ram.isApplicable(fixture.gameState, fixture.armourState(new Block(), 5, 6)));
		assertFalse(ram.isApplicable(fixture.gameState, fixture.armourState(new Foul(), 4, 4)));
	}

	@Test
	void notOfferedWithoutTacklezones() {
		fixture.attackerHasTacklezones(false);
		assertFalse(ram.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 4)));
		assertFalse(ram.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 4, 3)));
	}

	@Test
	void offeredForInjuryWhenItChangesTheResult() {
		assertTrue(ram.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 4, 3)));
		assertFalse(ram.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 2, 3)));
	}

	@Test
	void notOfferedForCasualty() {
		assertFalse(ram.isApplicable(fixture.gameState, fixture.injuryState(new Block(), 5, 6)));
	}

	@Test
	void notOfferedForInjuryWhenUsedOnArmour() {
		InjuryResolutionState state = fixture.injuryState(new Block(), 4, 3);
		state.getInjuryContext().addArmorModifiers(ramSkill.getArmorModifiers());

		assertFalse(ram.isApplicable(fixture.gameState, state));
	}

	@Test
	void notOfferedForInjuryAfterBeingAppliedToArmour() {
		InjuryResolutionState state = fixture.armourState(new Block(), 4, 4);
		ram.apply(fixture.gameState, state);
		state.getInjuryContext().setInjuryRoll(new int[]{4, 3});
		state.getInjuryContext().setInjury(new PlayerState(PlayerState.STUNNED));
		state.setPhase(InjuryPhase.INJURY);

		assertFalse(ram.isApplicable(fixture.gameState, state));
	}

	@Test
	void applyToInjury() {
		InjuryResolutionState state = fixture.injuryState(new Block(), 4, 5);

		AppliedInjuryModification applied = ram.apply(fixture.gameState, state);

		assertNull(state.getInjuryContext().getInjury());
		assertEquals(SkillUse.ADD_INJURY_MODIFIER, applied.getSkillUse());
		assertEquals(1, state.getSkillInjuryModifiers().size());
	}

	@Test
	void applyToArmour() {
		InjuryResolutionState state = fixture.armourState(new Block(), 4, 4);

		AppliedInjuryModification applied = ram.apply(fixture.gameState, state);

		assertTrue(state.getInjuryContext().isArmorBroken());
		assertEquals(SkillUse.ADD_ARMOUR_MODIFIER, applied.getSkillUse());
	}

	@Test
	void slayerOnlyAgainstBigGuys() {
		assertFalse(slayer.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 4)));
		fixture.defenderKeywords.add(Keyword.BIG_GUY);
		assertTrue(slayer.isApplicable(fixture.gameState, fixture.armourState(new Block(), 4, 4)));
		assertEquals(RestrictionType.KEYWORD, slayer.restriction().type());
		assertEquals(RestrictionType.NONE, ram.restriction().type());
	}
}
