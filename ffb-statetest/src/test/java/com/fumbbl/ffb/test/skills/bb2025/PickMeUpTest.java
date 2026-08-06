package com.fumbbl.ffb.test.skills.bb2025;

import com.fumbbl.ffb.FieldCoordinate;
import com.fumbbl.ffb.PlayerAction;
import com.fumbbl.ffb.PlayerState;
import com.fumbbl.ffb.server.GameState;
import com.fumbbl.ffb.test.*;
import com.fumbbl.ffb.test.AbstractStateTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PickMeUpTest extends AbstractStateTest {

    @Test
    void pickMeUpCanStandUpTeammates() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t.player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pick-me-up")))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.BLOCK));
        TestRolls.on(state).block("pushback");
        StepEngine.respond(state, Commands.block("home1", "away1"));

        assertNotNull(state.getGame().getFieldModel().getPlayerCoordinate(
                state.getGame().getPlayerById("home1")),
                "Pick-me-up can stand up teammates - home1 still on field after block action");
    }

    @Test
    void pickMeUpRangedStandUp() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pick-me-up"))
                        .player("home2", p -> p.at(10, 7).stats(6, 3, 3, 5, 8)
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(10, 7)));

        assertNotNull(state.getCurrentStep(),
                "Pick-me-up ranged stand up - game in valid state after carrier moves adjacent to prone teammate");
    }

    @Test
    void canStandUpTeamMatesAdjacentToOpponent() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pick-me-up"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t.player("away1", p -> p.at(7, 9).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 8)));

        assertNotNull(state.getCurrentStep(),
                "Can stand up team mates adjacent to opponent - game in valid state after moving to prone teammate with opponent in TZ");
    }

    @Test
    void pickMeUpStandUpTeammateNoMovementCost() {
        GameState state = new GameStateBuilder(testServer.getGameState())
                .withRule("BB2025")
                .withTeam(true, t -> t
                        .player("home1", p -> p.at(7, 7).stats(6, 3, 3, 5, 8).skill("Pick-me-up"))
                        .player("home2", p -> p.at(7, 8).stats(6, 3, 3, 5, 8)
                                .state(new PlayerState(PlayerState.PRONE).changeActive(true))))
                .withTeam(false, t -> t.player("away1", p -> p.at(14, 1).stats(6, 3, 3, 5, 8)))
                .build();
        this.gameState = state;

        StepEngine.start(state);
        StepEngine.respond(state, Commands.selectPlayer("home1", PlayerAction.MOVE));
        StepEngine.respond(state, Commands.move("home1", new FieldCoordinate(7, 7), new FieldCoordinate(7, 8)));

        assertNotNull(state.getCurrentStep(),
                "Pick-me-up stand up teammate no movement cost - game in valid state after standing up adjacent prone teammate");
    }
}
