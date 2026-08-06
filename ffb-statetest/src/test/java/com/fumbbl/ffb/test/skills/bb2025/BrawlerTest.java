package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogBlockRollPropertiesParameter;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BrawlerTest extends AbstractStateTest {

	@Test
	void brawlerAllowsSingleBothDownReroll() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Brawler")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("bothdown", "pushback").block("pushback");
		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected block roll properties dialog after single BothDown");
		assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId(), "Expected BLOCK_ROLL_PROPERTIES dialog");

		DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
		assertTrue(blockDialog.getRrActionToSource().containsKey("Single BothDown"),
				"Brawler reroll option should be offered for a single BothDown");

		StepEngine.respond(state, Commands.brawler("home1"));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		assertNotNull(step);

		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"Attacker should be standing after Brawler rerolls BothDown to Pushback");
	}

	@Test
	void brawlerInactiveOnBlitz() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("Brawler")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));

		IDialogParameter dialog = state.getGame().getDialogParameter();
		assertNotNull(dialog, "Expected blitz target selection dialog");
		assertEquals(DialogId.SELECT_BLITZ_TARGET, dialog.getId());

		StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

		TestRolls.on(state).block("bothdown", "pushback").armour(1, 1).armour(1, 1);
		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected block roll properties dialog on Blitz");
		assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());

		DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
		assertFalse(blockDialog.getRrActionToSource().containsKey("Single BothDown"),
				"Brawler should NOT be offered on Blitz");

		StepEngine.respond(state, Commands.blockChoice(0));

		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"Attacker should be down from BothDown on Blitz (Brawler not active)");
	}

	@Test
	void brawlerDoesNotPreventDoubleBothDownKnockdown() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Brawler")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 2, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("bothdown", "bothdown").block("pushback").armour(1, 1).armour(1, 1);
		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected block roll properties dialog");
		assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());

		DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
		assertTrue(blockDialog.getRrActionToSource().containsKey("Single BothDown"),
				"Brawler reroll option is offered (rerolls a single BothDown die)");

		StepEngine.respond(state, Commands.brawler("home1"));
		StepEngine.respond(state, Commands.blockChoice(1));

		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"Attacker is still down when a second BothDown remains after the Brawler reroll");
	}

	@Test
	void brawlerDeclinesRerollOption() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Brawler")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("bothdown", "pushback").armour(1, 1).armour(1, 1);
		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected block roll properties dialog after single BothDown");
		assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());

		StepEngine.respond(state, Commands.blockChoice(0));

		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"Attacker should be down when Brawler is declined");
	}

	@Test
	void brawlerOn3dBlockWithSingleBothDown() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 5, 3, 5, 8).skill("Brawler")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 2, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("bothdown", "stumble", "stumble").block("pushback");
		StepEngine.respond(state, Commands.block("home1", "away1"));

		Game game = state.getGame();
		IDialogParameter dialog = game.getDialogParameter();
		assertNotNull(dialog, "Expected block roll properties dialog on 3D block with single BothDown");
		assertEquals(DialogId.BLOCK_ROLL_PROPERTIES, dialog.getId());

		DialogBlockRollPropertiesParameter blockDialog = (DialogBlockRollPropertiesParameter) dialog;
		assertTrue(blockDialog.getRrActionToSource().containsKey("Single BothDown"),
				"Brawler reroll option should be offered on 3D block with a single BothDown");

		StepEngine.respond(state, Commands.brawler("home1"));

		StepEngine.respond(state, Commands.blockChoice(0));

		assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
				"Attacker should be standing after Brawler reroll on 3D block");
	}
}
