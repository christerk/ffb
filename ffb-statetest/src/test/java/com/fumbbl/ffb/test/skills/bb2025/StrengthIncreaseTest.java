package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.Pushback;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.IStep;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StrengthIncreaseTest extends AbstractStateTest {

    @Test
    void plusStrengthGivesExtraBlockDice() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("+ST")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));

        assertEquals(StepId.PUSHBACK, step.getId(), "+ST STR5 vs STR4 should be 2D, pushback works");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(
                state.getGame().getPlayerById("away1")).isStanding());
    }

    @Test
    void twoPlusSTStacksForHigherStrength() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("+ST").skill("+ST")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 5, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback", "pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));

        assertEquals(StepId.PUSHBACK, step.getId(), "Two +ST STR 5 vs STR 5 should be 2D, pushback works");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Defender should be standing after pushback when two +ST stacks");
    }

    @Test
    void strengthIncreaseVsDauntlessDauntlessCanMatchIncreasedStrength() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Dauntless")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 5, 3, 5, 8).skill("+ST")))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).dauntless(4).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertEquals(StepId.PUSHBACK, step.getId(),
                "Dauntless matching increased strength should result in equal strength block");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Defender (away1 with +ST) should be standing after pushback even when Dauntless matches strength");
    }

    @Test
    void strengthIncreaseBlockDiceDistributionSt4VsSt3() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("+ST")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback", "pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));

        assertEquals(StepId.PUSHBACK, step.getId(), "+ST STR4 vs STR3 should be 2D");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Defender should be standing after pushback when attacker has +ST advantage");
    }

    @Test
    void strengthIncreasePlusGuardAssistStackingOnBlock() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("+ST")).player("home2", p -> p.at(7, 6).stats(6, 3, 3, 5, 8).skill("Guard")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 4, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback", "pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));
        assertNotNull(step);
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Defender should be standing after pushback with +ST and Guard assist stacking");
    }

    @Test
    void strengthIncreaseStacksLimitedByPositionMaxAndGlobalMax() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 4, 3, 5, 8).skill("+ST").skill("+ST").skill("+ST")))
                .withTeam(false, t -> t.player("away1", p -> p.at(8, 7).stats(6, 6, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));
        IStep step = StepEngine.respond(state, Commands.blockChoice(0));

        assertEquals(StepId.PUSHBACK, step.getId(),
                "Three +ST skills are capped at min(position strength + 2, 8) = 6; the single block die resolves as an equal-strength block vs STR6");
        StepEngine.respond(state, Commands.pushback(new Pushback("away1", new FieldCoordinate(9, 7))));
        StepEngine.respond(state, Commands.followup(false));

        assertTrue(state.getGame().getFieldModel().getPlayerState(state.getGame().getPlayerById("away1")).isStanding(),
                "Defender should be standing after pushback when the +ST stack is capped");
    }
}
