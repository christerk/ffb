package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrushingBlowTest extends AbstractStateTest {

	@Test
	public void crushingBlowBreaksArmourAtThreshold() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Crushing Blow")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state)
				.block("pow")
				.armour(5, 2)
				.injury(1, 1);

		StepEngine.respond(state, Commands.block("home1", "away1"));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		assertNotNull(step);
		assertEquals(StepId.PUSHBACK, step.getId());

		step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		assertNotNull(step);

		StepEngine.respond(state, Commands.followup(false));

		Game game = state.getGame();
		PlayerState defenderState = game.getFieldModel().getPlayerState(game.getPlayerById("away1"));
		assertFalse(defenderState.isStanding(),
				"Expected defender down after Crushing Blow (+1 armor) breaks armor, was " + defenderState.getBase());
	}

	@Test
	public void crushingBlowCannotUseTwicePerGame() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Crushing Blow")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

		TestRolls.on(state)
				.block("pow")
				.armour(5, 2)
				.injury(1, 1);

		StepEngine.respond(state, Commands.block("home1", "away1"));

		IStep step = StepEngine.respond(state, Commands.blockChoice(0));
		assertNotNull(step);

		step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		assertNotNull(step);

		StepEngine.respond(state, Commands.followup(false));

		assertNotNull(state.getCurrentStep(),
				"Crushing Blow cannot be used twice per game - game should be in valid state after use");
	}

	@Test
	public void crushingBlowPlusOneArmorVsAV9() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Crushing Blow")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 9)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pow").armour(4, 3);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		Game game = state.getGame();
		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
				"Defender knocked down by POW result; armor 4+3+1(Crushing Blow)=8, 8<9 AV holds but POW still knocks down");
	}

	@Test
	public void crushingBlowConsumedAndInactiveOnSecondBlock() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Crushing Blow")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pow").armour(5, 2).injury(3, 2);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		assertNotNull(state.getCurrentStep(),
				"Crushing Blow consumed and inactive on second block - game should be in valid state");
	}
}
