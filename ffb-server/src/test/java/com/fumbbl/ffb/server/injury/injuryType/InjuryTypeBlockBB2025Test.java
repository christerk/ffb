package com.fumbbl.ffb.server.injury.injuryType;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InjuryTypeBlockBB2025Test {

	@Test
	void useArmourModifiersOnlyModeShouldExist() {
		// Test that the new BB2025 mode exists in the enum
		InjuryTypeBlock.Mode mode = InjuryTypeBlock.Mode.USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES;
		assertNotNull(mode);
		assertEquals("USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES", mode.name());
	}

	@Test
	void canCreateInjuryTypeBlockWithUseArmourModifiersOnlyMode() {
		// Test that we can create an InjuryTypeBlock instance with the new mode
		InjuryTypeBlock injuryTypeBlock = new InjuryTypeBlock(InjuryTypeBlock.Mode.USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES);
		assertNotNull(injuryTypeBlock);
	}

	@Test
	void allModesShouldBePresent() {
		// Verify all expected modes exist
		InjuryTypeBlock.Mode[] expectedModes = {
			InjuryTypeBlock.Mode.REGULAR,
			InjuryTypeBlock.Mode.USE_MODIFIERS_AGAINST_TEAM_MATES,
			InjuryTypeBlock.Mode.DO_NOT_USE_MODIFIERS,
			InjuryTypeBlock.Mode.USE_ARMOUR_MODIFIERS_ONLY_AGAINST_TEAM_MATES
		};

		for (InjuryTypeBlock.Mode expectedMode : expectedModes) {
			assertNotNull(expectedMode);
		}

		assertEquals(4, InjuryTypeBlock.Mode.values().length);
	}
}

