package com.fumbbl.ffb.server.step.mixed.pass;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StepPassBlockTest {

	@Test
	void serializesMovedThrowerState() throws Exception {
		StepPassBlock step = new StepPassBlock(null);
		setHasMoved(step, true);

		assertTrue(step.toJsonValue().get("hasMoved").asBoolean());
	}

	@Test
	void serializesUnmovedThrowerState() throws Exception {
		StepPassBlock step = new StepPassBlock(null);
		setHasMoved(step, false);

		assertFalse(step.toJsonValue().get("hasMoved").asBoolean());
	}

	private void setHasMoved(StepPassBlock step, boolean value) throws Exception {
		Field field = StepPassBlock.class.getDeclaredField("hasMoved");
		field.setAccessible(true);
		field.set(step, value);
	}
}
