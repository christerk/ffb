package com.fumbbl.ffb.test.start;

import com.eclipsesource.json.JsonObject;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.dialog.DialogStartGameParameter;
import com.fumbbl.ffb.server.GameCache;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepAction;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.server.step.generator.Sequence;
import com.fumbbl.ffb.test.GameStateBuilder;
import com.fumbbl.ffb.test.StepEngine;
import com.fumbbl.ffb.test.TestServer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies that a game restored from the pre-match checkpoint continues with the following step instead of
 * being stuck on the already finished checkpoint step.
 */
public class PreMatchCheckpointRestoreTest {

	private TestServer testServer;
	private final List<JsonObject> snapshots = new ArrayList<>();

	@BeforeEach
	public void setUp() throws Exception {
		testServer = new TestServer();
		testServer.getServer().setGameCache(new GameCache(testServer.getServer()) {
			@Override
			public void queueDbUpdate(GameState gameState, boolean withSerialization) {
				if (withSerialization) {
					snapshots.add(gameState.toJsonValue());
				}
			}
		});
	}

	@Test
	public void restoredCheckpointContinuesWithNextStep() {
		GameState gameState = gameStateAtCheckpoint();

		StepEngine.start(gameState);

		assertEquals(StepId.COIN_CHOICE, gameState.getCurrentStep().getId());
		assertEquals(1, snapshots.size());

		GameState restored = restore(snapshots.get(0));

		assertEquals(StepId.CHECKPOINT, restored.getCurrentStep().getId());
		assertEquals(StepAction.NEXT_STEP, restored.getCurrentStep().getResult().getNextAction());
		assertNull(restored.getGame().getDialogParameter());

		assertTrue(restored.resumeFinishedStep());

		assertEquals(StepId.COIN_CHOICE, restored.getCurrentStep().getId());
		assertEquals(DialogId.COIN_CHOICE, restored.getGame().getDialogParameter().getId());
	}

	@Test
	public void restoredWaitingStepIsNotSkipped() {
		GameState gameState = gameStateAtCheckpoint();
		StepEngine.start(gameState);

		GameState restored = restore(gameState.toJsonValue());

		assertFalse(restored.resumeFinishedStep());
		assertEquals(StepId.COIN_CHOICE, restored.getCurrentStep().getId());
	}

	private GameState gameStateAtCheckpoint() {
		GameState gameState = new GameStateBuilder(testServer.getGameState())
			.withRule("BB2025")
			.withTeam(true, team -> team.player("home1", player -> player.at(10, 7)))
			.withTeam(false, team -> team.player("away1", player -> player.at(15, 7)))
			.build();
		gameState.getStepStack().clear();
		gameState.getGame().setDialogParameter(new DialogStartGameParameter());
		gameState.fetchChanges();

		Sequence sequence = new Sequence(gameState);
		sequence.add(StepId.CHECKPOINT);
		sequence.add(StepId.COIN_CHOICE);
		gameState.getStepStack().push(sequence.getSequence());
		return gameState;
	}

	private GameState restore(JsonObject snapshot) {
		GameState restored = new GameState(testServer.getServer());
		restored.initFrom(restored.getGame().getRules(), snapshot);
		return restored;
	}
}
