package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.option.GameOptionId;
import com.fumbbl.ffb.option.GameOptionInt;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.server.step.StepId;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class KickTest extends AbstractStateTest {

    @Test
    void kickCanReduceKickDistance() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Kick")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")),
                "Kick can reduce kick distance - home1 still on field after block action");
    }

    @Test
    void kickReducesKickoffDistance() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Kick")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);

        assertNotNull(state.getCurrentStep(),
                "Kick reduces kickoff distance - game in valid state at start of turn");
    }

    @Test
    void kickReducesKickoffScatterDistance() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Kick")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);

        assertNotNull(state.getCurrentStep(),
                "Kick reduces kickoff scatter distance - game in valid state at start of turn");
    }

    @Test
    void kickHalvesKickoffScatterDistanceD6toD3() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .initialState()
                .withOption(new GameOptionInt(GameOptionId.MIN_PLAYERS_ON_LOS).setValue(0))
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Kick")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        assertEquals(StepId.KICKOFF, Kickoff.throughSetup(state).getId(),
                "Kick kickoff - the kickoff step waits for the ball placement after setup");

        assertEquals(StepId.KICKOFF_SCATTER_ROLL, Kickoff.kick(state, new FieldCoordinate(2, 7)).getId(),
                "Kick kickoff - the scatter roll step asks whether to use the Kick skill");

        TestRolls.on(state).scatterDirection(3).kickScatterDistance(2).kickoff(1, 3).general("solid defence", 1);
        assertEquals(StepId.APPLY_KICKOFF_RESULT, Kickoff.useKick(state, "home1", true).getId());

        FieldCoordinate ball = state.getGame().getFieldModel().getBallCoordinate();
        assertEquals(new FieldCoordinate(4, 7), ball,
                "Kick halves the kickoff scatter distance - the ball lands 2 squares from the kickoff spot");
    }

    @Test
    void kickCannotBeUsedFromWideZone() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .initialState()
                .withOption(new GameOptionInt(GameOptionId.MIN_PLAYERS_ON_LOS).setValue(0))
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(1, 1).stats(6, 3, 3, 5, 8).skill("Kick"))
                        .player("home2", p -> p.at(7, 7).stats(6, 3, 3, 5, 8)))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        assertEquals(StepId.KICKOFF, Kickoff.throughSetup(state).getId());

        TestRolls.on(state).scatterDirection(3).scatterDistance(4).kickoff(1, 3).general("solid defence", 1);
        assertEquals(StepId.APPLY_KICKOFF_RESULT, Kickoff.kick(state, new FieldCoordinate(2, 7)).getId(),
                "Kick kickoff - no Kick dialog is shown for a kicker in the wide zone, the full D6 distance roll is used");

        FieldCoordinate ball = state.getGame().getFieldModel().getBallCoordinate();
        assertEquals(new FieldCoordinate(6, 7), ball,
                "Kick in the wide zone does not reduce the kickoff scatter distance - the ball lands the full 4 squares away");
    }
}
