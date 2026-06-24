package com.fumbbl.ffb.server.step.mixed.pass;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.FactoryType;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.factory.IFactorySource;
import com.fumbbl.ffb.factory.INamedObjectFactory;
import com.fumbbl.ffb.factory.ReportFactory;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StepPassBlockTest {

	@Test
	void serializesMovedThrowerState() {
		PlayerState state = new PlayerState(PlayerState.MOVING).changeActive(true);
		StepPassBlock step = stepFromJson(true, state);

		assertTrue(step.toJsonValue().get("hasMoved").asBoolean());
		assertEquals(state.getId(), step.toJsonValue().get("passBlockThrowerState").asInt());
	}

	@Test
	void serializesUnmovedThrowerState() {
		PlayerState state = new PlayerState(PlayerState.MOVING).changeActive(true);
		StepPassBlock step = stepFromJson(false, state);

		assertFalse(step.toJsonValue().get("hasMoved").asBoolean());
		assertEquals(state.getId(), step.toJsonValue().get("passBlockThrowerState").asInt());
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private StepPassBlock stepFromJson(boolean hasMoved, PlayerState state) {
		IFactorySource source = mock(IFactorySource.class);
		INamedObjectFactory stepIdFactory = mock(INamedObjectFactory.class);
		INamedObjectFactory stepActionFactory = mock(INamedObjectFactory.class);
		ReportFactory reportFactory = mock(ReportFactory.class);
		when(source.forContext(any())).thenReturn(source);
		when(source.getFactory(FactoryType.Factory.STEP_ID)).thenReturn(stepIdFactory);
		when(source.getFactory(FactoryType.Factory.STEP_ACTION)).thenReturn(stepActionFactory);
		when(source.getFactory(FactoryType.Factory.REPORT)).thenReturn(reportFactory);
		when(stepIdFactory.forName(StepId.PASS_BLOCK.getName())).thenReturn(StepId.PASS_BLOCK);
		when(stepActionFactory.forName(StepAction.CONTINUE.getName())).thenReturn(StepAction.CONTINUE);

		JsonObject stepResult = new JsonObject()
			.add("nextAction", StepAction.CONTINUE.getName())
			.add("reportList", new JsonObject().add("reports", new JsonArray()))
			.add("synchronize", true);
		JsonObject jsonObject = new JsonObject()
			.add("stepId", StepId.PASS_BLOCK.getName())
			.add("stepResult", stepResult)
			.add("goingForIt", false)
			.add("hasMoved", hasMoved)
			.add("passBlockThrowerState", state.getId());
		return new StepPassBlock(null).initFrom(source, jsonObject);
	}
}
