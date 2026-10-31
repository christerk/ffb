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
	void mapsLegacySingleUsePropertiesToSingleUse() {
		assertEquals(ReRollProperty.SINGLE_USE, factory.forName("BRILLIANT_COACHING"));
		assertEquals(ReRollProperty.SINGLE_USE, factory.forName("PUMP_UP_THE_CROWD"));
		assertEquals(ReRollProperty.SINGLE_USE, factory.forName("SHOW_STAR"));
	}

	@Test
	void returnsNullForUnknownNames() {
		assertNull(factory.forName("UNKNOWN"));
	}
}
