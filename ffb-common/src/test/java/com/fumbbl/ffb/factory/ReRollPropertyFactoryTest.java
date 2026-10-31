package com.fumbbl.ffb.factory;

import com.fumbbl.ffb.ReRollProperty;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ReRollPropertyFactoryTest {

	private final ReRollPropertyFactory factory = new ReRollPropertyFactory();

	@Test
	void resolvesCurrentProperties() {
		for (ReRollProperty property : ReRollProperty.values()) {
			assertEquals(property, factory.forName(property.getName()));
		}
	}

	@Test
	void mapsLegacyDriveBonusPropertiesToDriveBonus() {
		assertEquals(ReRollProperty.DRIVE_BONUS, factory.forName("BRILLIANT_COACHING"));
		assertEquals(ReRollProperty.DRIVE_BONUS, factory.forName("PUMP_UP_THE_CROWD"));
		assertEquals(ReRollProperty.DRIVE_BONUS, factory.forName("SHOW_STAR"));
	}

	@Test
	void returnsNullForUnknownNames() {
		assertNull(factory.forName("UNKNOWN"));
	}
}
