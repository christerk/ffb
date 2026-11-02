package com.fumbbl.ffb.server.mechanic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyRollMechanicReRollPropertiesTest {

	@Test
	void bb2016DoesNotExposeExplicitTeamReRollProperties() {
		assertTrue(new com.fumbbl.ffb.server.mechanic.bb2016.RollMechanic()
			.findTeamReRollProperties(null, null).isEmpty());
	}

	@Test
	void bb2020DoesNotExposeExplicitTeamReRollProperties() {
		assertTrue(new com.fumbbl.ffb.server.mechanic.bb2020.RollMechanic()
			.findTeamReRollProperties(null, null).isEmpty());
	}
}
