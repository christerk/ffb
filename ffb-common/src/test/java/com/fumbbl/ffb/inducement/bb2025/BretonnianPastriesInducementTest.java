package com.fumbbl.ffb.inducement.bb2025;

import com.fumbbl.ffb.IIconProperty;
import com.fumbbl.ffb.inducement.InducementType;
import com.fumbbl.ffb.inducement.Usage;
import com.fumbbl.ffb.model.GameOptions;
import com.fumbbl.ffb.model.Team;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BretonnianPastriesInducementTest {

	private InducementType pastries;
	private Team team;
	private GameOptions options;

	@BeforeEach
	void setUp() {
		pastries = new InducementCollection().getTypes().stream()
			.filter(type -> "bretonnianPastries".equals(type.getName()))
			.findFirst()
			.orElseThrow(() -> new AssertionError("bretonnianPastries missing from BB2025 inducements"));
		team = new Team(null);
		options = new GameOptions(null);
	}

	@Test
	void pastriesAreAvailableToAnyTeamOncePerGameByDefault() {
		assertEquals(1, pastries.availability(team, options));
	}

	@Test
	void availabilityFollowsMaxOption() {
		options.addOption(new GameOptionInt(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_MAX).setValue(0));
		assertEquals(0, pastries.availability(team, options));
	}

	@Test
	void costIsReadFromCostOption() {
		assertEquals(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_COST, pastries.getActualCostId(team));
		assertEquals(GameOptionId.INDUCEMENT_BRETONNIAN_PASTRIES_MAX, pastries.getActualMaxId(team));
		assertEquals(15000, ((GameOptionInt) options.getOptionWithDefault(pastries.getActualCostId(team))).getValue());
	}

	@Test
	void pastriesHaveKnockoutReserveUsageAndOwnSlot() {
		assertTrue(pastries.hasSingleUsage(Usage.KNOCKOUT_RESERVE));
		assertTrue(pastries.isUsingGenericSlot());
		assertEquals(IIconProperty.RESOURCE_PASTRIES, pastries.getSlotIconProperty());
		assertEquals("Bretonnian Pastries", pastries.getDescription());
	}

	@Test
	void knockoutReserveIsCountedAndShownInResults() {
		assertFalse(Usage.EXCLUDE_FROM_COUNT.contains(Usage.KNOCKOUT_RESERVE));
		assertFalse(Usage.EXCLUDE_FROM_RESULT.contains(Usage.KNOCKOUT_RESERVE));
	}

	@Test
	void pastriesAreOnlyPartOfTheBb2025Collection() {
		assertFalse(new com.fumbbl.ffb.inducement.bb2020.InducementCollection().getTypes().stream()
			.anyMatch(type -> type.hasUsage(Usage.KNOCKOUT_RESERVE)));
	}
}
