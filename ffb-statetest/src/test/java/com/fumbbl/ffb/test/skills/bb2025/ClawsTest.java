package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ClawsTest extends AbstractStateTest {

	@Test
	void reducesArmourForHighAV() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Claws")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 9).skill("+AV")))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pow").armour(3, 5).injury(3, 2);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));

		assertFalse(state.getGame().getFieldModel().getPlayerState(
						state.getGame().getPlayerById("away1")).isStanding(),
				"Claws reduces armour for AV9, armor roll 8 should break");
	}

	@Test
	void clawsKeepsArmourAtSevenPlusForLowAV() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Claws")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 7)))
				.build();
		this.gameState = state;

		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pow").armour(3, 3);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));

		assertFalse(state.getGame().getFieldModel().getPlayerState(
						state.getGame().getPlayerById("away1")).isStanding(),
				"Claws should keep AV7, 3+3=6 should break");
	}

	@Test
	void clawsCapsAV10At8Plus() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Claws")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 10)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pow").armour(4, 4).injury(3, 2);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		assertFalse(state.getGame().getFieldModel().getPlayerState(
						state.getGame().getPlayerById("away1")).isStanding(),
				"Claws caps AV 10 at 8+, armor(4,4)=8 breaks capped armor");
	}

	@Test
	void clawsAndMightyBlowStacking() {
		GameState state = new GameStateBuilder(testServer.getGameState())
				.withRule("BB2025")
				.withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)
						.skill("Claws").skill("Mighty Blow")))
				.withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 10)))
				.build();
		this.gameState = state;
		StepEngine.start(state);
		StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
		TestRolls.on(state).block("pow").armour(4, 3);
		StepEngine.respond(state, Commands.block("home1", "away1"));
		StepEngine.respond(state, Commands.blockChoice(0));
		StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
		StepEngine.respond(state, Commands.followup(false));
		assertFalse(state.getGame().getFieldModel().getPlayerState(
						state.getGame().getPlayerById("away1")).isStanding(),
				"MB +1 armor roll (4+3+1=8), Claws caps AV at 8+, 8 breaks capped armor");
	}
}
