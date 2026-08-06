package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DecayTest extends AbstractStateTest {

	@Test
	public void casualtyModifier() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Decay")))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pow").armour(6, 6).injury(3, 4);
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertNotNull(g.getGame().getFieldModel().getPlayerCoordinate(g.getGame().getPlayerById("a1")));
	}

	@Test
	public void decayAndRegenerationInteraction() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Decay")
						.skill("Regeneration")))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pow").armour(6, 6).injury(3, 4);
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertNotNull(g.getCurrentStep());
	}

	@Test
	public void decayCasualtyModifierPushesInjury9ToCasualty10() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Decay")))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pow").armour(6, 6).injury(5, 4);
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertNotNull(g.getCurrentStep());
	}

	@Test
	public void decayCancelsRegenerationSaveFromInjury() {
		gameState = new GameStateBuilder(testServer.getGameState()).withRule("BB2025")
				.withTeam(true, t -> t.player("h1", p -> p.at(7, 7)
						.stats(6, 3, 3, 5, 8)))
				.withTeam(false, t -> t.player("a1", p -> p.at(8, 7)
						.stats(6, 3, 3, 5, 8)
						.skill("Decay")
						.skill("Regeneration")))
				.build();
		GameState g = gameState;
		StepEngine.start(g);
		StepEngine.respond(g, Commands.selectPlayer("h1", PlayerAction.BLOCK));
		TestRolls.on(g).block("pow").armour(6, 6).injury(6, 5).casualty(1, 1).skill(2);
		StepEngine.respond(g, Commands.block("h1", "a1"));
		StepEngine.respond(g, Commands.blockChoice(0));
		StepEngine.respond(g, Commands.pushback(new Pushback("a1", new FieldCoordinate(9, 7))));
		StepEngine.respond(g, Commands.followup(false));
		assertNotNull(g.getCurrentStep());
	}
}
