package com.fumbbl.ffb.server.step.mixed.pass;

import com.fumbbl.ffb.PlayerState;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepPassBlockTest {

	@Test
	void serializesMovedThrowerState() {
		PlayerState state = new PlayerState(PlayerState.MOVING).changeActive(true);
		StepPassBlock step = new StepPassBlock(null, true, state);

		assertTrue(step.toJsonValue().get("hasMoved").asBoolean());
		assertEquals(state.getId(), step.toJsonValue().get("passBlockThrowerState").asInt());
	}

	@Test
	void serializesUnmovedThrowerState() {
		PlayerState state = new PlayerState(PlayerState.MOVING).changeActive(true);
		StepPassBlock step = new StepPassBlock(null, false, state);

		assertFalse(step.toJsonValue().get("hasMoved").asBoolean());
		assertEquals(state.getId(), step.toJsonValue().get("passBlockThrowerState").asInt());
	}
}
