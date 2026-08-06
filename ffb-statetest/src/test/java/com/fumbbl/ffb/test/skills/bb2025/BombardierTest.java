package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.*;
import com.fumbbl.ffb.dialog.DialogId;
import com.fumbbl.ffb.model.ActingPlayer;
import com.fumbbl.ffb.model.Game;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BombardierTest extends AbstractStateTest {

	@Test
	void throwBombActionWorks() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
				state.getGame().getPlayerById("home1")),
				"Bombardier should be on the field after selecting THROW_BOMB action");
	}

	@Test
	void bombTargetsSquareAndExplodes() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		TestRolls.on(state).skill(4).scatterDirection(6).scatterDirection(6).scatterDirection(6).scatterDirection(6);

		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

		assertNotNull(state.getCurrentStep(),
				"Bomb targets square and explodes - game should be in valid state after explosion");
	}

	@Test
	void bombFumblesOnNaturalOne() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		TestRolls.on(state).skill(1).armour(3, 3);

		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

		Game game = state.getGame();
		PlayerState throwerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
		assertFalse(throwerState.isStanding(),
				"Bomb fumble on natural 1 knocks the thrower down, even when armour holds");
	}

	@Test
	void bombHitsAdjacentPlayersOnExplosion() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier")))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(10, 6).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		TestRolls.on(state)
				.skill(5).skill(2)
				.wizardSpell(5).armour(6, 5).injury(3, 1)
				.armour(6, 6).injury(3, 2);

		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

		Game game = state.getGame();
		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away2")).isStanding(),
				"away2 in target square should be stunned from direct hit");
		assertFalse(game.getFieldModel().getPlayerState(game.getPlayerById("away1")).isStanding(),
				"away1 adjacent should be stunned from adjacent hit");
	}

	@Test
	void bombScattersOnInaccurateThrow() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		TestRolls.on(state).skill(4).scatterDirection(1).scatterDirection(3).scatterDirection(5).scatterDirection(6);

		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

		assertNotNull(state.getCurrentStep(),
				"Bomb scatters on inaccurate throw - game should be in valid state after scatter");
	}

	@Test
	void bombFumbleHitsThrower() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier")))
				.withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		TestRolls.on(state)
				.skill(1)
				.armour(6, 6)
				.injury(3, 2);

		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(10, 7)));

		Game game = state.getGame();
		PlayerState throwerState = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
		assertFalse(throwerState.isStanding(),
				"Expected bombardier to be hit by own fumbled bomb, was " + throwerState.getBase());
	}

	@Test
	void bombardierNotReactivatedAfterBombCaughtAndRethrown() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withWeather(Weather.NICE)
				.withTeam(true, t -> t
						.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Bombardier"))
						.player("home2", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t
						.player("away1", p -> p.at(14, 7).stats(6, 3, 3, 5, 8))
						.player("away2", p -> p.at(1, 1).stats(6, 3, 3, 5, 8)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.THROW_BOMB));

		TestRolls.on(state)
				.skill(4)
				.scatterDirection(6)
				.scatterDirection(6)
				.scatterDirection(6)
				.scatterDirection(6);

		StepEngine.respond(state, Commands.pass("home1", new FieldCoordinate(14, 7)));

		Game game = state.getGame();

		IDialogParameter dialog = game.getDialogParameter();
		while (dialog != null) {
			if (dialog.getId() == DialogId.INTERCEPTION) {
				StepEngine.respond(state, Commands.interceptorChoice("away1"));
			}
			dialog = game.getDialogParameter();
		}

		ActingPlayer actingPlayer = game.getActingPlayer();
		if ("away1".equals(actingPlayer.getPlayerId())) {
			TestRolls.on(state).skill(4);
			StepEngine.respond(state, Commands.pass("away1", new FieldCoordinate(1, 1)));
		}

		dialog = game.getDialogParameter();
		while (dialog != null) {
			if (dialog.getId() == DialogId.INTERCEPTION) {
				StepEngine.respond(state, Commands.interceptorChoice("home2"));
			}
			dialog = game.getDialogParameter();
		}

		actingPlayer = game.getActingPlayer();
		PlayerState home1State = game.getFieldModel().getPlayerState(game.getPlayerById("home1"));
		assertFalse(home1State.isActive(),
				"BUG: home1 is still active after bomb throw. The bombardier was not properly "
						+ "marked as having activated and could be selected for another action.");
	}
}
