package com.fumbbl.ffb.server.step.mixed.pass;

import com.fumbbl.ffb.PlayerState;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepPassBlockTest {

	@Test
	void serializesMovedThrowerState() throws Exception {
		StepPassBlock step = new StepPassBlock(null);
		setHasMoved(step, true);
		PlayerState state = new PlayerState(PlayerState.MOVING).changeActive(true);
		setPlayerState(step, state);

		assertTrue(step.toJsonValue().get("hasMoved").asBoolean());
		assertEquals(state.getId(), step.toJsonValue().get("passBlockThrowerState").asInt());
	}

	@Test
	void serializesUnmovedThrowerState() throws Exception {
		StepPassBlock step = new StepPassBlock(null);
		setHasMoved(step, false);
		PlayerState state = new PlayerState(PlayerState.MOVING).changeActive(true);
		setPlayerState(step, state);

		assertFalse(step.toJsonValue().get("hasMoved").asBoolean());
		assertEquals(state.getId(), step.toJsonValue().get("passBlockThrowerState").asInt());
	}

	private void setHasMoved(StepPassBlock step, boolean value) throws Exception {
		Field field = StepPassBlock.class.getDeclaredField("hasMoved");
		field.setAccessible(true);
		field.set(step, value);
	}

	private void setPlayerState(StepPassBlock step, PlayerState state) throws Exception {
		Field field = StepPassBlock.class.getDeclaredField("fOldActingPlayerCurrentState");
		field.setAccessible(true);
		field.set(step, state);
	}
}
