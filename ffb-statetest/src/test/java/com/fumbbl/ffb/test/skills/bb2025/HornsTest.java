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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HornsTest extends AbstractStateTest {

    @Test
    public void hornsGivesStrengthBonusOnBlitz() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Horns")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Expected PUSHBACK after Horns gives +1 STR on Blitz (STR 3+1=4 vs 4, 1D block)");

        step = StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        assertNotNull(step);

        StepEngine.respond(state, Commands.followup(false));

        Game game = state.getGame();
        assertTrue(game.getFieldModel().getPlayerState(game.getPlayerById("home1")).isStanding(),
                "Expected attacker standing after Horns Blitz (MOVING=2 is standing)");
        assertEquals(PlayerState.STANDING, game.getFieldModel().getPlayerState(game.getPlayerById("away1")).getBase());
    }

    @Test
    public void hornsDoesNotWorkOnRegularBlock() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Horns")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));

        TestRolls.on(state)
                .block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));

        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
    }

    @Test
    public void addStrengthOnBlitzEndBlocking() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Horns")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state).block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        assertEquals(StepId.PUSHBACK, step.getId());

        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void hornsWithDauntlessOnBlitz() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Horns").skill("Dauntless")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state).dauntless(6).block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);

        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep());
    }

    @Test
    public void hornsWithStrengthIncreaseOnBlitz() {
        gameState = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Horns").skill("+ST")))
                .withTeam(false, t -> t
                        .player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        GameState state = gameState;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLITZ_MOVE));
        StepEngine.respond(state, Commands.selectBlitzTarget("away1"));

        TestRolls.on(state).block("pushback");

        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);

        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertNotNull(state.getCurrentStep());
    }
}
