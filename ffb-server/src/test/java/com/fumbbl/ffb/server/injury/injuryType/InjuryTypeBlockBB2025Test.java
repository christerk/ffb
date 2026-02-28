package com.fumbbl.ffb.server.injury.injuryType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InjuryTypeBlockBB2025Test {

	@Test
	void useArmourModifiersOnlyModeShouldExist() {
		// Test that the new BB2025 mode exists in the enum
		BlockInjuryEvaluator.Mode mode = BlockInjuryEvaluator.Mode.USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES;
		assertNotNull(mode);
		assertEquals("USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES", mode.name());
	}

	@Test
	void canCreateInjuryTypeBlockWithUseArmourModifiersOnlyMode() {
		// Test that we can create an InjuryTypeBlock instance with the new mode
		InjuryTypeBlock injuryTypeBlock = new InjuryTypeBlock(BlockInjuryEvaluator.Mode.USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES);
		assertNotNull(injuryTypeBlock);
	}

	@Test
	void allModesShouldBePresent() {
		// Verify all expected modes exist
		BlockInjuryEvaluator.Mode[] expectedModes = {
			BlockInjuryEvaluator.Mode.REGULAR,
			BlockInjuryEvaluator.Mode.USE_MODIFIERS_AGAINST_TEAM_MATES,
			BlockInjuryEvaluator.Mode.DO_NOT_USE_MODIFIERS,
			BlockInjuryEvaluator.Mode.USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES
		};

		for (BlockInjuryEvaluator.Mode expectedMode : expectedModes) {
			assertNotNull(expectedMode);
		}

		assertEquals(4, BlockInjuryEvaluator.Mode.values().length);
	}
}

